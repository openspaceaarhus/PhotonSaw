#include <stdio.h>
#include <string.h>

#include "state.h"
#include "api.h"
#include "shaker.h"
#include "alarm.h"
#include "joules.h"

WatchdogState *wdState;

void printInt(FILE *file, char *name, int value, char *unit) {
  fiprintf(file, "%-30s %d %s\r\n", name, value, unit);
}

void printHex(FILE *file, char *name, unsigned int value) {
  fiprintf(file, "%-30s %x\r\n", name, value);
}

void printDouble(FILE *file, char *name, double value, char *unit) {
  fprintf(file, "%-30s %f %s\r\n", name, value, unit);
}

void printString(FILE *file, char *name, char *value) {
  fiprintf(file, "%-30s %s\r\n", name, value);
}

void printBool(FILE *file, char *name, int value) {
  fiprintf(file, "%-30s %s\r\n", name, value?"Yes":"No");
}


void printState(FILE *file) {
  
  WatchdogState *wds = wdState;
  if (wds) {
    SYSTICK_TYPE age = systickInterval(wds->timestamp, systick);
    printInt(file, "watchdog.state.age", age, "ms");
    printString(file, "watchdog.state", wds->msg);
  }

  printBool(file, "usb.connected", usbConnected());
  printInt(file, "exhaust.airflow", airflow(), "%");
  //printInt(file, "exhaust.airflow.adc", readADC(IO_CHAN(IO_AIRFLOW)), "adc");

  printDouble(file, "board.temperature", readNTCcelcius(IO_CHAN(IO_TEMP_INTERNAL)), "C" );
  printInt(file, "board.inputvoltage", supplyVoltage(), "mv");        

  printHex(file, "cooling.alarm", getCoolantAlarm());
  printDouble(file, "cooling.flow",joulesWaterFlow(),"gram/s");        
  printDouble(file, "cooling.power", joulesCurrentPower(), "W");        
  printDouble(file, "cooling.temp.in", joulesLastInTemp(), "C");
  printDouble(file, "cooling.temp.out", joulesLastOutTemp(), "C");
    
  unsigned int err0 = errorUART(IO_DEBUG_RX);
  if (err0) {
    printHex(file, "debug.uart.error", err0);
  }
  err0 = errorUART(IO_WATCHDOG_RX);
  if (err0) {
    printHex(file, "watchdog.uart.error", err0);
  }
  err0 = errorUART(IO_CHILLER_RX);
  if (err0) {
    printHex(file, "chiller.uart.error", err0);
  }

  printInt(file, "sys.irq.max", stepperIRQMax, "us");
  printDouble(file, "sys.irq.avg", (stepperIRQAvg >> 8) / 255.0, "us");
  printInt(file, "sys.time", systick, "ms");

  printAlarmState(file);
}

void printAlarmState(FILE *file) {
  if (alarmCount()) {
    char buffy[100];
    buffy[0] = 0;
    for (int i=0;i<ALARMS;i++) {
      if (alarms[i].active) {
	char b[4];
	siprintf(b, "%i,",i);
	strcat(buffy, b);
      }
    }
    buffy[strlen(buffy)-1] = 0;
    printString(file, "alarm.ids", buffy);

    for (int i=0;i<ALARMS;i++) {
      if (alarms[i].active) {
	fiprintf(file, "alarm.%-24d ts:%d mid:%d mo:%d sw:%x msg:%s\r\n",
		 i, alarms[i].timestamp, alarms[i].moveId, alarms[i].moveCodeOffset,
		 alarms[i].switches, alarms[i].msg
		 );
      }      
    }
  }
}

