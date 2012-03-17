This directory contains the various pieces of firmware needed for the PhotonSaw.

The tools subdirectory contains a script which will download and build the entire toolchain needed along with the NXP driver code.


LPC1769 software:

The lpc1769 directory contains the low level framework (startup code, linker script, makefile, openocd config)
needed to build the lpc1769 based firmware.

blinky contains a very minimal test project that simply blinks the status LED on the PhotonSaw board.

uart blinky plus output via debug serial port and ADC readout

usbcdc gives the USB a workout.

stepper is a step motor test program


ATMega328 software:

The atmega328 directory contains the framework for the AVR based part of the firmware:

lapdog is a sample firmware for the watchdog MCU, it doesn't really protect
anything, it just flashes the LEDs and enables both steppers and the LASER.




-------------



= Thoughts on the general structure of the main firmware =


USBCDC 
 -> usbDataCallback(char *data) 
   -> usbLineCallback(char *line)
    -> Interpret command

G-code commands are buffered and the reply includes the space taken in the buffer by the command
as well as the space left to allow flow control.

     --> g: G-code + store in buffer + handshake with buffer free.
     --> q: Machine query
     --> c: Machine control                                    
     <-- r: Reply message

The host must ensure that only one command is sent to the controller at a time
and that there is room in the buffer for commands that are going to be buffered.

Once a g-code has been received it is interepreted and turned into moves which are placed into
the move buffer.

The stepper timer interrupt pops moves from the buffer whenever it's done with a move.

The move buffer is a fixed size, circular buffer of 32 bit words,
with a atomic push and a pop operations.


Each move starts with a magic word, the size of the move in words and a number which refers back
to an id assigned as each g-code command is buffered, this is to allow the front end to be told
exactly what g-code is being executed.

The commands could look something like this:

0: BB05AA00: Pause 
1: Size in words
2: g-code line number
3: ms

0: BB05AA01: Move
1: Size in words
2: g-code line number

BB05AA03: Move, with engraving pixels
1: Size in words
2: g-code line number
...

n  : Pixel 0 step number
n+1: Last pixel step number
n+2: Pixels >> 16 / tick
n+3: Pixel 0..31
n+4: Pixel 32..63
...

Each pixel is one bit.

One word is popped from the move buffer whenever the previous bunch of 32 bits has been consumed,
this allows the planner to fill the buffer earlier than if it had been forced to wait for each
move to complete.

