#include "lpc17xx_gpio.h"

#include "lpc17xx_uart.h"
#include "lpc17xx_pinsel.h"
#include <string.h>

#define UART_PORT 0

#if (UART_PORT == 0)
#define TEST_UART LPC_UART0
#elif (UART_PORT == 1)
#define TEST_UART (LPC_UART_TypeDef *)UART1
#endif

volatile unsigned long SysTickCnt;
void SysTick_Handler (void) {
  SysTickCnt++;
}

void Delay (unsigned long tick) {
  unsigned long systickcnt = SysTickCnt;
  while ((SysTickCnt - systickcnt) < tick);
}

void uartSend(char *str) {
  UART_Send(TEST_UART, (uint8_t *)str, strlen(str), BLOCKING);
  while (UART_CheckBusy(TEST_UART) == SET);
}

int main(void) {
  SysTick_Config(SystemCoreClock/1000 - 1);

  GPIO_SetDir(1, 1<<27, 1);
  GPIO_SetValue(1, 1<<27);



  PINSEL_CFG_Type PinCfg;
#if (UART_PORT == 0)
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
#endif
  
#if (UART_PORT == 1)
  /*
   * Initialize UART1 pin connect
   */
  PinCfg.Funcnum = 2;
  PinCfg.OpenDrain = 0;
  PinCfg.Pinmode = 0;
  PinCfg.Pinnum = 0;
  PinCfg.Portnum = 2;
  PINSEL_ConfigPin(&PinCfg);
  PinCfg.Pinnum = 1;
  PINSEL_ConfigPin(&PinCfg);
#endif

  
  /* Initialize UART Configuration parameter structure to default state: 9600 8n1 */
  UART_CFG_Type uartConfig;
  UART_ConfigStructInit(&uartConfig);
  uartConfig.Baud_rate = 115000;
  UART_Init(TEST_UART, &uartConfig);

  /* Initialize FIFOConfigStruct to default state:
   * 				- FIFO_DMAMode = DISABLE
   * 				- FIFO_Level = UART_FIFO_TRGLEV0
   * 				- FIFO_ResetRxBuf = ENABLE
   * 				- FIFO_ResetTxBuf = ENABLE
   * 				- FIFO_State = ENABLE
   */
  UART_FIFO_CFG_Type fifoConfig;
  UART_FIFOConfigStructInit(&fifoConfig);
  UART_FIFOConfig(TEST_UART, &fifoConfig);
  UART_TxCmd(TEST_UART, ENABLE);

  uartSend("Power Up!\n");

  while (1) {
    GPIO_SetValue(1, 1<<27);
    uartSend("On!\n");
    Delay(500);
    GPIO_ClearValue(1, 1<<27);
    uartSend("Off!\n");
    Delay(500);
  }
}
