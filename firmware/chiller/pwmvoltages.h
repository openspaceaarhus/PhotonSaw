#pragma once

// Initialize the PWM outputs
void initPWM();

// Call once per 10 ms cycle to update the pwm outputs.
void updatePWM();

// Sets the target speed of the circulation pump in percent (0=stopped, 100=full speed)
void setCirculationSpeed(float percent);

// Sets the target speed of the cooling pump in percent (0=stopped, 100=full speed)
void setCoolingSpeed(float percent);

// Gets the actual speed of the circulation pump in percent
float getCurrentCirculationSpeed();

// Gets the actual speed of the cooling pump in percent
float getCurrentCoolingSpeed();

