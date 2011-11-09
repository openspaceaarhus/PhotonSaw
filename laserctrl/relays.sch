EESchema Schematic File Version 2  date 2011-11-09T19:03:07 CET
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
EELAYER 25  0
EELAYER END
$Descr A4 11700 8267
encoding utf-8
Sheet 2 12
Title ""
Date "9 nov 2011"
Rev ""
Comp ""
Comment1 ""
Comment2 ""
Comment3 ""
Comment4 ""
$EndDescr
$Comp
L R R10
U 1 1 4EB83234
P 2500 3650
F 0 "R10" V 2580 3650 50  0000 C CNN
F 1 "3k3" V 2500 3650 50  0000 C CNN
	1    2500 3650
	0    1    1    0   
$EndComp
$Comp
L NPN Q2
U 1 1 4EB83233
P 3050 3650
F 0 "Q2" H 3050 3500 50  0000 R CNN
F 1 "BC817" H 3050 3800 50  0000 R CNN
	1    3050 3650
	1    0    0    -1  
$EndComp
$Comp
L GND #PWR15
U 1 1 4EB83232
P 3150 3900
F 0 "#PWR15" H 3150 3900 30  0001 C CNN
F 1 "GND" H 3150 3830 30  0001 C CNN
	1    3150 3900
	1    0    0    -1  
$EndComp
$Comp
L CONN_2 P3
U 1 1 4EB83231
P 3750 3300
F 0 "P3" V 3700 3300 40  0000 C CNN
F 1 "Assist Air" V 3800 3300 40  0000 C CNN
	1    3750 3300
	1    0    0    -1  
$EndComp
$Comp
L DIODE D2
U 1 1 4EB83230
P 3150 3150
F 0 "D2" H 3150 3250 40  0000 C CNN
F 1 "DIODE" H 3150 3050 40  0000 C CNN
	1    3150 3150
	0    -1   -1   0   
$EndComp
$Comp
L +24V #PWR14
U 1 1 4EB8322F
P 3150 2850
F 0 "#PWR14" H 3150 2800 20  0001 C CNN
F 1 "+24V" H 3150 2950 30  0000 C CNN
	1    3150 2850
	1    0    0    -1  
$EndComp
Text HLabel 2150 3650 0    50   Input ~ 0
Assist Air
Connection ~ 3150 2900
Wire Wire Line
	3150 2900 3400 2900
Wire Wire Line
	3400 2900 3400 3200
Wire Wire Line
	2850 3650 2750 3650
Wire Wire Line
	3150 3350 3150 3450
Wire Wire Line
	3150 2850 3150 2950
Wire Wire Line
	3150 3850 3150 3900
Wire Wire Line
	3400 3400 3150 3400
Connection ~ 3150 3400
Wire Wire Line
	2150 3650 2250 3650
Wire Wire Line
	2150 2200 2250 2200
Connection ~ 3150 1950
Wire Wire Line
	3400 1950 3150 1950
Wire Wire Line
	3150 2400 3150 2450
Wire Wire Line
	3150 1400 3150 1500
Wire Wire Line
	3150 1900 3150 2000
Wire Wire Line
	2850 2200 2750 2200
Wire Wire Line
	3400 1750 3400 1450
Wire Wire Line
	3400 1450 3150 1450
Connection ~ 3150 1450
Text HLabel 2150 2200 0    50   Input ~ 0
Exhaust
$Comp
L +24V #PWR12
U 1 1 4EB83187
P 3150 1400
F 0 "#PWR12" H 3150 1350 20  0001 C CNN
F 1 "+24V" H 3150 1500 30  0000 C CNN
	1    3150 1400
	1    0    0    -1  
$EndComp
$Comp
L DIODE D1
U 1 1 4EB83128
P 3150 1700
F 0 "D1" H 3150 1800 40  0000 C CNN
F 1 "DIODE" H 3150 1600 40  0000 C CNN
	1    3150 1700
	0    -1   -1   0   
$EndComp
$Comp
L CONN_2 P2
U 1 1 4EB83119
P 3750 1850
F 0 "P2" V 3700 1850 40  0000 C CNN
F 1 "Exhaust" V 3800 1850 40  0000 C CNN
	1    3750 1850
	1    0    0    -1  
$EndComp
$Comp
L GND #PWR13
U 1 1 4EB830F3
P 3150 2450
F 0 "#PWR13" H 3150 2450 30  0001 C CNN
F 1 "GND" H 3150 2380 30  0001 C CNN
	1    3150 2450
	1    0    0    -1  
$EndComp
$Comp
L NPN Q1
U 1 1 4EB830E3
P 3050 2200
F 0 "Q1" H 3050 2050 50  0000 R CNN
F 1 "BC817" H 3050 2350 50  0000 R CNN
	1    3050 2200
	1    0    0    -1  
$EndComp
$Comp
L R R9
U 1 1 4EB830BA
P 2500 2200
F 0 "R9" V 2580 2200 50  0000 C CNN
F 1 "3k3" V 2500 2200 50  0000 C CNN
	1    2500 2200
	0    1    1    0   
$EndComp
$EndSCHEMATC
