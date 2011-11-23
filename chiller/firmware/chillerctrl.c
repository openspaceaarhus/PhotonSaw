#define F_CPU 8000000UL //8 MHz

#define USART_BAUDRATE 9600
#define BAUD_PRESCALE (((F_CPU / (USART_BAUDRATE * 16UL))) - 1)


#include <avr/io.h>
#include <avr/interrupt.h>
#include <util/delay.h>
#include <string.h>
#include "uart.c"

char uart_buf[40];

void ParseCMD();

int main(void)
{
//	Setup UART

	UCSR0B |= (1 << RXEN0) | (1 << TXEN0);

	UBRR0H = (BAUD_PRESCALE >> 8);
	UBRR0L = BAUD_PRESCALE;

	stdout = stdin = &uart_str;

	char *buf_p;
	char newchar;

	buf_p = &uart_buf[0];

//	Main loop

	printf("Initalized\n");

	for (;;)
	{

		if(UCSR0A & _BV(RXC0))
		{
			newchar = UDR0;

			if(newchar == 0x0d)
			{
				//Parse CMD
				memset(uart_buf, 0, sizeof(uart_buf));
				buf_p = &uart_buf[0];
			} else {

				*buf_p = newchar;

				buf_p++;

				if(buf_p == (void *)&uart_buf[39])
				{
					printf("ERROR: linebuffer overflow\n");
					memset(uart_buf, 0, sizeof(uart_buf));
					buf_p = &uart_buf[0];
				}

			}

		}

	}

}
