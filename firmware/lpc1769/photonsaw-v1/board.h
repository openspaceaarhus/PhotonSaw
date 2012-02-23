#ifndef __BOARD_H
#define __BOARD_H

#include "lpc17xx_gpio.h"
#include "lpc17xx_uart.h"

/*
  Set up port configuration constants, these constants should be the only
  place with port specific references, each constant should fit in a 32 bit int.

  Some ports are not configurable, so it's not mentioned here.

  Bits:
    0..7:   Pin number
    8..15:  Port number 
    16..23: Channel: SPI number / UART number / ADC channel / PWM channel
    24..25: Pin function    

  Use these macros to access the bits:
*/


#define OUTPUT (1<<31)

#define IO_PIN(x)  ( (x)        & 31)
#define IO_PORT(x) (((x) >> 8)  & 15)
#define IO_CHAN(x) (((x) >> 16) & 15)
#define IO_FUNC(x) (((x) >> 24) & 3)
#define IO_OUTPUT(x) (x & OUTPUT)

#define IO_P0 (0 << 8)
#define IO_P1 (1 << 8)
#define IO_P2 (2 << 8)
#define IO_P3 (3 << 8)
#define IO_P4 (4 << 8)

#include "board-pins.h"
extern void configPin(const uint32_t pin);

extern uint32_t BAUD[];
extern LPC_UART_TypeDef* UARTS[];
extern LPC_UART_TypeDef* DEBUG_UART;
extern LPC_UART_TypeDef* WATCHDOG_UART;
extern LPC_UART_TypeDef* CHILLER_UART;

// TODO: Make macros for manipulating GPIO pins (I/O) via memory banding

#define GPIO_SET(x)   GPIO_SetValue(IO_PORT(x), 1<<IO_PIN(x))
#define GPIO_CLEAR(x) GPIO_ClearValue(IO_PORT(x), 1<<IO_PIN(x))
//#define GPIO_GET(x) GPIO_GetValue(IO_PORT(x), IO_PIN(x))

void boardInit();

#endif

