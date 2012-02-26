#ifndef __UARTS_H
#define __UARTS_H

extern LPC_UART_TypeDef* UARTS[];
extern LPC_UART_TypeDef* DEBUG_UART;
extern LPC_UART_TypeDef* WATCHDOG_UART;
extern LPC_UART_TypeDef* CHILLER_UART;

void initUARTs();

#endif
