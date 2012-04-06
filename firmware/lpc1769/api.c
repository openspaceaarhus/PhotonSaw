#include "api.h"
#include "uarts.h"

FILE *chiller;
FILE *watchdog;

void initAPI() {
  chiller = fopen("/dev/chiller", "r+");
  watchdog = fopen("/dev/watchdog", "r+");

  usbInit();
}

int getline(FILE *file, char line[], int max) {
  int nch = 0;
  int c;
  max--;

  while((c = getc(file)) != EOF) {
    if(c == '\n') break;
    
    if(nch < max) {
      line[nch] = c;
      nch = nch + 1;
    }
  }

  if(c == EOF && nch == 0) return EOF;
  
  line[nch] = '\0';
  return nch;
}
