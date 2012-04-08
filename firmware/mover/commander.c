#include <stdio.h>
#include <string.h>

#include "api.h"
#include "state.h"

void respondReady() {
  iprintf("Ready\r\n");
  usbFlush();
}

void respondError(char *msg) {
  iprintf("Error %s\r\nReady\r\n", msg);
  usbFlush();
}

const char TESTHEST[] = "Test hest\n\r";

void respondSyntaxError(char *msg) {
  iprintf("Error, unknown command: %s\r\nReady\r\n", msg);
  fprintf(stderr, "Got invalid command from USB: %s\r\n", msg);
  usbFlush();
}

void usbLine(char *line, unsigned int lineSize) {
  if (lineSize == 0) {
    iprintf("\x1b[2JShall we play a game?\r\n");
    printState(stdout);
    respondReady();
    return;
  }

  if (!strcmp(line, "ping")) {
    iprintf("pong!\r\n");
    respondReady();
    return;

  } else if (!strcmp(line, "state")) {
    printState(stdout);
    respondReady();
    return;

  } else {
    respondSyntaxError(line);
    return;
  }
}
