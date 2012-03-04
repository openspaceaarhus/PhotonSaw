#include <sys/stat.h>
#include "board.h"
#include "uarts.h"

int _close(int file) { return -1; }

int _fstat(int file, struct stat *st) {
  st->st_mode = S_IFCHR;
  return 0;
}

int _isatty(int file) { return 1; }

int _lseek(int file, int ptr, int dir) { return 0; }

int _open(const char *name, int flags, int mode) { return -1; }

int _read(int file, char *ptr, int len) {
  if(len == 0) return 0;

  return -1;
  /*
  while(UART_FR(UART0_ADDR) & UART_FR_RXFE);
  *ptr++ = UART_DR(UART0_ADDR);
  int todo;
  for(todo = 1; todo < len; todo++) {
    if(UART_FR(UART0_ADDR) & UART_FR_RXFE) {
      break;
    }
    *ptr++ = UART_DR(UART0_ADDR);
  }
  return todo;
  */
}

int _write(int file, char *ptr, int len) {
  
  return sendUART(IO_DEBUG_TX, ptr, len);
}
