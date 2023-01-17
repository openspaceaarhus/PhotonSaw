/*
 * Hardware UART interface that does unbuffered blocking transmits and buffered receives
 */
#include <stdint.h>
#include <stdio.h>

#include <avr/io.h>
#include <util/delay.h>
#include <avr/interrupt.h>
#include "uart.h"

#if defined(UCSR0A)

#define UCSRnA UCSR0A
#define UCSRnB UCSR0B
#define UDREn UDRE0
#define UDRn UDR0
#define FEn FE0
#define RXCn RXC0
#define DORn DOR0
#define RXENn RXEN0
#define TXENn TXEN0
#define U2Xn U2X0
#define UBRRnH UBRR0H
#define UBRRnL UBRR0L
#define TXCn TXC0
#define RXCIEn RXCIE0
#define USARTn_RX_vect USART0_RX_vect

#elif  defined(UCSR1A)

#define UCSRnA UCSR1A
#define UCSRnB UCSR1B
#define UDREn UDRE1
#define UDRn UDR1
#define FEn FE1
#define RXCn RXC1
#define DORn DOR1
#define RXENn RXEN1
#define TXENn TXEN1
#define U2Xn U2X1
#define UBRRnH UBRR1H
#define UBRRnL UBRR1L
#define TXCn TXC1
#define RXCIEn RXCIE1
#define USARTn_RX_vect USART1_RX_vect

#else
#error "Neither uart 0 or uart 1 exist"
#endif

// ==========================================================================================
// TX
// ==========================================================================================

void uartAwaitTxIdle() {  
  loop_until_bit_is_set(UCSRnA, UDREn);
  loop_until_bit_is_set(UCSRnA, TXCn);
}

void uartPutByte(uint8_t c) {
  loop_until_bit_is_set(UCSRnA, UDREn);
  UDRn = c;
  UCSRnA |= _BV(TXCn);
}

int uart_putchar(char c, FILE *stream){

  if (c == '\n') {
    uart_putchar('\r', stream);
  }
  uartPutByte(c);
  
  return 0;
}



// ==========================================================================================
// RX
// ==========================================================================================

#define RX_BUFFER_BIT_SIZE 7
#define RX_BUFFER_SIZE _BV(RX_BUFFER_BIT_SIZE)
#define RX_BUFFER_LAST_ELEMENT (RX_BUFFER_SIZE-1)
char rxBuffer[RX_BUFFER_SIZE];
uint8_t rxTail;
uint8_t rxHead; // The location where the next ch will be written

UartLineHandler rxLineHandler = 0;

UartLineHandler uartSetLineHandler(UartLineHandler newHandler) {
  UartLineHandler old=rxLineHandler;
  rxLineHandler = newHandler;
  return old;  
}

void putRx(char ch) {
  rxBuffer[rxHead] = ch;
  rxHead = (rxHead+1) & RX_BUFFER_LAST_ELEMENT;
  
  // If a newline is seen and there's a line handler active, call it to let it consume the line.
  if (rxLineHandler && ch == '\n') {
    (*rxLineHandler)();    
  }  
}

uint8_t rxFull() {
  return ((rxTail+1)&RX_BUFFER_LAST_ELEMENT) == rxHead;
}

uint8_t rxEmpty() {
  return rxTail == rxHead;
}

char getRx() {
  if (rxEmpty()) {
    return 0;    
  } else {
    char ch = rxBuffer[rxTail];
    rxTail = (rxTail+1) & RX_BUFFER_LAST_ELEMENT;
    return ch;
  }
}

// Called when a character has been received 
ISR(USARTn_RX_vect) {
//  uint8_t status = UCSRnA;    
  putRx(UDRn);
}

int uart_getchar(FILE *stream) {
  if (rxEmpty()) {
    return 0xffff;    
  } else {
    return getRx();    
  }
}

int _uart_getchar(FILE *stream) {
  if (UCSRnA & _BV(RXCn)) {
    if (UCSRnA & _BV(FEn))
      return _FDEV_EOF;
    if (UCSRnA & _BV(DORn))
      return _FDEV_ERR;
    
    return UDRn;
  } else {
    return 0xffff;
  }
}


// ==========================================================================================
// Common initialization 
// ==========================================================================================

void uartInit(void) {
  UCSRnA = _BV(U2Xn);   
  UBRRnH = 0;
  UBRRnL = (F_CPU / (8UL * UART_BAUD)) - 1;
  UCSRnB = _BV(TXENn) | _BV(RXENn) | _BV(RXCIEn); /* tx/rx enable, interrupt on RX complete */
  
  static FILE uart_output = FDEV_SETUP_STREAM(uart_putchar, NULL, _FDEV_SETUP_WRITE);
  static FILE uart_input = FDEV_SETUP_STREAM(NULL, uart_getchar, _FDEV_SETUP_READ);
  
  stdout = &uart_output;
  stdin  = &uart_input;
  
  rxTail = rxHead = 0;
  rxLineHandler = 0;
  
  sei(); // Globally enable interrupts
}




