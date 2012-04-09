#include "joules.h"
#include "api.h"
#include "alarm.h"

// This gets called at the highest possible frequency to detect the pulses
unsigned int waterflowPinstate;
unsigned int waterflowPulses;

void joulesPollFlow() {
  if (waterflowPinstate && !GPIO_GET(IO_WATERFLOW)) {
    waterflowPinstate = 0;
  } else if (!waterflowPinstate && GPIO_GET(IO_WATERFLOW)) {
   waterflowPinstate = 1;
    waterflowPulses++;
  }
}

// The total number of water flow pulses seen
unsigned int joulesRawFlowCount() {
  return waterflowPulses;
}

SYSTICK_TYPE lastTime;
double lastInTemp;
double lastOutTemp;
unsigned int lastPulseCount;
double totalWaterMass;
double totalJoules;
double avgPower;
double avgWaterFlow;
unsigned int coolantAlarm;


int div20;
void joulesUpdateTotals100Hz() {

  // Divide down the 100 Hz to 5 Hz, so we don't go too crazy with the floating point cycles
  if (++div20 > 19) { 
    div20 = 0;    
  } else {
    return;
  }

  double it = readNTCcelcius(IO_CHAN(IO_TEMP_IN)); 
  double ot = readNTCcelcius(IO_CHAN(IO_TEMP_OUT));

  if (it < 0 || ot < 0 || it > 50 || ot > 50) {
    // Ignore crazy values
    return;
  }
  SYSTICK_TYPE t1 = systick;
  SYSTICK_TYPE deltaTime = systickInterval(lastTime, t1);
  unsigned int p = waterflowPulses;
  
  if (lastTime && deltaTime) {
    double mass = (p-lastPulseCount)*WATERFLOW_GRAMS_PER_PULSE; // grams of water since last update
    totalWaterMass += mass/1000; // Because total watermass is in kg
    double deltaTemp = ot-it; // ot and it are in C, so the diff in K is the same as the diff in C.
    double j = WATER_HEAT_CAPACITY * mass * deltaTemp; 
    totalJoules += j;

    double power = (j * deltaTime) / 1000; // deltaTime is in ms
    avgPower += (power-avgPower) * 0.01;

    double waterFlow = (1000*mass/deltaTime); // gram / second
    avgWaterFlow += (waterFlow-avgWaterFlow) * 0.01;

    coolantAlarm = (avgWaterFlow < MINIMUM_WATER_FLOW_GS ? ALARM_COOLANT_FLOW : 0) |
      ((ot > MAXIMUM_WATER_TEMP || it < MINIMUM_WATER_TEMP) ? ALARM_COOLANT_TEMP : 0);
  }  
  
  lastPulseCount = p;
  lastTime = t1;
  lastOutTemp = it;
  lastInTemp = ot;
}

// The total mass of water, that has passed through the tube in kg 
double joulesTotalWaterMass() {
  return totalWaterMass;
}

// The total energy dissipated by the tube since poweron in joule
double joulesTotal() {
  return totalJoules;
}

// Average power currently being dissipated
double joulesCurrentPower() {
  return avgPower;
}

double joulesWaterFlow() {
  return avgWaterFlow;
}

unsigned int getCoolantAlarm() {
  return coolantAlarm;
}