#include "api.h"
#include "laser.h"


unsigned int currentLaserPWM;
unsigned int currentLaserOn;

inline void setLaserPWM(unsigned int laserPWM) {
  laserPWM &= 0xff;
  
  if (laserPWM != currentLaserPWM) {
    currentLaserPWM = laserPWM;
    setPWM(IO_CHAN(IO_LASER_POWER), laserPWM);
  }
}

inline void setLaserFire(unsigned int laserOn) {

  if (laserOn && !currentLaserOn) {
    GPIO_SET(IO_LASER_FIRE);
    currentLaserOn = 1;

  } else if (!laserOn && currentLaserOn) {
    GPIO_CLEAR(IO_LASER_FIRE);
    currentLaserOn = 0;
  }
}
