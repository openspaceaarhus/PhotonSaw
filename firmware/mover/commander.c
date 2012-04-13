#include <stdio.h>
#include <string.h>

#include "api.h"
#include "state.h"
#include "alarm.h"

#include "commander.h"

volatile int pendingUsbCommand;
int usbPending() {
  return pendingUsbCommand;
}

char usbCommandLine[USB_LINE_BUFFER_SIZE] IN_IRAM1;
void usbLine(char *line, unsigned int lineSize) {
  if (!pendingUsbCommand) {
    strcpy(usbCommandLine, line);
    pendingUsbCommand = 1;
  } 
}

char READY[] = "Ready\r\n";
void respondReady() {
  fflush(stdout);
  usbSendFlush(READY, sizeof(READY)-1);
}

void respondError(char *msg) {
  iprintf("Error %s\r\n", msg);
  respondReady();
}

void respondSyntaxError(char *msg) {
  iprintf("Error, unknown command: %s\r\nReady\r\n", msg);
  fprintf(stderr, "Got invalid command from USB: %s\r\n", msg);
  respondReady();
}

void usbHandleInternal();
void usbHandle() {
  if (!pendingUsbCommand) {
    return;
  }

  usbHandleInternal();

  pendingUsbCommand = 0;
} 

void usbHandleInternal() {
  if (!*usbCommandLine) {
    iprintf("\x1b[2J   PhotonSaw\r\n");
    printState(stdout);
    iprintf("\r\nTry ? for help\r\n");
    respondReady();
    return;
  }

  int id; // General purpose parameter
  if (!strcmp(usbCommandLine, "?")) {
    iprintf("Known commands:\r\n");
    iprintf("blank line: clear terminal and print status.\r\n");
    iprintf("st: Print status\r\n");
    iprintf("pf: Preflight, sets alarms in case of trouble\r\n");
    iprintf("ac=<id>: Clears alarm with <id>\r\n");
    respondReady();
    return;

  } else if (!strcmp(usbCommandLine, "st")) {
    printState(stdout);
    respondReady();
    return;

  } else if (!strcmp(usbCommandLine, "pf")) {
    checkAlarmInputs();
    printState(stdout);
    respondReady();
    return;

  } else if (sscanf(usbCommandLine, "ac=%d", &id)) {
    if (alarmClear(id)) {
      iprintf("Error: Alarm id not valid: %d\r\n", id);      
    } else {
      iprintf("OK: Alarm %d cleared\r\n", id);      
    }

    printAlarmState(stdout);
    respondReady();
    
  } else {
    respondSyntaxError(usbCommandLine);
    return;
  }
}
