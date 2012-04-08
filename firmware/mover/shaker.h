#ifndef __SHAKER_H__
#define __SHAKER_H__

#include "axis.h"

/*
  This is the number of micro seconds between each stepper interrupt fires,
  it is also the maximum time the IRQ routine is allowed to take.
*/

#define STEPPER_TIMER_INTERVAL_US 50

void shakerInit();

extern unsigned int stepperIRQMax;
extern unsigned int stepperIRQAvg;

unsigned int getCurrentMove();


// True if it's possible to add more move codes
char bufferIsFull();

// Add one move code to the buffer, it is not executed yet, though
void bufferMoveCode(unsigned int code);

// Release the buffered move for execution
void bufferCommit();

// Number of move codes available in the buffer
int bufferAvailable();

// The commited number of move codes ready for execution
int bufferInUse();

// True if there are no moves ready for execution
char bufferIsEmpty();



#define AXIS_X 0
#define AXIS_Y 1
#define AXIS_Z 2
#define AXIS_A 3

extern Axis axes[4];

#endif
