#include <stdio.h>

#include "state.h"
#include "api.h"

WatchdogState *wdState;

void printState(FILE *file) {
  
  if (wdState) {
    SYSTICK_TYPE age = systickInterval(wdState->timestamp, systick);
    if (age < 2000) {
      fiprintf(file, "Watchdog: %s (age: %d)\r\n", wdState->msg, age);
    } else {
      fiprintf(file, "Watchdog: Stale, age:%d (was: %s)\r\n", age, wdState->msg);
    }
  }

  fiprintf(file, "USB connected: %d\n\r", usbConnected());
  fiprintf(file, "Airflow:  %d %%\n\r", airflow());
  fprintf(file,  "T in:     %f degC\n\r", readNTCcelcius(IO_CHAN(IO_TEMP_OUT)));
  fprintf(file,  "T out:    %f degC\n\r", readNTCcelcius(IO_CHAN(IO_TEMP_IN)));
  fprintf(file,  "T driver: %f degC\n\r", readNTCcelcius(IO_CHAN(IO_TEMP_INTERNAL)));
  fiprintf(file, "Supply:   %d mv\n\r", supplyVoltage());        
    
  unsigned int err0 = errorUART(IO_DEBUG_RX);
  if (err0) {
    fiprintf(file, "Debug UART Error: %x\n\r", err0);        
  }
  err0 = errorUART(IO_WATCHDOG_RX);
  if (err0) {
    fiprintf(file, "Watchdog UART Error: %x\n\r", err0);        
  }
  err0 = errorUART(IO_CHILLER_RX);
  if (err0) {
    fiprintf(file, "Chiller UART Error: %x\n\r", err0);        
  }
}

