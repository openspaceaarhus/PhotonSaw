#include "aux_globals.h"

void delay_int(unsigned long delay) {
  volatile unsigned long i = 0;
  while(delay--) i = delay+1;// asm volatile("nop");
}
