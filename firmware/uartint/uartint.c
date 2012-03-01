#include "lpc17xx_uart.h"
#include "lpc17xx_libcfg.h"
#include "lpc17xx_pinsel.h"

/************************** PRIVATE DEFINTIONS *************************/
/* buffer size definition */
#define UART_RING_BUFSIZE (1<<8)

/* Buf mask */
#define __BUF_MASK (UART_RING_BUFSIZE-1)
/* Check buf is full or not */
#define __BUF_IS_FULL(head, tail) ((tail&__BUF_MASK)==((head+1)&__BUF_MASK))
/* Check buf will be full in next receiving or not */
#define __BUF_WILL_FULL(head, tail) ((tail&__BUF_MASK)==((head+2)&__BUF_MASK))
/* Check buf is empty */
#define __BUF_IS_EMPTY(head, tail) ((head&__BUF_MASK)==(tail&__BUF_MASK))
/* Reset buf */
#define __BUF_RESET(bufidx)	(bufidx=0)
#define __BUF_INCR(bufidx)	(bufidx=(bufidx+1)&__BUF_MASK)


/************************** PRIVATE TYPES *************************/

/** @brief UART Ring buffer structure */
typedef struct {
  volatile uint32_t tx_head;                /*!< UART Tx ring buffer head index */
  volatile uint32_t tx_tail;                /*!< UART Tx ring buffer tail index */
  volatile uint32_t rx_head;                /*!< UART Rx ring buffer head index */
  volatile uint32_t rx_tail;                /*!< UART Rx ring buffer tail index */
  volatile uint8_t  tx[UART_RING_BUFSIZE];  /*!< UART Tx data ring buffer */
  volatile uint8_t  rx[UART_RING_BUFSIZE];  /*!< UART Rx data ring buffer */
  volatile FlagStatus txInterrupt;
  LPC_UART_TypeDef *uart;
} UartRingBuffer;


// UART Ring buffer
UartRingBuffer uart0;


/************************** PRIVATE FUNCTIONS *************************/
/* Interrupt service routines */
void UART0_IRQHandler(void);

// Internal functions called from the IRQ
void UART_init(UartRingBuffer *ub, LPC_UART_TypeDef *uart);
void UART_IntErr(UartRingBuffer *ub, uint8_t bLSErrType);
void UART_IntTransmit(UartRingBuffer *ub);
void UART_IntReceive(UartRingBuffer *ub);

// API functions
uint32_t UARTReceive(UartRingBuffer *ub, uint8_t *rxbuf, uint8_t buflen);
uint32_t UARTSend(   UartRingBuffer *ub, uint8_t *txbuf, uint8_t buflen);


/*----------------- INTERRUPT SERVICE ROUTINES --------------------------*/
/*********************************************************************//**
 * @brief		UART0 interrupt handler sub-routine
 * @param[in]	None
 * @return 		None
 **********************************************************************/
void UART0_IRQHandler(void) {
   /* Determine the interrupt source */
   uint32_t intsrc = UART_GetIntId(LPC_UART0);
   uint32_t tmp = intsrc & UART_IIR_INTID_MASK;

   // Receive Line Status
   if (tmp == UART_IIR_INTID_RLS){
     // Check line status
     uint32_t tmp1 = UART_GetLineStatus(LPC_UART0);
     // Mask out the Receive Ready and Transmit Holding empty status
     tmp1 &= (UART_LSR_OE | UART_LSR_PE | UART_LSR_FE | UART_LSR_BI | UART_LSR_RXFE);
     // If any error exist
     if (tmp1) {
       UART_IntErr(&uart0, tmp1);
     }
   }
   
   // Receive Data Available or Character time-out
   if ((tmp == UART_IIR_INTID_RDA) || (tmp == UART_IIR_INTID_CTI)){
     UART_IntReceive(&uart0);
   }

   // Transmit Holding Empty
   if (tmp == UART_IIR_INTID_THRE){
     UART_IntTransmit(&uart0);
   }   
}

/********************************************************************//**
 * @brief 		UART receive function (ring buffer used)
 * @param[in]	None
 * @return 		None
 *********************************************************************/
void UART_IntReceive(UartRingBuffer *ub) {
  while (1) {
    // Call UART read function in UART driver
    uint8_t tmpc;
    uint32_t rLen = UART_Receive(ub->uart, &tmpc, 1, NONE_BLOCKING);
    // If data received
    if (rLen){
      /* Check if buffer has more space
       * If no more space, remaining character will be trimmed out
       */
      if (!__BUF_IS_FULL(ub->rx_head,ub->rx_tail)) {
	ub->rx[ub->rx_head] = tmpc;
	__BUF_INCR(ub->rx_head);
      }
    } else { // no more data
      break;
    }
  }
}

