#include <string.h>
#include <stdio.h>

#include "api.h"

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
  while (1) {
    GPIO_SET(IO_LED);
    delay(200);

    GPIO_CLEAR(IO_LED);
    delay(500);
  }
}
