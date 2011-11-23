#define F_CPU 8000000UL //8 MHz

#define USART_BAUDRATE 9600
#define BAUD_PRESCALE (((F_CPU / (USART_BAUDRATE * 16UL))) - 1)


#include <avr/io.h>
#include <avr/interrupt.h>
#include <util/delay.h>
#include <string.h>
#include "uart.c"

char uart_buf[40];
char power;
int store_max;
int store_min;
int buffer_set;
int post_run;
int storage;
int buffer;
int cooling_pwm;
char compressor;
char fan;


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
				ParseCMD();
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


void ParseCMD()
{
	char option[5];
	int number[4];

	if(sscanf(uart_buf,"power=%s",option))
	{
		if(option[1] == 'n')
		{
			power = 1;
			option[4] = 'o';
		}
		else if(option[1] == 'f')
		{
			power = 0;
			option[4] = 'o';
		}
		else
		{
			option[4] = 'n';
		}

	}
	else if(sscanf(uart_buf,"store-max=%d",number))
	{
		if(number[0])
		{
			store_max = number[0];
			option[4] = 'o';
		}
		else
		{
			printf("%d\n",number[0]);
			option[4] = 'n';
		}
	}
        else if(sscanf(uart_buf,"store-min=%d",number))
        {
                if(option[0])
                {
                        store_min = number[0];
                        option[4] = 'o';
                }
                else
                {
                        option[4] = 'n';
                }
        }
        else if(sscanf(uart_buf,"buffer-set=%d",number))
        {
                if(option[0])
                {
                        buffer_set = number[0];
                        option[4] = 'o';
                }
                else
                {
                        option[4] = 'n';
                }
        }
        else if(sscanf(uart_buf,"post-run=%d",number))
        {
                if(option[0])
                {
                        post_run = number[0];
                        option[4] = 'o';
                }
                else
                {
                        option[4] = 'n';
                }
        }
	else if(sscanf(uart_buf,"status%s",option))
	{
		option[4] = 's';

	}
	else
	{
		printf("ERROR: Command not found\n");
	}

	if(option[4] == 'o')
	{
		printf("ok\n");
	}
	else if(option[4] == 'n')
	{
		printf("nok\n");
	}
	else if(option[4] == 's')
	{
		printf("ok\n");
		if(power){ printf("power: on\n"); } else { printf("power: off\n"); }
                printf("store-max: %d\n",store_max);
                printf("store-min: %d\n",store_min);
                printf("buffer-set: %d\n",buffer_set);
                printf("post-run: %d\n",post_run);
                printf("storage: %d\n",storage);
                printf("buffer: %d\n",buffer);
                printf("cooling-pwm: %d\n",cooling_pwm);
		if(compressor){ printf("compressor: on\n"); } else { printf("compressor: off\n"); }
                if(fan){ printf("fan: on\n"); } else { printf("fan: off\n"); }

	}

	memset(option,0,sizeof(option));
}
