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
