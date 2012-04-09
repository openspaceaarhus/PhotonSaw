#include <string.h>
#include <stdio.h>

#include "api.h"
#include "shaker.h"

int main(void) {
  fiprintf(stderr, "\x1b[2JPower Up!\r\n");

  /*
    What a boring main routine, but everything happens in the IRQ service routines:

    commander.c Parses the usb commands
    watchbone.c Code the watchdog chews on
    mrchilly.c  Chiller interface
    console.c   Debug serial port interface 
    shaker.c    Realtime control output
  */

  shakerInit();

  fiprintf(stderr, "Non-default IRQ priorities:\r\n");    
  for (int i=-2;i<35;i++) {
    unsigned int p = (unsigned int)NVIC_GetPriority(i);

    if (p != GROUP_PRIORITY_DEFAULT) {
      fiprintf(stderr, "  IRQ %d group:%d, sub:%d\r\n", i, p >> 2, p & 3);    
    }
  }

  while (1) {
    GPIO_SET(IO_LED);
    delay(200);

    GPIO_CLEAR(IO_LED);
    delay(500);
  }
}
