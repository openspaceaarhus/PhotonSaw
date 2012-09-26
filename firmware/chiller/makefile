PROJ=chiller
AVR = atmega88
INTERMEDIATE = parameterlist.h
include ../atmega8/makefile

parameterlist.h: generate-parameters.pl
	./generate-parameters.pl
