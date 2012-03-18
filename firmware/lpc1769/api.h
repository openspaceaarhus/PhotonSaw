#ifndef __API_H
#define __API_H

#include "board.h"
#include "adc.h"
#include "pwm.h"
#include "uarts.h"
#include <stdio.h>

#define IN_IRAM1 __attribute__ ((section (".iram1")))

extern FILE *chiller;
extern FILE *watchdog;
extern volatile unsigned long systick;

void delay(unsigned long ms);

void initAPI();

extern void usbLine(char *line);

#endif
