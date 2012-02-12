This directory contains the various pieces of firmware needed for the PhotonSaw.

The tools subdirectory contains a script which will download and build the entire toolchain needed.

tools/fix-lpc17xx will download and fix various windows-bugs in NXPs driver library
for the lpc17xx series, which is a good source of drivers for the lpc1769 framework.

The lpc1769 directory contains the low level framework (startup code, linker script, makefile, openocd config)
needed to build the lpc1769 based firmware.

blinky contains a very minimal test project that simply blinks the status LED on the PhotonSaw board.
