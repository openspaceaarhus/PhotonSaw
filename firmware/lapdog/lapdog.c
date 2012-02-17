#include "defines.h"

#include <ctype.h>
#include <inttypes.h>

#include <string.h>
#include <stdlib.h>
#include <stdint.h>
#include <stdio.h>

#include <avr/io.h>
#include <util/delay.h>
#include <avr/pgmspace.h>
#include <avr/sleep.h>

#include <avr/wdt.h> 
#include <avr/interrupt.h>
#include <avr/eeprom.h> 
#include <avr/pgmspace.h>

#include "uart.h"
#include "sleep.h"

// We don't really care about unhandled interrupts.
EMPTY_INTERRUPT(__vector_default)

// A macro and function to store string constants in flash and only copy them to
// RAM when needed, note the limit on string length.
char stringBuffer[80];

const char *getString(PGM_P src) {
    //assert(strlen_P(src) < sizeof(stringBuffer));
    strcpy_P(stringBuffer, src);
    return stringBuffer;
}

#define PROGSTR(s) getString(PSTR(s))

void led1(char on) {
  if (on) {
    PORTC |= 1<<PB6;   
  } else {
    PORTC &=~ 1<<PB6;   
  }
}

void led2(char on) {
  if (on) {
    PORTC |= 1<<PB7;   
  } else {
    PORTC &=~ 1<<PB7;   
  }
}

int main(void) {
  wdt_enable(WDTO_4S);
  
  // The fuses set the clock prescaler to divide by 8, so we need to 
  // turn up the clock speed:
  CLKPR = 1<<CLKPCE; CLKPR = CLOCK_PRESCALER;

  DDRB |= 1<<PB6 | 1<<PB6;  // LED outputs
  led1(1);

  uart_init();
  FILE uart_str = FDEV_SETUP_STREAM(uart_putchar, uart_getchar, _FDEV_SETUP_RW);
  stdout = stdin = &uart_str;
  fprintf(stdout, PROGSTR("#Power up!\n"));
  
  unsigned int frame = 0;
  while(1) {
    fprintf(stdout, PROGSTR("frame=%u\n"), frame);

    if (frame & 8) {
      led1(frame & (1|4));
    } else {
      led2(frame & (1|4));
    }
    
    sleepMs(100);
    wdt_reset();
  }	
}
