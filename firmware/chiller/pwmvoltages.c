#include <avr/io.h>
#include <math.h>

#include "pwmvoltages.h"
#include "adchelper.h"

#define ADC_PER_VOLT ((4.7/(10+4.7)) / (4.650/1023))

#define P 0.07
#define I 0.0001
#define D 0.005

#define  MAX_OUTPUT 128
#define  MIN_OUTPUT 0

#define  ADC_MAX 780.0
// 100% in ADC output

float errorSumA = 0;
float lastErrorA = 0;
float outputA = 0;
float targetA = 0;
float currentA = 0;

float errorSumB = 0;
float lastErrorB = 0;
float outputB = 0;
float targetB = 0;
float currentB = 0;

void setAPWM(unsigned char value) {
  if (value) {    
    OCR1AL = value;
    TCCR1A |= _BV(COM1A1);
  } else {
    TCCR1A &=~ _BV(COM1A1);
  }
}

void setBPWM(unsigned char value) {
  if (value) {    
    OCR1BL = value;
    TCCR1A |= _BV(COM1B1);
  } else {
    TCCR1A &=~ _BV(COM1B1);
  }
}

void setCirculationSpeed(float percent) {
  targetA = (percent * ADC_MAX) / 100;
  if (targetA > 1023) {
    targetA = 1023;
  }
  if (targetA < 0) {
    targetA = 0;
  }
}

void setCoolingSpeed(float percent) {
  targetB = (percent * ADC_MAX) / 100;
  if (targetB > 1023) {
    targetB = 1023;
  }
  if (targetB < 0) {
    targetB = 0;
  }
}

float getCurrentCirculationSpeed() {
  return 100*currentA / ADC_MAX;
}

float getCurrentCoolingSpeed() {
  return 100*currentB / ADC_MAX;
}

void updatePWM() {
  
  currentA = getOsADC(2);

  float errorA = currentA - targetA;
  errorSumA += errorA;
  
  outputA = outputA
      - P*errorA 
      - I*errorSumA
      - D*(errorA-lastErrorA);

  lastErrorA = errorA;

  if (outputA > MAX_OUTPUT) {
    outputA = MAX_OUTPUT;
      
  } else if (outputA < MIN_OUTPUT) {
    outputA = MIN_OUTPUT;
  }
    
  setAPWM(floor(outputA));


  currentB = getOsADC(3);
  float errorB = currentB - targetB;
  errorSumB += errorB;
  
  outputB = outputB
      - P*errorB 
      - I*errorSumB
      - D*(errorB-lastErrorB);

  lastErrorB = errorB;

  if (outputB > MAX_OUTPUT) {
    outputB = MAX_OUTPUT;
      
  } else if (outputB < MIN_OUTPUT) {
    outputB = MIN_OUTPUT;
  }
    
  setBPWM(floor(outputB));
}

void initPWM() {
  DDRB  |= _BV(PB1);  // Circulation pump PWM
  DDRB  |= _BV(PB2);  // Cooling pump PWM

  // Set up timer 1 for fast PWM mode & the highest frequency available
  TCCR1A = _BV(WGM10);
  TCCR1B = _BV(WGM12) | _BV(CS10);
}