/********************************************************************//**
 * @brief 		UART transmit function (ring buffer used)
 * @param[in]	None
 * @return 		None
 *********************************************************************/
void UART_IntTransmit(UartRingBuffer *ub) {
  // Disable THRE interrupt
  UART_IntConfig(ub->uart, UART_INTCFG_THRE, DISABLE);

  /* Wait for FIFO buffer empty, transfer UART_TX_FIFO_SIZE bytes
   * of data or break whenever ring buffers are empty */
  /* Wait until THR empty */
  while (UART_CheckBusy(ub->uart) == SET);

  while (!__BUF_IS_EMPTY(ub->tx_head,ub->tx_tail)) {
    /* Move a piece of data into the transmit FIFO */
    if (UART_Send(ub->uart, (uint8_t *)&ub->tx[ub->tx_tail], 1, NONE_BLOCKING)){
      /* Update transmit ring FIFO tail pointer */
      __BUF_INCR(ub->tx_tail);
    } else {
      break;
    }
  }

  /* If there is no more data to send, disable the transmit
     interrupt - else enable it or keep it enabled */
  if (__BUF_IS_EMPTY(ub->tx_head, ub->tx_tail)) {
    UART_IntConfig(ub->uart, UART_INTCFG_THRE, DISABLE);
    // Reset Tx Interrupt state
    ub->txInterrupt = RESET;
  } else {
    // Set Tx Interrupt state
    ub->txInterrupt = SET;
    UART_IntConfig(ub->uart, UART_INTCFG_THRE, ENABLE);
  }
}


/*********************************************************************//**
 * @brief		UART Line Status Error
 * @param[in]	bLSErrType	UART Line Status Error Type
 * @return		None
 **********************************************************************/
void UART_IntErr(UartRingBuffer *ub, uint8_t bLSErrType) {
  // Loop forever...
  while (1) { }
}

/*-------------------------PRIVATE FUNCTIONS------------------------------*/
/*********************************************************************//**
 * @brief		UART transmit function for interrupt mode (using ring buffers)
 * @param[in]	UARTPort	Selected UART peripheral used to send data,
 * 				should be UART0
 * @param[out]	txbuf Pointer to Transmit buffer
 * @param[in]	buflen Length of Transmit buffer
 * @return 		Number of bytes actually sent to the ring buffer
 **********************************************************************/
uint32_t UARTSend(UartRingBuffer *ub, uint8_t *txbuf, uint8_t buflen)
{
  uint8_t *data = (uint8_t *) txbuf;
  uint32_t bytes = 0;
  
  /* Temporarily lock out UART transmit interrupts during this
     read so the UART transmit interrupt won't cause problems
     with the index values */
  UART_IntConfig(ub->uart, UART_INTCFG_THRE, DISABLE);
  
  /* Loop until transmit run buffer is full or until n_bytes
     expires */
  while ((buflen > 0) && (!__BUF_IS_FULL(ub->tx_head, ub->tx_tail))) {
    /* Write data from buffer into ring buffer */
    ub->tx[ub->tx_head] = *data;
    data++;
    
    /* Increment head pointer */
    __BUF_INCR(ub->tx_head);
    
    /* Increment data count and decrement buffer size count */
    bytes++;
    buflen--;
  }
  
  /*
   * Check if current Tx interrupt enable is reset,
   * that means the Tx interrupt must be re-enabled
   * due to call UART_IntTransmit() function to trigger
   * this interrupt type
   */
  if (txInterrupt == RESET) {
    UART_IntTransmit();

  } else {
    /*
     * Otherwise, re-enables Tx Interrupt
     */
    UART_IntConfig(ub->uart, UART_INTCFG_THRE, ENABLE);
  }
  
  return bytes;
}


/*********************************************************************//**
 * @brief		UART read function for interrupt mode (using ring buffers)
 * @param[in]	UARTPort	Selected UART peripheral used to send data,
 * 				should be UART0
 * @param[out]	rxbuf Pointer to Received buffer
 * @param[in]	buflen Length of Received buffer
 * @return 		Number of bytes actually read from the ring buffer
 **********************************************************************/
uint32_t UARTReceive(UartRingBuffer *ub, uint8_t *rxbuf, uint8_t buflen) {
  uint8_t *data = (uint8_t *) rxbuf;
  uint32_t bytes = 0;
  
  /* Temporarily lock out UART receive interrupts during this
     read so the UART receive interrupt won't cause problems
     with the index values */
  UART_IntConfig(ub->uart, UART_INTCFG_RBR, DISABLE);
  
  /* Loop until receive buffer ring is empty or until max_bytes expires */
  while ((buflen > 0) && (!(__BUF_IS_EMPTY(ub->rx_head, ub->rx_tail)))) {
    /* Read data from ring buffer into user buffer */
    *data = ub->rx[ub->rx_tail];
    data++;

    __BUF_INCR(ub->rx_tail);
    
    bytes++;
    buflen--;
  }
  
  /* Re-enable UART interrupts */
  UART_IntConfig(ub->uart, UART_INTCFG_RBR, ENABLE);
  
  return bytes;
}

