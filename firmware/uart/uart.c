#include "api.h"

#include <string.h>
#include <stdio.h>

int main(void) {
  fiprintf(stderr, "Power Up!\n\r");
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
    
    fiprintf(stderr, "Airflow: %d (%d %%)\n\r", READ_ADC(IO_AIRFLOW), airflow());

    fprintf(stderr, "T out:   %d (%f Ohm, %f degC)\n\r",
	   READ_ADC(IO_TEMP_OUT),
	   readNTCres(IO_CHAN(IO_TEMP_OUT)),
	   readNTCcelcius(IO_CHAN(IO_TEMP_OUT))
	   );

    fprintf(stderr, "T in:    %f degC\n\r", readNTCcelcius(IO_CHAN(IO_TEMP_IN)));
    fprintf(stderr, "T inter: %f degC\n\r", readNTCcelcius(IO_CHAN(IO_TEMP_INTERNAL)));
    fiprintf(stderr, "Supply:  %d mv\n\r", supplyVoltage());        
    
    char buffer[100];
    uint32_t len = recvUART(IO_WATCHDOG_RX, buffer, sizeof(buffer)-1);
    if (len) {
      buffer[len] = 0;
      fiprintf(stderr, "WD said (%d): %s\n\r", (unsigned int)len, buffer);
    }

    unsigned int err0 = errorUART(IO_DEBUG_RX);
    if (err0) {
      fiprintf(stderr, "Debug UART Error: %x\n\r", err0);        
    }

    err0 = errorUART(IO_WATCHDOG_RX);
    if (err0) {
      fiprintf(stderr, "Watchdog UART Error: %x\n\r", err0);        
    }
    err0 = errorUART(IO_CHILLER_RX);
    if (err0) {
      fiprintf(stderr, "Chiller UART Error: %x\n\r", err0);        
    }
  }
}
