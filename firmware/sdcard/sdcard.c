#include <string.h>
#include <stdio.h>


#include "api.h"
#include "fat_sd/ff.h"

int main(void) {
  fiprintf(stderr, "Power Up!\n\r");
  
  FATFS fs;
  FRESULT mr = f_mount(0, &fs);
  fiprintf(stderr, "Mount result: %d\n\r", mr);
  /*
  FRESULT r = f_mkfs(0, 0, 0);
  fiprintf(stderr, "MKFS result: %d\n\r", r);
  */

  while (1) {
    GPIO_SET(IO_LED);
    GPIO_SET(IO_ASSIST_AIR);
    GPIO_CLEAR(IO_EXHAUST);
    GPIO_CLEAR(IO_LASER_FIRE);
    delay(200);

    GPIO_CLEAR(IO_LED);
    GPIO_CLEAR(IO_ASSIST_AIR);
    GPIO_SET(IO_EXHAUST);
    GPIO_SET(IO_LASER_FIRE);
    delay(200);
  }
}
