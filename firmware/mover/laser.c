#include "api.h"
#include "laser.h"

unsigned int currentLaserPWM;
unsigned int currentLaserOn;

unsigned char laserPwmLUT[256];

void laserInit() {
  for (int i=0;i<256;i++) {
    laserPwmLUT[i] = i;
  }
}


