#PROJ=hest

AVR = atmega328p
#PROG = -c usbtiny
PROG = -c avrispv2 -P usb

FW = ../atmega328

CSRC  = $(wildcard $(FW)/*.c) $(wildcard $(FW)/*/*.c) $(wildcard *.c) $(wildcard */*.c)
CPSRC = $(wildcard $(FW)/*.cpp) $(wildcard $(FW)/*/*.cpp) $(wildcard *.cpp) $(wildcard */*.cpp)

OBJS = $(CSRC:%.c=%.o) $(CPSRC:%.cpp=%.o)

CC=avr-gcc
CFLAGS  = -Os -Wall -pedantic-errors -Werror -mcall-prologues -mmcu=$(AVR) -std=c99
CFLAGS  += -I$(FW)

program : $(PROJ).hex
	avrdude $(PROG) -p $(AVR) -e -U flash:w:$(PROJ).hex

# Internal oscilator:
fuses:
	avrdude $(PROG) -p $(AVR) -U lfuse:w:0x4e:m -U hfuse:w:0xdd:m

#$(PROG).obj : *.c
#	$(CC) $(CFLAGS) uart.c votron.c sleep.c -o $@


$(PROJ).hex : $(OBJS)
	$(CC) $(OBJS) -o $(PROJ).hex
#	avr-ld 
#	avr-objcopy -R .eeprom -O ihex $< $(OBJ

clean :
	rm -f *.hex *.obj *.o

