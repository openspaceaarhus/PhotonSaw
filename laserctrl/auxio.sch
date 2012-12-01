EESchema Schematic File Version 2  date Sat 01 Dec 2012 10:01:00 PM CET
LIBS:power
LIBS:device
LIBS:transistors
LIBS:conn
LIBS:linear
LIBS:regul
LIBS:74xx
LIBS:cmos4000
LIBS:adc-dac
LIBS:memory
LIBS:xilinx
LIBS:special
LIBS:microcontrollers
LIBS:dsp
LIBS:microchip
LIBS:analog_switches
LIBS:motorola
LIBS:texas
LIBS:intel
LIBS:audio
LIBS:interface
LIBS:digital-audio
LIBS:philips
LIBS:display
LIBS:cypress
LIBS:siliconi
LIBS:opto
LIBS:contrib
LIBS:valves
LIBS:mounting
LIBS:23k256
LIBS:tps78233
LIBS:drv8811
LIBS:mcu-nxp
LIBS:opto-transistor-4p2
LIBS:atmega328p-a
LIBS:atmel
LIBS:microsd
LIBS:jumper3
LIBS:diode2
LIBS:78l00a
LIBS:conn_11x2
LIBS:bc807
LIBS:bc817
LIBS:vo-2630
LIBS:little-logic
LIBS:con-molex
LIBS:con-molex-2
LIBS:laserctrl-cache
EELAYER 25  0
EELAYER END
$Descr A4 11700 8267
encoding utf-8
Sheet 2 11
Title ""
Date "17 oct 2012"
Rev ""
Comp ""
Comment1 ""
Comment2 ""
Comment3 ""
Comment4 ""
$EndDescr
Text Notes 2200 2300 0    50   ~ 0
This is part of the 40 pin IO header that connects to\nthe bulkhead breakout board.\n\nNotice that these GPIO pins are unprotected and need proper protection\ncircuitry if they are needed further out.
Wire Wire Line
	3700 2000 3600 2000
Wire Wire Line
	3600 1800 3750 1800
Wire Wire Line
	3600 1600 3750 1600
Wire Wire Line
	3600 1400 3850 1400
Wire Wire Line
	2650 2000 2800 2000
Wire Wire Line
	2650 1800 2800 1800
Wire Wire Line
	2650 1600 2800 1600
Wire Wire Line
	2600 1400 2800 1400
Wire Wire Line
	2700 1300 2800 1300
Wire Wire Line
	2700 1500 2800 1500
Wire Wire Line
	2650 1700 2800 1700
Wire Wire Line
	2650 1900 2800 1900
Wire Wire Line
	3600 1300 3700 1300
Wire Wire Line
	3600 1500 3700 1500
Wire Wire Line
	3600 1700 3750 1700
Wire Wire Line
	3600 1900 3750 1900
$Comp
L GND #PWR016
U 1 1 507B0CE5
P 3700 2000
F 0 "#PWR016" H 3700 2000 30  0001 C CNN
F 1 "GND" H 3700 1930 30  0001 C CNN
	1    3700 2000
	0    -1   -1   0   
$EndComp
Text HLabel 2650 2000 0    50   BiDi ~ 0
aux8
Text HLabel 3750 1900 2    50   BiDi ~ 0
aux7
Text HLabel 2650 1900 0    50   BiDi ~ 0
aux6
Text HLabel 3750 1800 2    50   BiDi ~ 0
aux5
Text HLabel 2650 1800 0    50   BiDi ~ 0
aux4
Text HLabel 3750 1700 2    50   BiDi ~ 0
aux3
Text HLabel 2650 1700 0    50   BiDi ~ 0
aux2
Text HLabel 3750 1600 2    50   BiDi ~ 0
aux1
Text HLabel 2650 1600 0    50   BiDi ~ 0
aux0
$Comp
L +24V #PWR017
U 1 1 507B0BE8
P 3700 1500
F 0 "#PWR017" H 3700 1450 20  0001 C CNN
F 1 "+24V" H 3700 1600 30  0000 C CNN
	1    3700 1500
	0    1    1    0   
$EndComp
$Comp
L GND #PWR018
U 1 1 507B0BE0
P 2700 1500
F 0 "#PWR018" H 2700 1500 30  0001 C CNN
F 1 "GND" H 2700 1430 30  0001 C CNN
	1    2700 1500
	0    1    1    0   
$EndComp
$Comp
L +3.3VADC #PWR019
U 1 1 507B0BCB
P 3850 1400
F 0 "#PWR019" H 3850 1520 20  0001 C CNN
F 1 "+3.3VADC" H 3850 1490 30  0000 C CNN
	1    3850 1400
	0    1    1    0   
$EndComp
$Comp
L AGND #PWR020
U 1 1 507B0BAC
P 2600 1400
F 0 "#PWR020" H 2600 1400 40  0001 C CNN
F 1 "AGND" H 2600 1330 50  0000 C CNN
	1    2600 1400
	0    1    1    0   
$EndComp
$Comp
L +3.3V #PWR021
U 1 1 507B0BA0
P 3700 1300
F 0 "#PWR021" H 3700 1260 30  0001 C CNN
F 1 "+3.3V" H 3700 1410 30  0000 C CNN
	1    3700 1300
	0    1    1    0   
$EndComp
$Comp
L GND #PWR022
U 1 1 507B0B88
P 2700 1300
F 0 "#PWR022" H 2700 1300 30  0001 C CNN
F 1 "GND" H 2700 1230 30  0001 C CNN
	1    2700 1300
	0    1    1    0   
$EndComp
$Comp
L CONN_8X2 P2
U 1 1 507B0B80
P 3200 1650
F 0 "P2" H 3200 2100 60  0000 C CNN
F 1 "auxio" V 3200 1650 50  0000 C CNN
	1    3200 1650
	1    0    0    -1  
$EndComp
$EndSCHEMATC
