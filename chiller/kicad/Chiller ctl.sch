EESchema Schematic File Version 2  date Mon 07 Nov 2011 07:48:35 PM CET
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
LIBS:atmel
LIBS:contrib
LIBS:valves
LIBS:cazh_atmel
LIBS:dl_atmel
LIBS:Chiller ctl-cache
EELAYER 25  0
EELAYER END
$Descr A4 11700 8267
encoding utf-8
Sheet 1 1
Title ""
Date "7 nov 2011"
Rev ""
Comp ""
Comment1 ""
Comment2 ""
Comment3 ""
Comment4 ""
$EndDescr
Connection ~ 4050 4600
Wire Wire Line
	4050 4750 4050 4400
Wire Wire Line
	4050 4600 4150 4600
Wire Wire Line
	4050 4400 4150 4400
Connection ~ 4000 2400
Wire Wire Line
	4000 2300 4150 2300
Connection ~ 9300 4200
Wire Wire Line
	9300 4050 9300 4200
Wire Wire Line
	9100 4050 9100 4200
Connection ~ 8900 4200
Wire Wire Line
	9000 4200 8300 4200
Wire Wire Line
	9000 4200 9000 4050
Wire Wire Line
	8300 4200 8300 4400
Wire Wire Line
	8700 4200 8700 3250
Wire Wire Line
	8700 3250 8900 3250
Wire Wire Line
	8900 3250 8900 3350
Wire Wire Line
	9200 3350 9200 3100
Wire Wire Line
	6050 3900 7850 3900
Wire Wire Line
	7850 3900 7850 2850
Wire Wire Line
	7850 2850 9300 2850
Wire Wire Line
	9300 2850 9300 3350
Wire Wire Line
	6050 4000 7950 4000
Wire Wire Line
	7950 4000 7950 2950
Wire Wire Line
	7950 2950 9100 2950
Wire Wire Line
	9100 2950 9100 3350
Wire Wire Line
	9000 3350 9000 3050
Wire Wire Line
	9000 3050 8500 3050
Wire Wire Line
	8500 3050 8500 3150
Wire Wire Line
	8900 4200 8900 4050
Connection ~ 8700 4200
Wire Wire Line
	9100 4200 9650 4200
Wire Wire Line
	9650 4200 9650 3900
Wire Wire Line
	9200 4050 9200 4200
Connection ~ 9200 4200
Wire Wire Line
	4150 2600 4000 2600
Wire Wire Line
	4000 2400 4150 2400
Wire Wire Line
	4000 2600 4000 2100
Connection ~ 4000 2300
Wire Wire Line
	4150 4500 4050 4500
Connection ~ 4050 4500
$Comp
L GND #PWR?
U 1 1 4EB81B5E
P 4050 4750
F 0 "#PWR?" H 4050 4750 30  0001 C CNN
F 1 "GND" H 4050 4680 30  0001 C CNN
	1    4050 4750
	1    0    0    -1  
$EndComp
$Comp
L +3.3V #PWR?
U 1 1 4EB81B17
P 4000 2100
F 0 "#PWR?" H 4000 2060 30  0001 C CNN
F 1 "+3.3V" H 4000 2210 30  0000 C CNN
	1    4000 2100
	1    0    0    -1  
$EndComp
$Comp
L +24V #PWR?
U 1 1 4EB81AF2
P 9650 3900
F 0 "#PWR?" H 9650 3850 20  0001 C CNN
F 1 "+24V" H 9650 4000 30  0000 C CNN
	1    9650 3900
	1    0    0    -1  
$EndComp
$Comp
L GNDPWR #PWR?
U 1 1 4EB81AC4
P 8300 4400
F 0 "#PWR?" H 8300 4450 40  0001 C CNN
F 1 "GNDPWR" H 8300 4320 40  0000 C CNN
	1    8300 4400
	1    0    0    -1  
$EndComp
$Comp
L GND #PWR?
U 1 1 4EB80FA9
P 8500 3150
F 0 "#PWR?" H 8500 3150 30  0001 C CNN
F 1 "GND" H 8500 3080 30  0001 C CNN
	1    8500 3150
	1    0    0    -1  
$EndComp
$Comp
L +3.3V #PWR?
U 1 1 4EB80F64
P 9200 3100
F 0 "#PWR?" H 9200 3060 30  0001 C CNN
F 1 "+3.3V" H 9200 3210 30  0000 C CNN
	1    9200 3100
	1    0    0    -1  
$EndComp
$Comp
L DIL10 P?
U 1 1 4EB80F02
P 9100 3700
F 0 "P?" H 9100 4000 60  0000 C CNN
F 1 "DIL10" V 9100 3700 50  0000 C CNN
	1    9100 3700
	0    1    1    0   
$EndComp
$Comp
L ATMEGA328P-A IC?
U 1 1 4EB80EDE
P 5050 3400
F 0 "IC?" H 4350 4650 50  0000 L BNN
F 1 "ATMEGA328P-A" H 5250 2000 50  0000 L BNN
F 2 "TQFP32" H 4500 2050 50  0001 C CNN
	1    5050 3400
	1    0    0    -1  
$EndComp
$EndSCHEMATC
