#define __FROM_BOARD_C

#include "board.h"
#include "lpc17xx_pinsel.h"
#include "lpc17xx_adc.h"

uint32_t BAUD[] = { 115200, 9600, 9600, 9600 };
LPC_UART_TypeDef* UARTS[] = {LPC_UART0, (LPC_UART_TypeDef *)LPC_UART1, LPC_UART2, LPC_UART3};
LPC_UART_TypeDef* DEBUG_UART;
LPC_UART_TypeDef* WATCHDOG_UART;
LPC_UART_TypeDef* CHILLER_UART;

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

// Configures a pair of pins to use as an UART
void configUart(const uint32_t txpin, const uint32_t rxpin) {
  const int uartNumber = IO_CHAN(txpin);
  LPC_UART_TypeDef *uart = UARTS[uartNumber];

  UART_CFG_Type uartConfig;
  UART_ConfigStructInit(&uartConfig);
  uartConfig.Baud_rate = BAUD[uartNumber];
  UART_Init(uart, &uartConfig);

  /* Initialize FIFOConfigStruct to default state:
   * 				- FIFO_DMAMode = DISABLE
   * 				- FIFO_Level = UART_FIFO_TRGLEV0
   * 				- FIFO_ResetRxBuf = ENABLE
   * 				- FIFO_ResetTxBuf = ENABLE
   * 				- FIFO_State = ENABLE
   */
  UART_FIFO_CFG_Type fifoConfig;
  UART_FIFOConfigStructInit(&fifoConfig);
  UART_FIFOConfig(uart, &fifoConfig);
  UART_TxCmd(uart, ENABLE);

  configPin(txpin);
  configPin(rxpin);
}

void configureADC(const uint32_t pin) {
  const int channel = IO_CHAN(pin);
  ADC_IntConfig( LPC_ADC, channel, DISABLE);
  ADC_ChannelCmd(LPC_ADC, channel, ENABLE);
  configPin(pin);
}

void boardInit() {    
  // Configure the uarts
  configUart(IO_DEBUG_TX, IO_DEBUG_RX);
  DEBUG_UART = UARTS[IO_CHAN(IO_DEBUG_TX)];

  configUart(IO_WATCHDOG_TX, IO_WATCHDOG_RX);
  WATCHDOG_UART = UARTS[IO_CHAN(IO_WATCHDOG_TX)];

  configUart(IO_CHILLER_TX, IO_CHILLER_RX);
  CHILLER_UART = UARTS[IO_CHAN(IO_CHILLER_TX)];

  // Configure the ADCs
  ADC_Init(LPC_ADC, 200000);
  configureADC(IO_AIRFLOW); 
  
  configureADC(IO_TEMP_OUT);
  configureADC(IO_TEMP_IN);
  configureADC(IO_TEMP_INTERNAL);
  configureADC(IO_VOLTAGE);
  ADC_StartCmd(LPC_ADC, ADC_START_CONTINUOUS);
  ADC_BurstCmd(LPC_ADC, 1);
   
  // Configure the PWM outputs
  

  // Set the simple I/O configuration for all the pins we use
  for (int i=0;i<ALL_PINS_SIZE;i++) {
    configPin(ALL_PINS[i]);
  }
}

