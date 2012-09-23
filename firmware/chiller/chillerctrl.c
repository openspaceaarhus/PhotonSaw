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

#include "aux_globals.h"
#include "HD44780.h"
#include "pwmvoltages.h"
#include "adchelper.h"


// We don't really care about unhandled interrupts.
EMPTY_INTERRUPT(__vector_default)


// A macro and function to store string constants in flash and only copy them to
// RAM when needed, note the limit on string length.

char stringBuffer[80];

const char *getString(PGM_P src) {
    strcpy_P(stringBuffer, src);
    return stringBuffer;
}

#define PROGSTR(s) getString(PSTR(s))

void led(char on) {
  if (on) {
    PORTB |= _BV(PB5);   
  } else {
    PORTB &=~ _BV(PB5);   
  }
}

void lcdInit() {
  _delay_ms(10);
  lcd_init();   // init the LCD screen
  _delay_ms(10);
  lcd_clrscr();	// initial screen cleanup
  _delay_ms(10);
  lcd_home();
  _delay_ms(10);
  lcd_instr(LCD_DISP_ON);
  _delay_ms(10);
}

/*
void lcdReadout(char watt) {
  
  float a = getOsADC(0)*100.0/(1<<10);
  int ai = a;
  int ad = trunc((a-ai)*100);

  float v = getOsADC(1)*30.0/(1<<10);
  int vi = v;
  int vd = trunc((v-vi)*100);

  float w = v*a;
  int wi = w;
  int wd = trunc((w-wi)*10);

  char buffy[17];
  memset(buffy, 0, sizeof(buffy));

  if (watt) {
    sprintf(buffy, PROGSTR("%2d.%02d V %4d.%01d W"), vi,vd, wi, wd);
  } else {
    sprintf(buffy, PROGSTR("%2d.%02d V %3d.%02d A"), vi,vd, ai,ad);
  }

  while (strlen(buffy) < 16) {
    strcat(buffy, " ");
  }

  lcd_string(buffy);
  //  lcd_string_format(PROGSTR("%d   %d  "), getOsADC(0), getOsADC(1));
  lcd_home();
}
*/


int main(void) {
  wdt_enable(WDTO_4S); // We don't want to hang.

  // Set up the outputs:
  DDRB  |= _BV(PB5);  // LED output on SCK pin

  DDRC  |= _BV(PC5);  // Compressor Relay
  DDRD  |= _BV(PD7);  // Fan Relay
  DDRB  |= _BV(PB0);  // Tank circulation pump Relay

  led(1);

  initADC();
  initPWM();

  uart_init();
  FILE uart_str = FDEV_SETUP_STREAM(uart_putchar, uart_getchar, _FDEV_SETUP_RW);
  stdout = stdin = &uart_str;
  fprintf(stdout, PROGSTR("#Power up!\r\n"));

  _delay_ms(1500);

  lcdInit();
  _delay_ms(20);
  lcd_setline(0);
  lcd_string(PROGSTR("  Chillah ctrl"));
  lcd_setline(1);
  lcd_string(PROGSTR("    Stand by"));
    
  led(0);

  char frame = 0;

  setCirculationSpeed(100);

  setCoolingSpeed(100);


  while(1) {

    if ((frame & 15) == 0) {
      fprintf(stdout, PROGSTR("OK circ=%d cool=%d\r\n"), 
	      (int)round(getCurrentCirculationSpeed()),
	      (int)round(getCurrentCoolingSpeed()) );
    } 

    led(frame & 8); 

    updatePWM();
    _delay_ms(10);
    wdt_reset();
    frame++;
  }	
}
