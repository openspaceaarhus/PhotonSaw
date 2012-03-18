#include "board.h"
#include "uarts.h"

/* buffer size for each ring buffer, each used UART takes two of these */
#define UART_RING_BUFSIZE (1<<8)

// UART Ring buffer structure
typedef struct {
  volatile uint32_t tx_head;                /* UART Tx ring buffer head index */
  volatile uint32_t tx_tail;                /* UART Tx ring buffer tail index */
  volatile uint32_t rx_head;                /* UART Rx ring buffer head index */
  volatile uint32_t rx_tail;                /* UART Rx ring buffer tail index */
  volatile uint8_t  tx[UART_RING_BUFSIZE];  /* UART Tx data ring buffer */
  volatile uint8_t  rx[UART_RING_BUFSIZE];  /* UART Rx data ring buffer */
  volatile FlagStatus txInterrupt;
  LPC_UART_TypeDef *uart;
  unsigned int error;
} UartRingBuffer;

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

#ifdef USE_UART0
UartRingBuffer uart0;
#endif
#ifdef USE_UART1
UartRingBuffer uart1;
#endif
#ifdef USE_UART2
UartRingBuffer uart2;
#endif
#ifdef USE_UART3
UartRingBuffer uart3;
#endif

UartRingBuffer *UART_BUFFER[4] = {
#ifdef USE_UART0
  [0] = &uart0,
#endif
#ifdef USE_UART1
  [1] = &uart1,
#endif
#ifdef USE_UART2
  [2] = &uart2,
#endif
#ifdef USE_UART3
  [3] = &uart3,
#endif
};

LPC_UART_TypeDef* UARTS[] = {LPC_UART0, (LPC_UART_TypeDef *)LPC_UART1, LPC_UART2, LPC_UART3};

// Perhaps this should live in the board-config.h file in stead?
uint32_t BAUD[] = { 
  115200, // Debug
  9600,   // Not in use
  9600,   // Chiller
  19200   // Watchdog
};


// Configures a pair of pins to use as an UART
void configUart(const uint32_t txpin, const uint32_t rxpin) {
  const int uartNumber = IO_CHAN(txpin);
  LPC_UART_TypeDef *uart = UARTS[uartNumber];
  UartRingBuffer *ub = UART_BUFFER[uartNumber];

  UART_CFG_Type uartConfig;
  UART_ConfigStructInit(&uartConfig);
  uartConfig.Baud_rate = BAUD[uartNumber];
  UART_Init(uart, &uartConfig);

  /* Initialize FIFOConfigStruct to default state:
   * 				- FIFO_DMAMode = DISABLE
   * 				- FIFO_Level = UART_FIFO_TRGLEV0
   * 				- FIFO_ResetRxBuf = ENABLE
   * 				- FIFO_ResetTxBuf = ENABLE
   * 				- FIFO_State = ENABLE
   */
  UART_FIFO_CFG_Type fifoConfig;
  UART_FIFOConfigStructInit(&fifoConfig);
  UART_FIFOConfig(uart, &fifoConfig);
  UART_TxCmd(uart, ENABLE);

  ub->error = 0;
  ub->uart = uart;

  /*
   * Do not enable transmit interrupt here, since it is handled by
   * UART_Send() function, just to reset Tx Interrupt state for the
   * first time
   */
  ub->txInterrupt = RESET;
  
  // Reset ring buf head and tail idx
  __BUF_RESET(ub->rx_head);
  __BUF_RESET(ub->rx_tail);
  __BUF_RESET(ub->tx_head);
  __BUF_RESET(ub->tx_tail);

  // Enable UART Transmit
  UART_TxCmd(uart, ENABLE);

  /* Enable UART Rx interrupt */
  UART_IntConfig(uart, UART_INTCFG_RBR, ENABLE);
  /* Enable UART line status interrupt */
  UART_IntConfig(uart, UART_INTCFG_RLS, ENABLE);

  IRQn_Type irq;
  if (uartNumber == 0) {
    irq = UART0_IRQn;

  } else if (uartNumber == 1) {
    irq = UART1_IRQn;

  } else if (uartNumber == 2) {
    irq = UART2_IRQn;

  } else /* if (uart == LPC_UART3)*/ {
    irq = UART3_IRQn;
  } 

  /* preemption = 1, sub-priority = 1 */
  NVIC_SetPriority(irq, ((0x01<<3)|0x01));
  /* Enable Interrupt for UART channel */
  NVIC_EnableIRQ(irq);

  configPin(txpin);
  configPin(rxpin);
}


void initUARTs() {

#ifdef IO_DEBUG_TX
  configUart(IO_DEBUG_TX, IO_DEBUG_RX);
#endif
  
#ifdef IO_WATCHDOG_TX
  configUart(IO_WATCHDOG_TX, IO_WATCHDOG_RX);
#endif
  
#ifdef IO_CHILLER_TX
  configUart(IO_CHILLER_TX, IO_CHILLER_RX);
#endif
}


