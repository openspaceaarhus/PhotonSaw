#PROJ=hest

AVR = atmega328p
#PROG = -c usbtiny
AVR_ISP_PROGRAMMER = -c avrispv2 -P usb

# Allow the user to have a different programmer.
-include $(HOME)/.makefile

FW = ../atmega328

CSRC  = $(wildcard $(FW)/*.c) $(wildcard $(FW)/*/*.c) $(wildcard *.c) $(wildcard */*.c)
CPSRC = $(wildcard $(FW)/*.cpp) $(wildcard $(FW)/*/*.cpp) $(wildcard *.cpp) $(wildcard */*.cpp)

OBJS = $(CSRC:%.c=%.o) $(CPSRC:%.cpp=%.o)

CC=avr-gcc
SIZE=avr-size
CFLAGS  = -Os -Wall -pedantic-errors -Werror -mcall-prologues -mmcu=$(AVR) -std=c99 -g
CFLAGS  += -I$(FW)

CPPFLAGS=-fno-exceptions

LDFLAGS = -Wl,--gc-sections -Wl,-Map,$(PROJ).map -mmcu=$(AVR) -lm

program : $(PROJ).hex
	avr-objcopy -O ihex $(PROJ).elf $(PROJ).hex
	avrdude $(AVR_ISP_PROGRAMMER) -p $(AVR) -B 1 -e -U flash:w:$(PROJ).hex

$(PROJ).hex : $(OBJS)
	$(CC) $(LDFLAGS) $(OBJS) -o $(PROJ).elf

# Internal oscilator 8MHz, no prescaler, BOD=2.7V:
fuses:
	avrdude $(AVR_ISP_PROGRAMMER) -p $(AVR) -B 100 -U lfuse:w:0xe2:m -U hfuse:w:0xd9:m -U efuse:w:0x05:m

clean :
	rm -f $(PROJ).hex $(PROJ).elf $(PROJ).map $(OBJS) 

