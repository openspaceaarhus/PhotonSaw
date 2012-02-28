#include "board.h"
#include "adc.h"
#include "pwm.h"
#include "stepper.h"

#include <string.h>
#include <stdio.h>

volatile unsigned long SysTickCnt;
void SysTick_Handler (void) {
  SysTickCnt++;
  GPIO_SET(IO_X_STEP);
  GPIO_CLEAR(IO_X_STEP);
}

void Delay (unsigned long tick) {
  unsigned long systickcnt = SysTickCnt;
  while ((SysTickCnt - systickcnt) < tick);
}

int main(void) {
  SysTick_Config(SystemCoreClock/100 - 1);

  iprintf("Power Up!\n\r");

  Stepper s = stpInit(IO_X_STEP, IO_X_DIR, IO_X_ENABLE, IO_X_CURRENT, IO_X_USM0, IO_X_USM1);
  stpCurrent(&s, 1800);
  stpMicrostep(&s, 3);
  stpEnable(&s);
   
  while (1) {
    GPIO_SET(IO_LED);
    Delay(500);
    
    GPIO_CLEAR(IO_LED);
    Delay(500);
  }
}
