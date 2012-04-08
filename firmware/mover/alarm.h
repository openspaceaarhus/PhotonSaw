#ifndef __ALARM_H__
#define __ALARM_H__

#include "board.h"

/*
  Checks the limit switches, emergency switch, watchdog ready output
  then sets an alarm if anything is wrong.  

  If any alarms are active this will always return 1 without checking anything else.
*/
unsigned int checkAlarmInputs();

#define ALARM_SW_X_MIN 0
#define ALARM_SW_Y_MIN 1
#define ALARM_SW_Z_MIN 2
#define ALARM_SW_A_MIN 3

#define ALARM_SW_X_MAX 4
#define ALARM_SW_Y_MAX 5
#define ALARM_SW_Z_MAX 6
#define ALARM_SW_A_MAX 7

#define ALARM_SW_ESTOP 8

#define ALARM_SW_WD_READY    9


#define ALARM_MAX_LENGTH 80
typedef struct {
  SYSTICK_TYPE timestamp;
  int active; // Never set or clear this manually always use alarmSet and alarmClear!
  unsigned int moveId;
  unsigned int switches;
  char msg[ALARM_MAX_LENGTH];
} Alarm;

#define ALARMS 10
extern Alarm alarms[ALARMS] IN_IRAM1;
extern int alarmsActive;

// Sets an alarm
void alarmSet(unsigned int switches, char *message);

// Clears an alarm
void alarmClear(int index);

// Returns the number of non-cleared alarms
int alarmCount();

#endif
