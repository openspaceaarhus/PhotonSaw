#include "board.h"
#include <string.h>
#include <stdio.h>

volatile unsigned long SysTickCnt;
void SysTick_Handler (void) {
  SysTickCnt++;
}

void Delay (unsigned long tick) {
  unsigned long systickcnt = SysTickCnt;
  while ((SysTickCnt - systickcnt) < tick);
}

void uartSend(char *str) {
  UART_Send(DEBUG_UART, (uint8_t *)str, strlen(str), BLOCKING);
  while (UART_CheckBusy(DEBUG_UART) == SET);
}

int main(void) {
  SysTick_Config(SystemCoreClock/1000 - 1);

  uartSend("Power Up!\n\r");

  while (1) {
    GPIO_SET(IO_LED);
    GPIO_SET(IO_ASSIST_AIR);
    GPIO_CLEAR(IO_EXHAUST);
    GPIO_CLEAR(IO_LASER_FIRE);
    uartSend("On!\n\r");
    Delay(500);

    GPIO_CLEAR(IO_LED);
    GPIO_CLEAR(IO_ASSIST_AIR);
    GPIO_SET(IO_EXHAUST);
    GPIO_SET(IO_LASER_FIRE);
    uartSend("Off!\n\r");
    Delay(500);


    printf("Hest: 0x%0x\n", 42);
  }
}
