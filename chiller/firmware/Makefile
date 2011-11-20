PART=atmega328p

PROGRAMMER = avrispmkII -P usb


CC=avr-gcc
CFLAGS=-Os -Wall -pedantic-errors -Werror -mcall-prologues -mmcu=${PART} -std=c99
OBJ2HEX=avr-objcopy 

AVRDUDE = avrdude -c ${PROGRAMMER} -p ${PART}

default: clean program

program: chillerctrl.hex
	$(AVRDUDE) -e -U flash:w:chillerctrl.hex

rdfuses:
	$(AVRDUDE) -B 100 -v -q

# see: http://www.engbedded.com/fusecalc/
fuses:
	$(AVRDUDE) -B 100 -u -v -U lfuse:w:0xe2:m -U hfuse:w:0xd9:m -U efuse:w:0xff:m

chillerctrl.obj : chillerctrl.c Makefile
	$(CC) $(CFLAGS) -DNODE=$(NODE) chillerctrl.c -o $@
	avr-size -C $@

%.hex : %.obj
	$(OBJ2HEX) -R .eeprom -O ihex $< $@

clean :
	rm -f *.hex *.obj *.o *.eep

