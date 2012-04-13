#include <stdio.h>
#include <string.h>

#include "api.h"
#include "state.h"
#include "alarm.h"

char commandBuffer[1<<7];
volatile int pendingCommand;

void handleUart0Line(const char *line, int lineLength) {
  if (!pendingCommand) {
    strcpy(commandBuffer, line);
    pendingCommand = 1;
  }
}

int consolePending() {
  return pendingCommand;
}

void consoleHandle() {
  if (!pendingCommand) {
    return;
  }

  if (*commandBuffer) {
    fprintf(stderr, "Sorry don't know command: %s\r\n", commandBuffer);
  } else {
    fiprintf(stderr, "\x1b[2J   PhotonSaw console\r\n");
    printState(stderr);
  }

  pendingCommand = 0;
}
