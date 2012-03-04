#define CLOCK_8

#if defined(CLOCK_10)

// 20 MHz crystal divided down to 10 MHz to stay within the spec for 3.3V
#define F_CPU 10000000UL
#define CLOCK_PRESCALER 1<<CLKPS0
#define SLEEP_10_MS_COUNT 90


#elif defined(CLOCK_20) 

// 20 MHz crystal, full bore, only valid for 4.5-5.5V
#define F_CPU 20000000UL
#define CLOCK_PRESCALER 0
#define SLEEP_10_MS_COUNT 180


#elif defined(CLOCK_8) 

// 8 MHz internal RC osc, inaccurate.
#define F_CPU 8000000UL
#define CLOCK_PRESCALER 0
#define SLEEP_10_MS_COUNT 79

#else
#error No CLOCK_ defined
#endif

#define UART_BAUD  19200

//#define TWI_DELAY 500



// Mapping of old fashioned register names to atmega8x names

// For rs232:
#define UBRRL UBRR0L

#define UCSRB UCSR0B
#define TXEN TXEN0
#define RXEN RXEN0
#define UCSRA UCSR0A
#define UDRE UDRE0

#define UDR UDR0
#define RXC RXC0
#define FE FE0
#define DOR DOR0



