This directory contains the framework for writing applications for the lpc1769, 
specifically the photonsaw boards, but for the most part, the board specific parts
should be kept in a separate subdirectory, chosen by the application make file via
the BOARD variable.


Subdirs:
  drivers: Files from the lpc17xx driver library by NXP, including some very minor bugfixes.

  photonsaw-v1: Board specific routines and configuration for the first version of the PS board.

  newlib: Stubs that allow the needed functions of newlib to work, including file system support.

  usb: The USB-CDC sub system, use the functions in usbapi.h to access it.
  
  fat_sd: The SD card based FAT file system from: http://www.siwawi.arubi.uni-kl.de/avr_projects/arm_projects/arm_memcards/#chanfat_lpc_cm3


Files:

  lpc1769.ld: Linker script for the lpc1769 (and '68)

  startup.c: Boot strap code run before main, calls out to the board specific boardInit()
             and the application main()

  adc.h & adc.c: ADC "API" routines

  fix-lpcchecksum: Perl script used to make the interrupt vector checksum correct in a bin file.

  openocd.cfg: Configuration file used to start and configure openocd (use make flash)

  gdb.cfg: Configuration file loaded by gdb (use make gdb once openocd is started)

  makefile: Do not run this manually, include it from the application specific directories


*** Flow control ***

Ignore it completely for all channels except USB-CDC and just go slow enough the receiver is always
able to keep up.

For the USB connection it's certainly possible to fill up the receive buffer,
so perhaps we should reply to each command with a status message telling
the client how much buffer space is available, so the client can figure out ahead of time
if it should wait before sending a command.

A good way to ensure proper handling of buffers and avoiding overruns is to package every command
in frames that first tells the controller how long the command is going to be before sending it.

P:Controlling PC
M:MCU

P:gcode 9        <--- Here comes an 9 char long g-code command
P:G0 X1 Y2       <--- The command itself, read from the input file.
C:ok 9991/10000  <--- 9991 bytes of 10000 are available in the g-code buffer


Other meta-commands could be implemented, like one for getting the current buffer status:

P:buffer         <--- Hey what's going on?
C:ok 40/10000    <--- 40 bytes of 10000 are available in the g-code buffer


Perhaps a non-flow-control aware fallback could be implemented that simply discards overflowing
gcode commands and returns an error, that way legacy applications will be able to talk to the
controller, but not as efficiently as a flow control aware one.
