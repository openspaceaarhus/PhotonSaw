#pragma once

#include <stdio.h>
#include <avr/pgmspace.h>

typedef void (*UartLineHandler)(void);
UartLineHandler uartSetLineHandler(UartLineHandler newHandler);


#define P(format, ...) printf_P(PSTR(format), __VA_ARGS__)
#define L(str) puts_P(PSTR(str))

void	uartInit(void);

void uartPutByte(uint8_t c);
void uartAwaitTxIdle(void);
int uart_getchar(FILE *stream);