/*----------------- INTERRUPT SERVICE ROUTINES --------------------------*/
void UART_IntReceive(UartRingBuffer *ub) {
  while (1) {
    // Call UART read function in UART driver
    uint8_t tmpc;
    uint32_t rLen = UART_Receive(ub->uart, &tmpc, 1, NON_BLOCKING);
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

void UART_IntTransmit(UartRingBuffer *ub) {
  // Disable THRE interrupt
  UART_IntConfig(ub->uart, UART_INTCFG_THRE, DISABLE);

  /* Wait for FIFO buffer empty, transfer UART_TX_FIFO_SIZE bytes
   * of data or break whenever ring buffers are empty */
  /* Wait until THR empty */
  while (UART_CheckBusy(ub->uart) == SET);

  while (!__BUF_IS_EMPTY(ub->tx_head,ub->tx_tail)) {
    /* Move a piece of data into the transmit FIFO */
    if (UART_Send(ub->uart, (uint8_t *)&ub->tx[ub->tx_tail], 1, NON_BLOCKING)){
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

void UART_IntErr(UartRingBuffer *ub, uint8_t bLSErrType) {
  ub->error = bLSErrType;
}


#ifdef USE_UART0
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
#endif


#ifdef USE_UART1
void UART1_IRQHandler(void) {
   /* Determine the interrupt source */
   uint32_t intsrc = UART_GetIntId(LPC_UART1);
   uint32_t tmp = intsrc & UART_IIR_INTID_MASK;

   // Receive Line Status
   if (tmp == UART_IIR_INTID_RLS){
     // Check line status
     uint32_t tmp1 = UART_GetLineStatus(LPC_UART1);
     // Mask out the Receive Ready and Transmit Holding empty status
     tmp1 &= (UART_LSR_OE | UART_LSR_PE | UART_LSR_FE | UART_LSR_BI | UART_LSR_RXFE);
     // If any error exist
     if (tmp1) {
       UART_IntErr(&uart1, tmp1);
     }
   }
   
   // Receive Data Available or Character time-out
   if ((tmp == UART_IIR_INTID_RDA) || (tmp == UART_IIR_INTID_CTI)){
     UART_IntReceive(&uart1);
   }

   // Transmit Holding Empty
   if (tmp == UART_IIR_INTID_THRE){
     UART_IntTransmit(&uart1);
   }   
}
#endif


#ifdef USE_UART2
void UART2_IRQHandler(void) {
   /* Determine the interrupt source */
   uint32_t intsrc = UART_GetIntId(LPC_UART2);
   uint32_t tmp = intsrc & UART_IIR_INTID_MASK;

   // Receive Line Status
   if (tmp == UART_IIR_INTID_RLS){
     // Check line status
     uint32_t tmp1 = UART_GetLineStatus(LPC_UART2);
     // Mask out the Receive Ready and Transmit Holding empty status
     tmp1 &= (UART_LSR_OE | UART_LSR_PE | UART_LSR_FE | UART_LSR_BI | UART_LSR_RXFE);
     // If any error exist
     if (tmp1) {
       UART_IntErr(&uart2, tmp1);
     }
   }
   
   // Receive Data Available or Character time-out
   if ((tmp == UART_IIR_INTID_RDA) || (tmp == UART_IIR_INTID_CTI)){
     UART_IntReceive(&uart2);
   }

   // Transmit Holding Empty
   if (tmp == UART_IIR_INTID_THRE){
     UART_IntTransmit(&uart2);
   }   
}
#endif


#ifdef USE_UART3
void UART3_IRQHandler(void) {
   /* Determine the interrupt source */
   uint32_t intsrc = UART_GetIntId(LPC_UART3);
   uint32_t tmp = intsrc & UART_IIR_INTID_MASK;

   // Receive Line Status
   if (tmp == UART_IIR_INTID_RLS){
     // Check line status
     uint32_t tmp1 = UART_GetLineStatus(LPC_UART3);
     // Mask out the Receive Ready and Transmit Holding empty status
     tmp1 &= (UART_LSR_OE | UART_LSR_PE | UART_LSR_FE | UART_LSR_BI | UART_LSR_RXFE);
     // If any error exist
     if (tmp1) {
       UART_IntErr(&uart3, tmp1);
     }
   }
   
   // Receive Data Available or Character time-out
   if ((tmp == UART_IIR_INTID_RDA) || (tmp == UART_IIR_INTID_CTI)){
     UART_IntReceive(&uart3);
   }

   // Transmit Holding Empty
   if (tmp == UART_IIR_INTID_THRE){
     UART_IntTransmit(&uart3);
   }   
}
#endif



uint32_t UARTSend(UartRingBuffer *ub, char *txbuf, uint32_t buflen)  {
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
  if (ub->txInterrupt == RESET) {
    UART_IntTransmit(ub);

  } else {
    /*
     * Otherwise, re-enables Tx Interrupt
     */
    UART_IntConfig(ub->uart, UART_INTCFG_THRE, ENABLE);
  }
  
  return bytes;
}


uint32_t UARTReceive(UartRingBuffer *ub, char *rxbuf, uint32_t buflen) {
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

void UARTFlush(UartRingBuffer *ub) {
  // wait for current transmission complete - THR must be empty
  while (UART_CheckBusy(ub->uart));
}



// Abstracted, public API

uint32_t sendUART(unsigned int txPin, char *txbuf, uint32_t buflen) {
  return UARTSend(UART_BUFFER[IO_CHAN(txPin)], txbuf, buflen);
}

void flushUART(unsigned int txPin) {
  UARTFlush(UART_BUFFER[IO_CHAN(txPin)]);
}

uint32_t recvUART(unsigned int rxPin, char *rxbuf, uint32_t buflen) {
  return UARTReceive(UART_BUFFER[IO_CHAN(rxPin)], rxbuf, buflen);
}

uint32_t errorUART(unsigned int rxPin) {
  return UART_BUFFER[IO_CHAN(rxPin)]->error;
}

