#include "api.h"

#include <string.h>
#include <stdio.h>

// Allocate the largest 2^n sized buffer we can in IRAM1 for the move buffer.
#define MOVE_BUFFER_SIZE (1<<12)
int moves[MOVE_BUFFER_SIZE] IN_IRAM1;

// 8 KB for the input line buffer ought to be enough
#define USB_LINE_BUFFER_SIZE (1<<13)
char usbLineBuffer[USB_LINE_BUFFER_SIZE] IN_IRAM1;

// 4 KB for the output line buffer ought to be enough
#define USB_TX_BUFFER_SIZE (1<<12)
char usbTxBuffer[USB_TX_BUFFER_SIZE] IN_IRAM1;

int main(void) {
  fiprintf(stderr, "Power Up!\n\r");
  
  while (1) {
    fiprintf(stderr, "Move buffer located at: 0x%x  line buffer at: 0x%x\n\r", 
	     (unsigned int)moves,
	     (unsigned int)usbLineBuffer);
    GPIO_SET(IO_LED);
    GPIO_SET(IO_ASSIST_AIR);
    GPIO_CLEAR(IO_EXHAUST);
    GPIO_CLEAR(IO_LASER_FIRE);
    delay(200);

    GPIO_CLEAR(IO_LED);
    GPIO_CLEAR(IO_ASSIST_AIR);
    GPIO_SET(IO_EXHAUST);
    GPIO_SET(IO_LASER_FIRE);
    delay(200);
    
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
    *buffer = 0;
    fgets(buffer, sizeof(buffer), watchdog);
    if (*buffer) {
      fiprintf(stderr, "WD said: %s\n\r", buffer);
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