/*-------------------------MAIN FUNCTION------------------------------*/
/*********************************************************************//**
 * @brief		c_entry: Main UART program body
 * @param[in]	None
 * @return 		int
 **********************************************************************/
int main(void) {
  // UART Configuration structure variable
  UART_CFG_Type UARTConfigStruct;
  // UART FIFO configuration Struct variable
  UART_FIFO_CFG_Type UARTFIFOConfigStruct;
  // Pin configuration for UART0
  PINSEL_CFG_Type PinCfg;
  
  uint32_t idx, len;
  volatile FlagStatus exitflag;
  uint8_t buffer[10];
  
  /*
   * Initialize UART0 pin connect
   */
  PinCfg.Funcnum = 1;
  PinCfg.OpenDrain = 0;
  PinCfg.Pinmode = 0;
  PinCfg.Pinnum = 2;
  PinCfg.Portnum = 0;
  PINSEL_ConfigPin(&PinCfg);
  PinCfg.Pinnum = 3;
  PINSEL_ConfigPin(&PinCfg);
  
  
  /* Initialize UART Configuration parameter structure to default state:
   * Baudrate = 9600bps
   * 8 data bit
   * 1 Stop bit
   * None parity
   */
  UART_ConfigStructInit(&UARTConfigStruct);
  
  // Initialize UART0 peripheral with given to corresponding parameter
  UART_Init((LPC_UART_TypeDef *)LPC_UART0, &UARTConfigStruct);
  
  
  /* Initialize FIFOConfigStruct to default state:
   * 				- FIFO_DMAMode = DISABLE
   * 				- FIFO_Level = UART_FIFO_TRGLEV0
   * 				- FIFO_ResetRxBuf = ENABLE
   * 				- FIFO_ResetTxBuf = ENABLE
   * 				- FIFO_State = ENABLE
   */
  UART_FIFOConfigStructInit(&UARTFIFOConfigStruct);
  
  // Initialize FIFO for UART0 peripheral
  UART_FIFOConfig((LPC_UART_TypeDef *)LPC_UART0, &UARTFIFOConfigStruct);
  
  
  // Enable UART Transmit
  UART_TxCmd((LPC_UART_TypeDef *)LPC_UART0, ENABLE);
  
  /* Enable UART Rx interrupt */
  UART_IntConfig((LPC_UART_TypeDef *)LPC_UART0, UART_INTCFG_RBR, ENABLE);
  /* Enable UART line status interrupt */
  UART_IntConfig((LPC_UART_TypeDef *)LPC_UART0, UART_INTCFG_RLS, ENABLE);
  /*
   * Do not enable transmit interrupt here, since it is handled by
   * UART_Send() function, just to reset Tx Interrupt state for the
   * first time
   */
  txInterrupt = RESET;
  
  // Reset ring buf head and tail idx
  __BUF_RESET(ub->rx_head);
  __BUF_RESET(ub->rx_tail);
  __BUF_RESET(ub->tx_head);
  __BUF_RESET(ub->tx_tail);
  
  /* preemption = 1, sub-priority = 1 */
  NVIC_SetPriority(UART0_IRQn, ((0x01<<3)|0x01));
  /* Enable Interrupt for UART0 channel */
  NVIC_EnableIRQ(UART0_IRQn);
  
  // reset exit flag
  exitflag = RESET;
  
  /* Read some data from the buffer */
  while (exitflag == RESET) {
    len = 0;
    while (len == 0) {
      len = UARTReceive((LPC_UART_TypeDef *)LPC_UART0, buffer, sizeof(buffer));
    }
    
    /* Got some data */
    idx = 0;
    while (idx < len) {
      if (buffer[idx] == 27) {
	/* ESC key, set exit flag */
	UARTSend((LPC_UART_TypeDef *)LPC_UART0, menu3, sizeof(menu3));
	exitflag = SET;
      } else if (buffer[idx] == 'r') {
	//print_menu();
      } else {
	/* Echo it back */
	UARTSend((LPC_UART_TypeDef *)LPC_UART0, &buffer[idx], 1);
      }
      idx++;
    }
  }
  
  // wait for current transmission complete - THR must be empty
  while (UART_CheckBusy((LPC_UART_TypeDef *)LPC_UART0));
  
  // DeInitialize UART0 peripheral
  UART_DeInit((LPC_UART_TypeDef *)LPC_UART0);
  
  /* Loop forever */
  while(1);
  return 1;
}

