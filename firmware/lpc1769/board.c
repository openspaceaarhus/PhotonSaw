#define __FROM_BOARD_C

#include "board.h"
#include "adc.h"
#include "uarts.h"
#include "pwm.h"
#include "api.h"

#include "lpc17xx_pinsel.h"
#include "lpc17xx_adc.h"

volatile unsigned long systick;

void __attribute__ ((weak)) diskTick100Hz() {
  // Do nothing.
}

unsigned char div10 = 0;

void SysTick_Handler (void) {
  systick++;

  if (++div10 > 10) {
    div10 = 0;
    diskTick100Hz();
  }
}

void delay(unsigned long ms) {
  unsigned long endtick = ms+systick;

  if (endtick < systick) { // Wraparound
    while (systick); // Wait for 0 to roll around.
  }

  while (systick < endtick); 
}

// Does the basic GPIO/function configuration of a pin using the pin config constant.
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
  SysTick_Config(SystemCoreClock/1000 - 1);

  initUARTs();
  initADC();
  initPWM();
  
  // Motor drivers are active low, so let's disable all of them, until the drivers turn them on:
  GPIO_SET(IO_X_ENABLE);
  GPIO_SET(IO_Y_ENABLE);
  GPIO_SET(IO_Z_ENABLE);
  GPIO_SET(IO_A_ENABLE); 
   
  /*
    Set the simple I/O configuration for all the pins we use,    
    this will ensure that all pins have had its function selected
  */
  for (int i=0;i<ALL_PINS_SIZE;i++) {
    configPin(ALL_PINS[i]);
  }

  initAPI();
}

