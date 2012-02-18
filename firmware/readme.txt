This directory contains the various pieces of firmware needed for the PhotonSaw.

The tools subdirectory contains a script which will download and build the entire toolchain needed along with the NXP driver code.


LPC1769 software:

The lpc1769 directory contains the low level framework (startup code, linker script, makefile, openocd config)
needed to build the lpc1769 based firmware.

blinky contains a very minimal test project that simply blinks the status LED on the PhotonSaw board.

uart blinky plus output via debug serial port

usbcdc gives the USB a workout.


ATMega328 software:

The atmega328 directory contains the framework for the AVR based part of the firmware:

lapdog is a sample firmware for the watchdog MCU, it doesn't really protect
anything, it just flashes the LEDs and the enable outputs.

