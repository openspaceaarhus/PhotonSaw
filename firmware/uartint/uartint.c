#include "lpc17xx_uart.h"
#include "lpc17xx_pinsel.h"

#include "uarts.h"


#define menu1 "fest fest fest fest fest\n\r"
#define menu2 "test test test test test\n\r"
#define menu3 "hest hest hest hest hest\n\r"

/*-------------------------MAIN FUNCTION------------------------------*/
/*********************************************************************//**
 * @brief		c_entry: Main UART program body
 * @param[in]	None
 * @return 		int
 **********************************************************************/
int main(void) {
  // reset exit flag
  FlagStatus exitflag = RESET;
  char buffer[256];

  sendUART(IO_DEBUG_TX, menu1, sizeof(menu1));

  
  /* Read some data from the buffer */
  while (exitflag == RESET) {
    uint32_t len = 0;
    while (len == 0) {
      len = recvUART(IO_DEBUG_RX, buffer, sizeof(buffer));
    }
    
    /* Got some data */
    int idx = 0;
    while (idx < len) {
      if (buffer[idx] == 27) {
	/* ESC key, set exit flag */
	sendUART(IO_DEBUG_TX, menu3, sizeof(menu3));
	exitflag = SET;

      } else if (buffer[idx] == 'r') {
	sendUART(IO_DEBUG_TX, menu2, sizeof(menu2));

      } else {
	/* Echo it back */
	sendUART(IO_DEBUG_TX, &buffer[idx], 1);
      }
      idx++;
    }
  }
  
  /* Loop forever */
  while(1);
  return 1;
}

