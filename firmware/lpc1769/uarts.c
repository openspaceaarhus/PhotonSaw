#include "board.h"
#include "uarts.h"

uint32_t BAUD[] = { 115200, 9600, 9600, 9600 };
LPC_UART_TypeDef* UARTS[] = {LPC_UART0, (LPC_UART_TypeDef *)LPC_UART1, LPC_UART2, LPC_UART3};
LPC_UART_TypeDef* DEBUG_UART;
LPC_UART_TypeDef* WATCHDOG_UART;
LPC_UART_TypeDef* CHILLER_UART;

// Configures a pair of pins to use as an UART
void configUart(const uint32_t txpin, const uint32_t rxpin) {
  const int uartNumber = IO_CHAN(txpin);
  LPC_UART_TypeDef *uart = UARTS[uartNumber];

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

  configPin(txpin);
  configPin(rxpin);
}

void initUARTs() {
  configUart(IO_DEBUG_TX, IO_DEBUG_RX);
  DEBUG_UART = UARTS[IO_CHAN(IO_DEBUG_TX)];

  configUart(IO_WATCHDOG_TX, IO_WATCHDOG_RX);
  WATCHDOG_UART = UARTS[IO_CHAN(IO_WATCHDOG_TX)];

  configUart(IO_CHILLER_TX, IO_CHILLER_RX);
  CHILLER_UART = UARTS[IO_CHAN(IO_CHILLER_TX)];
}
