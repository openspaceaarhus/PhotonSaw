#PROJ=hest

AVR ?= atmega88
#PROG = -c usbtiny
#AVR_ISP_PROGRAMMER = -c avrispv2 -P usb
AVR_ISP_PROGRAMMER = -c jtag2isp -P usb

# Allow the user to have a different programmer.
-include $(HOME)/.makefile

FW = ../atmega8

CSRC  = $(wildcard $(FW)/*.c) $(wildcard $(FW)/*/*.c) $(wildcard *.c) $(wildcard */*.c)
CPSRC = $(wildcard $(FW)/*.cpp) $(wildcard $(FW)/*/*.cpp) $(wildcard *.cpp) $(wildcard */*.cpp)

OBJS = $(CSRC:%.c=%.o) $(CPSRC:%.cpp=%.o)

CC=avr-gcc
SIZE=avr-size
CFLAGS  = -Os -Wall -pedantic-errors -Werror -mcall-prologues -mmcu=$(AVR) -std=c99 -g
CFLAGS  += -I$(FW)

CFLAGS += -fdata-sections -ffunction-sections

CFLAGS += -funsigned-char
CFLAGS += -funsigned-bitfields
CFLAGS += -fpack-struct
CFLAGS += -fshort-enums 

CFLAGS += -finline-limit=3 
CFLAGS += -fno-inline-small-functions 

CFLAGS += --combine -fwhole-program 

CPPFLAGS=-fno-exceptions

LDFLAGS = -lc -mmcu=$(AVR) -Wl,--gc-sections -Wl,-Map,$(PROJ).map -Wl,--print-gc-sections 
LDFLAGS += -Wl,--relax

#LDFLAGS += -L/usr/lib/avr/lib
#LDFLAGS += -lm

program : $(PROJ).hex
	avr-objcopy -O ihex $(PROJ).elf $(PROJ).hex
	avrdude $(AVR_ISP_PROGRAMMER) -p $(AVR) -B 1 -e -U flash:w:$(PROJ).hex

# This builds each source file to an OBJ and then links it to a binary at the end.
#$(PROJ).hex : $(OBJS)
#	$(CC) $(LDFLAGS) $(OBJS) -o $(PROJ).elf
#	$(SIZE) $(PROJ).elf

# This uses whole-program optimization so there's only one compilation unit:
$(PROJ).hex : $(CSRC) $(CPSRC)
	$(CC) $(CFLAGS) $(CPFLAGS) $(LDFLAGS) $(CSRC) $(CPSRC) /usr/lib/avr/lib/libm.a -o $(PROJ).elf
	$(SIZE) $(PROJ).elf

# Internal oscilator 8MHz, no prescaler, BOD=2.7V:
fuses:
	avrdude $(AVR_ISP_PROGRAMMER) -p $(AVR) -B 100 -U lfuse:w:0xe2:m -U hfuse:w:0xdc:m -U efuse:w:0xf9:m 

clean :
	rm -f $(PROJ).hex $(PROJ).elf $(PROJ).map $(OBJS) 

