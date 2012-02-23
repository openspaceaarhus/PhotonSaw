#include "board.h"
#include "adc.h"
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
    Delay(500);

    int airflow = readADC(IO_AIRFLOW);

    GPIO_CLEAR(IO_LED);
    GPIO_CLEAR(IO_ASSIST_AIR);
    GPIO_SET(IO_EXHAUST);
    GPIO_SET(IO_LASER_FIRE);
    Delay(500);
    
    iprintf("Airflow: %d\n", airflow);
    iprintf("T out:   %d\n", readADC(IO_TEMP_OUT));
    iprintf("T in:    %d\n", readADC(IO_TEMP_IN));
    iprintf("T in:    %d\n", readADC(IO_TEMP_INTERNAL));
    iprintf("Supply:  %d\n", readADC(IO_VOLTAGE));    
  }
}
