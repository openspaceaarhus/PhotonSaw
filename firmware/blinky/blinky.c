#include "lpc17xx_gpio.h"

volatile unsigned long SysTickCnt;
void SysTick_Handler (void) {
  SysTickCnt++;
}

void Delay (unsigned long tick) {
  unsigned long systickcnt = SysTickCnt;
  while ((SysTickCnt - systickcnt) < tick);
}

int main(void) {
  int num = -1;
  int dir =  1;

  SysTick_Config(SystemCoreClock/1000 - 1);

  GPIO_SetDir(1, 1<<27, 1);

  while (1) {
    GPIO_SetValue(1, 1<<27);
    Delay(200);
    GPIO_ClearValue(1, 1<<27);
    Delay(200);
  }
}
