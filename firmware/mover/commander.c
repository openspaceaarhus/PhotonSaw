#include <stdio.h>
#include <string.h>

#include "api.h"
#include "state.h"
#include "alarm.h"

#include "commander.h"

char READY[] = "Ready\r\n";
void respondReady(FILE *output) {
  fflush(output);
  if (output == stdout) {
    usbSendFlush(READY, sizeof(READY)-1);
  } else {
    fiprintf(output, READY);    
  }
}

void respondError(char *msg, FILE *output) {
  fiprintf(output, "Error %s\r\n", msg);
  respondReady(output);
}

void respondSyntaxError(char *msg, FILE *output) {
  fiprintf(output, "Error, unknown command: %s\r\n", msg);
  if (output != stderr) {
    fiprintf(stderr, "Got invalid command from USB: %s\r\n", msg);
  }
  respondReady(output);
}

void commandRun(char *line, FILE *output) {
  if (!*line) {
    fiprintf(output, "\x1b[2J   PhotonSaw\r\n");
    printState(output);
    fiprintf(output, "\r\nTry ? for help\r\n");
    respondReady(output);
    return;
  }

  int id; // General purpose parameter
  if (!strcmp(line, "?")) {
    fiprintf(output, "Known commands:\r\n");
    fiprintf(output, "blank line: clear terminal and print status.\r\n");
    fiprintf(output, "st: Print status\r\n");
    fiprintf(output, "pf: Preflight, sets alarms in case of trouble\r\n");
    fiprintf(output, "ac=<id>: Clears alarm with <id>\r\n");
    respondReady(output);
    return;

  } else if (!strcmp(line, "st")) {
    printState(output);
    respondReady(output);
    return;

  } else if (!strcmp(line, "pf")) {
    checkAlarmInputs();
    printState(output);
    respondReady(output);
    return;

  } else if (sscanf(line, "ac=%d", &id)) {
    if (alarmClear(id)) {
      fiprintf(output, "Error: Alarm id not valid: %d\r\n", id);      
    } else {
      fiprintf(output, "OK: Alarm %d cleared\r\n", id);      
    }

    printAlarmState(output);
    respondReady(output);
    
  } else {
    respondSyntaxError(line, output);
    return;
  }
}
