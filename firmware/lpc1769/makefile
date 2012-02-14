# Set the PROJ variable before including this make file.
# PROJ = blinky

EXECNAME=$(PROJ).elf
MAPFILE=$(PROJ).map

FW = ../lpc1769

CSRC  = $(wildcard $(FW)/*.c) $(wildcard $(FW)/*/*.c) $(wildcard *.c) $(wildcard */*.c)
CPSRC = $(wildcard $(FW)/*.cpp) $(wildcard $(FW)/*/*.cpp) $(wildcard *.cpp) $(wildcard */*.cpp)

OBJS = $(CSRC:%.c=%.o) $(CPSRC:%.cpp=%.o)

CC                      = arm-none-eabi-gcc
AS                      = arm-none-eabi-as
AR                      = arm-none-eabi-ar -r
LD                      = arm-none-eabi-gcc
NM                      = arm-none-eabi-nm
OBJDUMP                 = arm-none-eabi-objdump
OBJCOPY                 = arm-none-eabi-objcopy
READELF                 = arm-none-eabi-readelf
CODESIZE                = arm-none-eabi-size
GDB                     = arm-none-eabi-gdb

CFLAGS                  = 
CFLAGS			+= -std=c99
CFLAGS			+= -Werror
CFLAGS                  += -Wall 
CFLAGS                  += -mcpu=cortex-m3
CFLAGS                  += -mthumb 
CFLAGS                  += -O0
CFLAGS                  += -mapcs-frame 
CFLAGS                  += -D__thumb2__=1 
CFLAGS                  += -D__RAM_MODE__=0
CFLAGS                  += -msoft-float 
CFLAGS                  += -gdwarf-2 
CFLAGS                  += -mno-sched-prolog 
CFLAGS                  += -fno-hosted 
CFLAGS                  += -mtune=cortex-m3 
CFLAGS                  += -march=armv7-m 
CFLAGS                  += -mfix-cortex-m3-ldrd  
CFLAGS                  += -ffunction-sections 
CFLAGS                  += -fdata-sections 
CFLAGS                  += -fno-asynchronous-unwind-tables -fno-unwind-tables
CFLAGS                  += -I$(FW) -I$(FW)/drivers

LDFLAGS = -static -mcpu=cortex-m3 -mthumb -mthumb-interwork -Wl,--start-group 
#LDFLAGS += -L$(THUMB2GNULIB) -L$(THUMB2GNULIB2)
LDFLAGS += -lgcc -lm 
LDFLAGS += -lc -lg -lstdc++ -lsupc++ 
LDFLAGS += -Wl,--end-group 
LDFLAGS += -Wl,--gc-sections
#LDFLAGS += -Wl,--print-gc-sections 
LDFLAGS += -T $(FW)/lpc1769.ld
LDFLAGS += -Xlinker -Map -Xlinker $(PROJ).map

$(EXECNAME): $(OBJS)
	$(LD) $(OBJS) $(LDFLAGS) -o $(EXECNAME)
#	$(OBJCOPY) -O ihex $(EXECNAME) $(PROJ).hex
	$(CODESIZE) $(EXECNAME)

flash: $(EXECNAME)
	$(OBJCOPY) -O binary $(EXECNAME) $(PROJ).bin
	$(FW)/fix-lpcchecksum $(PROJ).bin
	openocd -f $(FW)/openocd.cfg -c 'flash write_image erase $(PROJ).bin' -c 'verify_image $(PROJ).bin' -c 'reset run'

gdb:
	$(GDB) -x $(FW)/gdb.config $(EXECNAME)

clean:
	rm -r $(OBJS) $(EXECNAME) $(PROJ).hex $(PROJ).bin $(PROJ).map */*~ *~

