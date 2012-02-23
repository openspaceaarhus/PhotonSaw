#include "board.h"
#include "lpc17xx_adc.h"
#include "lpc17xx_pinsel.h"

int readADC(const int pin) {
  const int channel = IO_CHAN(pin);
  
  ADC_ChannelCmd(LPC_ADC, channel, ENABLE);
  ADC_StartCmd(LPC_ADC, ADC_START_NOW);
  while (!(ADC_ChannelGetStatus(LPC_ADC, channel,ADC_DATA_DONE)));
  return ADC_ChannelGetData(LPC_ADC, channel);
}
