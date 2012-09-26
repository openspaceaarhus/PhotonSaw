#include "mprintf.h"
#include <stdlib.h>

#include "defines.h"
#include "lcd.h"

void muartInit(void) {
#if F_CPU < 2000000UL && defined(U2X)
  UCSRA = _BV(U2X);             /* improve baud rate error by using 2x clk */
  UBRRL = (F_CPU / (8UL * UART_BAUD)) - 1;
#else
  UBRRL = (F_CPU / (16UL * UART_BAUD)) - 1;
#endif
  UCSRB = _BV(TXEN) | _BV(RXEN); /* tx/rx enable */
}

/*
 * Send character c down the UART Tx, wait until tx holding register
 * is empty.
 */
void mputchar(char c) {
  loop_until_bit_is_set(UCSRA, UDRE);
  UDR = c;
  loop_until_bit_is_set(UCSRA, UDRE);
}  

void mputs(char *c) {
  while (*c) {
    mputchar(*(c++));
  }
}  


void mprintf(PGM_P format, ...) {
  va_list ap;

  va_start(ap, format);

  char ch;
  while ((ch = pgm_read_byte(format))) {
    if (ch == '%') {
      char type = pgm_read_byte(++format);
      if (type == 'd' || type == 'x') { // An integer from ram
	int d = va_arg(ap, int);
	char db[10];
	db[0] = 0;
	itoa(d, db, type=='d' ? 10 : 16);
	mputs(db);

      } else if (type == 'l') { // A long from ram
	long d = va_arg(ap, long);
	char db[10];
	db[0] = 0;
	itoa(d, db, 10);
	mputs(db);

      } else if (type == 's') { // A string from ram
	char *s = va_arg(ap, char *);
	mputs(s);

      } else if (type == 'p') { // A string from progmem
	PGM_P s = va_arg(ap, PGM_P);

	while ((ch = pgm_read_byte(s))) {
	  mputchar(ch);
	  ++s;
	}

      } else { // Fall back is to print the formatting code (so %% works normally)
	mputchar('%');
	mputchar(ch);		
      }	  

    } else if (ch == '\n') {
      mputchar('\r');
      mputchar(ch);

    } else {
      mputchar(ch);
    }

    ++format;
  }

  va_end(ap);
}


void msprintf(char *out, PGM_P format, ...) {
  va_list ap;

  va_start(ap, format);

  char ch;
  while ((ch = pgm_read_byte(format))) {
	if (ch == '%') {
	  char type = pgm_read_byte(++format);
	  if (type == 'd' || type == 'x') { // An integer from ram
		int d = va_arg(ap, int);
		char db[10];
		db[0] = 0;
		itoa(d, db, type == 'd' ? 10 : 16);
		char *dp = db;
		while (*dp) {
		  *(out++) = *(dp++);
		}

	  } else if (type == 's') { // A string from ram
		char *s = va_arg(ap, char *);
		while (*s) {
		  *(out++) = *(s++);
		}

	  } else if (type == 'p') { // A string from progmem
		PGM_P s = va_arg(ap, PGM_P);

		while ((ch = pgm_read_byte(s))) {
		  *(out++) = ch;
		  ++s;
		}
		*out = 0;

	  } else { // Fall back is to print the formatting code (so %% works normally)
		*(out++) = '%';
		*(out++) = ch;
	  }	  

	} else if (ch == '\n') {
	  *(out++) = '\r';
	  *(out++) = ch;

	} else {
	  *(out++) = ch;
	}

	++format;
  }

  *out = 0;

  va_end(ap);
}


void lcd_printf(PGM_P format, ...) {
  va_list ap;

  va_start(ap, format);

  char ch;
  while ((ch = pgm_read_byte(format))) {
	if (ch == '%') {
	  char type = pgm_read_byte(++format);
	  if (type == 'd') { // An integer from ram
		int d = va_arg(ap, int);
		char db[10];
		db[0] = 0;
		itoa(d, db, 10);
		char *dp = db;
		while (*dp) {
		  lcd_putc(*(dp++));
		}

	  } else if (type == 's') { // A string from ram
		char *s = va_arg(ap, char *);
		while (*s) {
		  lcd_putc(*(s++));
		}

	  } else if (type == 'p') { // A string from progmem
		PGM_P s = va_arg(ap, PGM_P);

		while ((ch = pgm_read_byte(s))) {
		  lcd_putc(ch);
		  ++s;
		}

	  } else { // Fall back is to print the formatting code (so %% works normally)
		lcd_putc('%');
		lcd_putc(ch);
	  }	  

	} else if (ch == '\n') {
	  lcd_putc('\r');
	  lcd_putc(ch);

	} else {
	  lcd_putc(ch);
	}

	++format;
  }

  va_end(ap);
}


