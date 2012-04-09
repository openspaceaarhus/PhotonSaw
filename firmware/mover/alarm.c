#include "api.h"
#include "alarm.h"
#include "shaker.h"
#include <string.h>

Alarm alarms[ALARMS] IN_IRAM1;
int alarmsActive;

// Sets an alarm
int alarmSet(unsigned int switches, char *message) {
  int index = -1;
  for (int i=0;i<ALARMS;i++) {
    if (!alarms[i].active) {
      index = i;
      break;
    }
  }

  if (index < 0) {
    return -1; // Well, shit has already hit the fan, it does nobody any good to remove the evidence.
  }

  alarms[index].active = 1;
  alarms[index].timestamp = systick;
  alarms[index].switches = switches;
  alarms[index].moveId = getCurrentMove();
  alarms[index].moveCodeOffset = getCurrentMoveCodeOffset();
  strncpy(alarms[index].msg, message, ALARM_MAX_LENGTH);

  alarmsActive++;

  return index;
}

// Clears an active alarm
void alarmClear(int index) {
  if (index > 0 && index < ALARMS && alarms[index].active) {
    alarms[index].active = 0;
    alarmsActive--;
  }
}

// Returns the number of non-cleared alarms

unsigned int checkAlarmInputs() {  
  if (alarmsActive) {
    return alarmsActive;
  }
  
  unsigned int sw = 0;
  if (!GPIO_GET(IO_X_MIN)) sw |= 1<<ALARM_SW_X_MIN;
  if (!GPIO_GET(IO_X_MAX)) sw |= 1<<ALARM_SW_X_MAX;
  if (!GPIO_GET(IO_Y_MIN)) sw |= 1<<ALARM_SW_Y_MIN;
  if (!GPIO_GET(IO_Y_MAX)) sw |= 1<<ALARM_SW_Y_MAX;
  if (!GPIO_GET(IO_Z_MIN)) sw |= 1<<ALARM_SW_Z_MIN;
  if (!GPIO_GET(IO_Z_MAX)) sw |= 1<<ALARM_SW_Z_MAX;
  if (!GPIO_GET(IO_A_MIN)) sw |= 1<<ALARM_SW_A_MIN;
  if (!GPIO_GET(IO_A_MAX)) sw |= 1<<ALARM_SW_A_MAX;

  if (!GPIO_GET(IO_ESTOP)) sw |= 1<<ALARM_SW_ESTOP;
  if (!GPIO_GET(IO_WD_READY)) sw |= 1<<ALARM_SW_WD_READY;

  if (sw) {
    alarmSet(sw, "One or more switches triggered");
    return 1;
  }

  return 0;
}
