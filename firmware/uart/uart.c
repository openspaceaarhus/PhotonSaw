#include "board.h"
#include "adc.h"
#include "pwm.h"

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

int main(void) {
  SysTick_Config(SystemCoreClock/1000 - 1);

  iprintf("Power Up!\n\r");
  /*
  setPWM(IO_CHAN(IO_X_CURRENT), 128);
  setPWM(IO_CHAN(IO_Y_CURRENT), 256);
  setPWM(IO_CHAN(IO_Z_CURRENT), 0);
  */

  while (1) {
    GPIO_SET(IO_LED);
    GPIO_SET(IO_ASSIST_AIR);
    GPIO_CLEAR(IO_EXHAUST);
    GPIO_CLEAR(IO_LASER_FIRE);
    Delay(500);

    GPIO_CLEAR(IO_LED);
    GPIO_CLEAR(IO_ASSIST_AIR);
    GPIO_SET(IO_EXHAUST);
    GPIO_SET(IO_LASER_FIRE);
    Delay(500);
    
    iprintf("Airflow: %d (%d %%)\n\r", READ_ADC(IO_AIRFLOW), airflow());

    printf("T out:   %d (%f Ohm, %f degC)\n\r",
	   READ_ADC(IO_TEMP_OUT),
	   readNTCres(IO_CHAN(IO_TEMP_OUT)),
	   readNTCcelcius(IO_CHAN(IO_TEMP_OUT))
	   );

    printf("T in:    %f degC\n\r", readNTCcelcius(IO_CHAN(IO_TEMP_IN)));
    printf("T inter: %f degC\n\r", readNTCcelcius(IO_CHAN(IO_TEMP_INTERNAL)));
    iprintf("Supply:  %d mv\n\r", supplyVoltage());        
  }
}
