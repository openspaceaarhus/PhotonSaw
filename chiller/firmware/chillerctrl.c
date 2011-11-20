#define F_CPU 8000000UL //8 MHz

#define USART_BAUDRATE 9600
#define BAUD_PRESCALE (((F_CPU / (USART_BAUDRATE * 16UL))) - 1)


#include <avr/io.h>
#include <avr/interrupt.h>
#include <util/delay.h>
#include "uart.c"


int main(void)
{
//	Setup UART

	UCSR0B |= (1 << RXEN0) | (1 << TXEN0);

	UBRR0H = (BAUD_PRESCALE >> 8);
	UBRR0L = BAUD_PRESCALE;

	stdout = stdin = &uart_str;

	char uart_buf[40];

//	Main loop

	for (;;) 
	{

		fgets(uart_buf, sizeof(uart_buf)-1, stdin);

		if(uart_buf[0] != 0)
		{

			//do stuff

			printf("ok\n");

			uart_buf[0] = 0;

		}
	}
}
