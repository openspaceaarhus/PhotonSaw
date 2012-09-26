#pragma once

#include <stdarg.h>
#include <avr/pgmspace.h>

// Prints a formatted string using a format stored in flash
void mprintf(PGM_P format, ...);

// like mprintf, but outputs to a string
void msprintf(char *out, PGM_P format, ...);

void lcd_printf(PGM_P format, ...);


void muartInit(void);
void mputchar(char c);
void mputs(char *c);
