#ifndef __AXIS_H__
#define __AXIS_H__

#include "stepper.h"
#include "move.h"
#include "api.h"

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

void inline axisNewMove(Axis *a) {
  a->moveError = a->moveSpeed = a->moveDirection = a->moveAccel = 0;
}

void inline axisSetSpeed(Axis *a, int speed) {
  if (speed < 0) {
    a->moveSpeed = -speed;
    a->moveDirection = 1;
    GPIO_SET(a->stepper.dirPin);
    
  } else {
    a->moveSpeed = speed;    
    GPIO_CLEAR(a->stepper.dirPin);
  }
}

void inline axisSetAccel(Axis *a, int accel) {
  a->moveAccel = accel;
}

void inline axisTick(Axis *a) {
  a->moveError += a->moveSpeed;

  if (a->moveError >= ONE_STEP) {
    a->moveError -= ONE_STEP;

    GPIO_SET(a->stepper.stepPin);
    if (a->moveDirection) {
      a->position--;
    } else {
      a->position++;
    }
  }

  if (a->moveAccel) {
    a->moveSpeed += a->moveAccel;
  } 
}

void inline axisTock(Axis *a) {
  GPIO_CLEAR(a->stepper.stepPin);
}


#endif
