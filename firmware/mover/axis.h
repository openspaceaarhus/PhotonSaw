#ifndef __AXIS_H__
#define __AXIS_H__

#include "stepper.h"

typedef struct {
  int volatile position;     // Current step position.
  Stepper stepper;

  int moveError;    // 0..ONE_STEP-1 
  int moveSpeed;    // Never negative.
  int moveDirection;// 1 if speed was negative 
  int moveAccel;    
} Axis;

void axisInit(Axis *a, 
	      const unsigned int stepPin, 
	      const unsigned int dirPin, 
	      const unsigned int enablePin, 
	      const unsigned int currentPin, 
	      const unsigned int usm0Pin, 
	      const unsigned int usm1Pin);

#define AXIS_INIT(a, name) axisInit(a, IO_ ## name ## _STEP, IO_ ## name ## _DIR, IO_ ## name ## _ENABLE, IO_ ## name ## _CURRENT, IO_ ## name ## _USM0, IO_ ## name ## _USM1)

void axisNewMove(Axis *a);
void axisSetSpeed(Axis *a, int speed);
void axisSetAccel(Axis *a, int accel);

// Call to start taking a step
void axisTick(Axis *a);

// Call a bit later to stop taking a step.
void axisTock(Axis *a);

#endif
