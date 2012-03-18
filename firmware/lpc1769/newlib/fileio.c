#include <string.h>
#include <sys/stat.h>
#include "board.h"
#include "uarts.h"

const unsigned int UART_FD[10] = {
  [2] = IO_DEBUG_TX
  
#ifdef IO_WATCHDOG_TX
  ,[3] = IO_WATCHDOG_TX
#endif
  
#ifdef IO_CHILLER_TX
  ,[4] = IO_CHILLER_TX
#endif
};

const char SD[] = "/sd/";

int _open(const char *name, int flags, int mode) {

  if (!strcmp(name, "/dev/stdin")) {
    return 0;

  } else if (!strcmp(name, "/dev/stdout")) {
    return 1;
    
  } else if (!strcmp(name, "/dev/stderr")) {
    return 2;

  } else if (!strcmp(name, "/dev/watchdog")) {
    return 3;

  } else if (!strcmp(name, "/dev/chiller")) {
    return 4;

  } else if (!strncmp(name, SD, strlen(SD))) {
    // TODO: Call down to the FAT code here.
    return -2;
  }

  return -1;
}

int _close(int file) {
  if (file < 10) {
    return -1; // We don't support closing a device.

  } else {
    // TODO: Call down to the FAT code here.
    return -1;
  }
}

int _fstat(int file, struct stat *st) {
  if (file < 10) {
    st->st_mode = S_IFCHR;
    return 0; 

  } else {
    // TODO: Call down to the FAT code here.
    return -1;
  }
}

int _isatty(int file) {
  return file <= 2 ? 1 : 0; // stderr, stdout, stdin are all terminals, of sorts.
}

int _lseek(int file, int ptr, int dir) {
  // TODO: Call down to the FAT code here.
  return 0;
}

int _read(int file, char *ptr, int len) {
  if (len == 0) return 0;

  if (file == 0 || file == 1) {
    // TODO: Call down to the CDC code here

  } else if (file < 10) {
    unsigned int port = UART_FD[file];

    if (port) {
      unsigned int res;
      while (!(res = recvUART(port, ptr, len))) {
	// Let's just wait, surely the UART will deliver...
      }
      return res;
    } else {
      return -1;
    }

  } else {
    // TODO: Call down to the FAT code here.
  }

  return -1;
}

int _write(int file, char *ptr, int len) {
  if (len == 0) return 0;

  if (file == 0 || file == 1) {
    // TODO: Call down to the CDC code here

  } else if (file < 10) {
    unsigned int port = UART_FD[file];
    if (port) {
      return sendUART(port, ptr, len);
    } else {
      return -1;
    }

  } else {
    // TODO: Call down to the FAT code here.
  }  
  return -1;
}
