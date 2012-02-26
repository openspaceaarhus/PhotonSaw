#define __FROM_BOARD_C

#include "board.h"
#include "adc.h"
#include "uarts.h"

#include "lpc17xx_pinsel.h"
#include "lpc17xx_adc.h"

// Does the basic GPIO/function configuration of a pin
void configPin(const uint32_t pin) {

  PINSEL_CFG_Type PinCfg;

  PinCfg.Pinnum = IO_PIN(pin);
  PinCfg.Portnum = IO_PORT(pin);
  PinCfg.Funcnum = IO_FUNC(pin);

  PinCfg.OpenDrain = 0;
  PinCfg.Pinmode = 0;

  PINSEL_ConfigPin(&PinCfg);

  GPIO_SetDir(IO_PORT(pin), 1<<IO_PIN(pin), IO_OUTPUT(pin) ? 1 : 0);
}

void boardInit() {    
  initUARTs();
  initADC();

  
   
  /*
    Set the simple I/O configuration for all the pins we use,    
    this will ensure that all pins have had its function selected.
  */
  for (int i=0;i<ALL_PINS_SIZE;i++) {
    configPin(ALL_PINS[i]);
  }
}

