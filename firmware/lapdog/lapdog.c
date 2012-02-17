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
    PORTB |= _BV(PB6);   
  } else {
    PORTB &=~ _BV(PB6);   
  }
}

void led2(char on) {
  if (on) {
    PORTB |= _BV(PB7);   
  } else {
    PORTB &=~ _BV(PB7);   
  }
}

void stop() {
  PORTB &=~ _BV(PB2);
  PORTC &=~ _BV(PC1);
}

void run() {
  PORTB |= _BV(PB2);
  PORTC |= _BV(PC1);
}


int main(void) {
  DDRB  |= _BV(PB6) | _BV(PB7);  // LED outputs
  PORTB |= _BV(PB7) | _BV(PB6);

  DDRB  |= _BV(PB2);  // Enable Motors output
  DDRC  |= _BV(PC1);  // Enable LASER outout 
  
  wdt_enable(WDTO_4S);
  
  uart_init();
  FILE uart_str = FDEV_SETUP_STREAM(uart_putchar, uart_getchar, _FDEV_SETUP_RW);
  stdout = stdin = &uart_str;
  fprintf(stdout, PROGSTR("#Power up!\n"));
  
  led1(0);
  led2(0);

  char frame = 0;
  while(1) {
    if (!(frame & 15)) {
      fprintf(stdout, PROGSTR("OK\n"));
    }

    if (frame & 8) {
      led1(!(frame & 1));
      run();
  
    } else {
      led2(!(frame & 1));
      stop();
    }
    
    sleepMs(50);
    wdt_reset();
    frame++;
  }	
}
