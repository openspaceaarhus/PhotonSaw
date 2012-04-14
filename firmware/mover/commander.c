#include <stdio.h>
#include <string.h>

#include "api.h"
#include "state.h"
#include "alarm.h"
#include "shaker.h"
#include "hexparser.h"
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

void cmdHelp(FILE *output) {
    fiprintf(output, "Known commands:\r\n");
    fiprintf(output, "blank line: clear terminal and print status.\r\n");
    fiprintf(output, "st: Print status\r\n");
    fiprintf(output, "pf: Preflight, sets alarms in case of trouble\r\n");
    fiprintf(output, "ac <id>: Clears alarm with <id>\r\n");
    fiprintf(output, "ai <flags>: Ignore alarms\r\n");
    fiprintf(output, "bs Report buffer state\r\n");
    fiprintf(output, "bm (-nc) <moves> <code>... to buffer move codes\r\n");
}

void cmdAlarmClear(char *line, FILE *output) {
  int id;
  if (sscanf(line, "%d", &id) != 1) {
    fiprintf(output, "Error: Unable to parse alarm id: %s\r\n", line);    
    return;
  } 

  if (alarmClear(id)) {
    fiprintf(output, "Error: Alarm id not valid: %d\r\n", id);      
  } else {
    fiprintf(output, "OK: Alarm %d cleared\r\n", id);      
  }

  printAlarmState(output);
}

void cmdAlarmIgnore(char *line, FILE *output) {
  unsigned int flags; 
  if (sscanf(line, "%x", &flags) != 1) {
    fiprintf(output, "Error: Unable to parse alarm mask as hex: %s\r\n", line);    
    return;
  }

  alarmsIgnored = flags;
  fiprintf(output, "OK: New alarm mask: %x\r\n", alarmsIgnored);

  printAlarmState(output);
}


static const char NOCOMMIT[] = "-nc";

void cmdBufferMoves(char *line, FILE *output) {
  char *lineStart = line;

  char commit = 1;
  while (*line && (*line == ' ' || *line == '-')) {
    if (*line == ' ') {
      line++;
    } else if (!strncmp(line, NOCOMMIT, sizeof(NOCOMMIT))) {
      *line += sizeof(NOCOMMIT);
      commit = 0;
    }
  }
  
  unsigned int moves = 0;
  if (parseHex(&line, &moves) < 1) {
    fprintf(output, "Error: Unable to parse the number of moves int at char %d\r\n", (line-lineStart));
    respondReady(output);
    return;
  }

  if (moves > bufferAvailable()) {
    fprintf(output, "Error: Not enough room in buffer for %d moves (only %d words free)\r\n", moves, bufferAvailable());
    respondReady(output);
    return;
  }

  while (*line) {
    unsigned int mc;
    if (parseHex(&line, &mc) < 1) {
      fiprintf(output, "Error: Unable to parse the move code at char %d\r\n", (line-lineStart));
      respondReady(output);
      bufferRollback();
      return;
    }
    bufferMoveCode(mc);
  }

  if (commit) {
    bufferCommit();
  }
  
  printBufferState(output);
  return;
}

void commandRun(char *line, FILE *output) {
  if (!*line) {
    fiprintf(output, "\x1b[2JPhotonSaw console\r\n");
    printState(output);
    fiprintf(output, "\r\nTry ? for help\r\n");
    respondReady(output);
    return;
  }

  if (!strncmp(line, "bm ", 3)) {
    cmdBufferMoves(line+3, output);
    return;
    
  } else if (!strcmp(line, "bs")) {
    printBufferState(output);

  } else if (!strcmp(line, "?")) {
    cmdHelp(output);

  } else if (!strcmp(line, "st")) {
    printState(output);

  } else if (!strcmp(line, "pf")) {
    checkAlarmInputs();
    printState(output);

  } else if (!strncmp(line, "ac ", 3)) {
    cmdAlarmClear(line+3, output);

  } else if (!strncmp(line, "ai ", 3)) {
    cmdAlarmIgnore(line+3, output);
    
  } else {
    respondSyntaxError(line, output);
    return;
  }
  respondReady(output);
}
