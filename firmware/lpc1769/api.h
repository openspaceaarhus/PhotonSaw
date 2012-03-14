#ifndef __API_H
#define __API_H

#include "board.h"
#include "adc.h"
#include "pwm.h"
#include "uarts.h"

extern FILE *chiller;
extern FILE *watchdog;
extern volatile unsigned long systick;

void delay(unsigned long ms);

void initAPI();

#endif
