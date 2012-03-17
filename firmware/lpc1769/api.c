#include "api.h"
#include "uarts.h"

FILE *chiller;
FILE *watchdog;
volatile unsigned long systick;

void SysTick_Handler (void) {
  systick++;
}

void delay(unsigned long ms) {
  unsigned long endtick = ms+systick;

  if (endtick < systick) { // Wraparound
    while (systick); // Wait for 0 to roll around.
  }

  while (systick < endtick); 
}

void initAPI() {
  chiller = open("/dev/chiller", "r+");
  watchdog = open("/dev/watchdog", "r+");

  SysTick_Config(SystemCoreClock/1000 - 1);
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
