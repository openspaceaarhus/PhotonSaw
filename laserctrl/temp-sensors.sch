EESchema Schematic File Version 2  date 2011-11-06T20:55:52 CET
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
LIBS:mounting
LIBS:23k256
LIBS:tps78233
LIBS:drv8811
LIBS:mcu-nxp
LIBS:opto-transistor-4p2
LIBS:laserctrl-cache
EELAYER 25  0
EELAYER END
$Descr A4 11700 8267
encoding utf-8
Sheet 4 11
Title ""
Date "6 nov 2011"
Rev ""
Comp ""
Comment1 ""
Comment2 ""
Comment3 ""
Comment4 ""
$EndDescr
Connection ~ 2550 4200
Wire Wire Line
	2550 4150 2550 4600
Wire Wire Line
	2550 4200 2500 4200
Connection ~ 2550 3700
Wire Wire Line
	2550 3650 2550 3750
Wire Wire Line
	2000 3500 2000 3150
Wire Wire Line
	2000 3150 2550 3150
Connection ~ 1800 5000
Wire Wire Line
	1800 5000 1800 4950
Connection ~ 1800 4400
Wire Wire Line
	1800 4400 1800 4450
Wire Wire Line
	2500 4400 2550 4400
Wire Wire Line
	1600 1100 1600 1250
Connection ~ 1600 1800
Wire Wire Line
	2100 1100 2100 1200
Wire Wire Line
	2100 1700 2100 2000
Connection ~ 1850 1800
Wire Wire Line
	1850 1800 1850 1900
Wire Wire Line
	1850 2300 1850 2400
Wire Wire Line
	1500 1800 2100 1800
Connection ~ 2100 1800
Wire Wire Line
	2100 2500 2100 2200
Connection ~ 2100 2400
Wire Wire Line
	1600 2300 1600 2400
Wire Wire Line
	1600 2400 2100 2400
Connection ~ 1850 2400
Wire Wire Line
	1600 1900 1600 1650
Wire Wire Line
	3200 1900 3200 1650
Connection ~ 3450 2400
Wire Wire Line
	3700 2400 3200 2400
Wire Wire Line
	3200 2400 3200 2300
Connection ~ 3700 2400
Wire Wire Line
	3700 2500 3700 2200
Connection ~ 3700 1800
Wire Wire Line
	3100 1800 3700 1800
Wire Wire Line
	3450 2400 3450 2300
Wire Wire Line
	3450 1900 3450 1800
Connection ~ 3450 1800
Wire Wire Line
	3700 1700 3700 2000
Wire Wire Line
	3700 1100 3700 1200
Connection ~ 3200 1800
Wire Wire Line
	3200 1100 3200 1250
Wire Wire Line
	5000 1100 5000 1250
Connection ~ 5000 1800
Wire Wire Line
	5500 1100 5500 1200
Wire Wire Line
	5500 1700 5500 2000
Connection ~ 5250 1800
Wire Wire Line
	5250 1800 5250 1900
Wire Wire Line
	5250 2300 5250 2400
Wire Wire Line
	4900 1800 5500 1800
Connection ~ 5500 1800
Wire Wire Line
	5500 2500 5500 2200
Connection ~ 5500 2400
Wire Wire Line
	5000 2300 5000 2400
Wire Wire Line
	5000 2400 5500 2400
Connection ~ 5250 2400
Wire Wire Line
	5000 1900 5000 1650
Wire Wire Line
	1300 4500 1300 4250
Connection ~ 1550 5000
Wire Wire Line
	1300 4900 1300 5000
Connection ~ 2550 5000
Wire Wire Line
	2550 5100 2550 4800
Connection ~ 2550 4400
Wire Wire Line
	1550 4900 1550 5000
Wire Wire Line
	1550 4400 1550 4500
Connection ~ 1550 4400
Connection ~ 1300 4400
Wire Wire Line
	1300 3700 1300 3850
Wire Wire Line
	1200 4400 2000 4400
Wire Wire Line
	1300 5000 2550 5000
Wire Wire Line
	2550 3150 2550 3050
Wire Wire Line
	2550 3700 2300 3700
Wire Wire Line
	2250 3950 2000 3950
Wire Wire Line
	2000 3900 2000 4200
Connection ~ 2000 3950
$Comp
L R R?
U 1 1 4EB5C802
P 2250 4200
F 0 "R?" V 2330 4200 50  0000 C CNN
F 1 "22k" V 2250 4200 50  0000 C CNN
	1    2250 4200
	0    1    1    0   
$EndComp
Text Notes 3100 4700 0    50   ~ 0
Self-heating NTC\nplaced in air flow
Text Notes 2650 3700 0    50   ~ 0
10mA\nconstant current
$Comp
L +24V #PWR?
U 1 1 4EB542F9
P 2550 3050
F 0 "#PWR?" H 2550 3000 20  0001 C CNN
F 1 "+24V" H 2550 3150 30  0000 C CNN
	1    2550 3050
	1    0    0    -1  
$EndComp
$Comp
L R R?
U 1 1 4EB542D5
P 2550 3400
F 0 "R?" V 2630 3400 50  0000 C CNN
F 1 "68R" V 2550 3400 50  0000 C CNN
	1    2550 3400
	1    0    0    -1  
$EndComp
$Comp
L PNP Q?
U 1 1 4EB542BA
P 2100 3700
F 0 "Q?" H 2100 3550 60  0000 R CNN
F 1 "BC807" V 2350 3700 60  0000 R CNN
	1    2100 3700
	-1   0    0    1   
$EndComp
$Comp
L PNP Q?
U 1 1 4EB542A8
P 2450 3950
F 0 "Q?" H 2450 3800 60  0000 R CNN
F 1 "BC807" H 2450 4100 60  0000 R CNN
	1    2450 3950
	1    0    0    1   
$EndComp
$Comp
L R R?
U 1 1 4EB528F0
P 2250 4400
F 0 "R?" V 2330 4400 50  0000 C CNN
F 1 "100k" V 2250 4400 50  0000 C CNN
	1    2250 4400
	0    1    1    0   
$EndComp
$Comp
L R R?
U 1 1 4EB528C5
P 1800 4700
F 0 "R?" V 1880 4700 50  0000 C CNN
F 1 "15k" V 1800 4700 50  0000 C CNN
	1    1800 4700
	1    0    0    -1  
$EndComp
$Comp
L C C?
U 1 1 4EB5260C
P 1550 4700
F 0 "C?" H 1450 4800 50  0000 L CNN
F 1 "100nF" H 1500 4350 50  0000 L CNN
	1    1550 4700
	-1   0    0    -1  
$EndComp
$Comp
L AGND #PWR?
U 1 1 4EB5260B
P 2550 5100
F 0 "#PWR?" H 2550 5100 40  0001 C CNN
F 1 "AGND" H 2550 5030 50  0000 C CNN
	1    2550 5100
	-1   0    0    -1  
$EndComp
Text HLabel 1200 4400 0    50   Output ~ 0
airflow
$Comp
L CONN_2 P?
U 1 1 4EB5260A
P 2900 4700
F 0 "P?" V 2850 4700 40  0000 C CNN
F 1 "CONN_2" V 2950 4700 40  0000 C CNN
	1    2900 4700
	1    0    0    -1  
$EndComp
$Comp
L DIODE D?
U 1 1 4EB52608
P 1300 4050
F 0 "D?" H 1300 4150 40  0000 C CNN
F 1 "DIODE" H 1300 3950 40  0000 C CNN
	1    1300 4050
	0    -1   -1   0   
$EndComp
$Comp
L DIODE D?
U 1 1 4EB52607
P 1300 4700
F 0 "D?" H 1300 4800 40  0000 C CNN
F 1 "DIODE" H 1300 4600 40  0000 C CNN
	1    1300 4700
	0    -1   -1   0   
$EndComp
$Comp
L +3.3VADC #PWR?
U 1 1 4EB52606
P 1300 3700
F 0 "#PWR?" H 1300 3820 20  0001 C CNN
F 1 "+3.3VADC" H 1300 3790 30  0000 C CNN
	1    1300 3700
	1    0    0    -1  
$EndComp
$Comp
L +3.3VADC #PWR?
U 1 1 4EB52599
P 5000 1100
F 0 "#PWR?" H 5000 1220 20  0001 C CNN
F 1 "+3.3VADC" H 5000 1190 30  0000 C CNN
	1    5000 1100
	1    0    0    -1  
$EndComp
$Comp
L DIODE D?
U 1 1 4EB52598
P 5000 2100
F 0 "D?" H 5000 2200 40  0000 C CNN
F 1 "DIODE" H 5000 2000 40  0000 C CNN
	1    5000 2100
	0    -1   -1   0   
$EndComp
$Comp
L DIODE D?
U 1 1 4EB52597
P 5000 1450
F 0 "D?" H 5000 1550 40  0000 C CNN
F 1 "DIODE" H 5000 1350 40  0000 C CNN
	1    5000 1450
	0    -1   -1   0   
$EndComp
$Comp
L +5V #PWR?
U 1 1 4EB52596
P 5500 1100
F 0 "#PWR?" H 5500 1190 20  0001 C CNN
F 1 "+5V" H 5500 1190 30  0000 C CNN
	1    5500 1100
	1    0    0    -1  
$EndComp
$Comp
L CONN_2 P?
U 1 1 4EB52595
P 5850 2100
F 0 "P?" V 5800 2100 40  0000 C CNN
F 1 "CONN_2" V 5900 2100 40  0000 C CNN
	1    5850 2100
	1    0    0    -1  
$EndComp
Text HLabel 4900 1800 0    50   Output ~ 0
laser-out
$Comp
L AGND #PWR?
U 1 1 4EB52594
P 5500 2500
F 0 "#PWR?" H 5500 2500 40  0001 C CNN
F 1 "AGND" H 5500 2430 50  0000 C CNN
	1    5500 2500
	-1   0    0    -1  
$EndComp
$Comp
L C C?
U 1 1 4EB52593
P 5250 2100
F 0 "C?" H 5150 2200 50  0000 L CNN
F 1 "100nF" H 5200 1750 50  0000 L CNN
	1    5250 2100
	-1   0    0    -1  
$EndComp
$Comp
L R R?
U 1 1 4EB52592
P 5500 1450
F 0 "R?" V 5580 1450 50  0000 C CNN
F 1 "10k" V 5500 1450 50  0000 C CNN
	1    5500 1450
	-1   0    0    -1  
$EndComp
$Comp
L R R?
U 1 1 4EB52540
P 3700 1450
F 0 "R?" V 3780 1450 50  0000 C CNN
F 1 "10k" V 3700 1450 50  0000 C CNN
	1    3700 1450
	-1   0    0    -1  
$EndComp
$Comp
L C C?
U 1 1 4EB5253F
P 3450 2100
F 0 "C?" H 3350 2200 50  0000 L CNN
F 1 "100nF" H 3400 1750 50  0000 L CNN
	1    3450 2100
	-1   0    0    -1  
$EndComp
$Comp
L AGND #PWR?
U 1 1 4EB5253E
P 3700 2500
F 0 "#PWR?" H 3700 2500 40  0001 C CNN
F 1 "AGND" H 3700 2430 50  0000 C CNN
	1    3700 2500
	-1   0    0    -1  
$EndComp
Text HLabel 3100 1800 0    50   Output ~ 0
laser-in
$Comp
L CONN_2 P?
U 1 1 4EB5253D
P 4050 2100
F 0 "P?" V 4000 2100 40  0000 C CNN
F 1 "CONN_2" V 4100 2100 40  0000 C CNN
	1    4050 2100
	1    0    0    -1  
$EndComp
$Comp
L +5V #PWR?
U 1 1 4EB5253C
P 3700 1100
F 0 "#PWR?" H 3700 1190 20  0001 C CNN
F 1 "+5V" H 3700 1190 30  0000 C CNN
	1    3700 1100
	1    0    0    -1  
$EndComp
$Comp
L DIODE D?
U 1 1 4EB5253B
P 3200 1450
F 0 "D?" H 3200 1550 40  0000 C CNN
F 1 "DIODE" H 3200 1350 40  0000 C CNN
	1    3200 1450
	0    -1   -1   0   
$EndComp
$Comp
L DIODE D?
U 1 1 4EB5253A
P 3200 2100
F 0 "D?" H 3200 2200 40  0000 C CNN
F 1 "DIODE" H 3200 2000 40  0000 C CNN
	1    3200 2100
	0    -1   -1   0   
$EndComp
$Comp
L +3.3VADC #PWR?
U 1 1 4EB52539
P 3200 1100
F 0 "#PWR?" H 3200 1220 20  0001 C CNN
F 1 "+3.3VADC" H 3200 1190 30  0000 C CNN
	1    3200 1100
	1    0    0    -1  
$EndComp
$Comp
L +3.3VADC #PWR?
U 1 1 4EB52427
P 1600 1100
F 0 "#PWR?" H 1600 1220 20  0001 C CNN
F 1 "+3.3VADC" H 1600 1190 30  0000 C CNN
	1    1600 1100
	1    0    0    -1  
$EndComp
$Comp
L DIODE D?
U 1 1 4EB52405
P 1600 2100
F 0 "D?" H 1600 2200 40  0000 C CNN
F 1 "DIODE" H 1600 2000 40  0000 C CNN
	1    1600 2100
	0    -1   -1   0   
$EndComp
$Comp
L DIODE D?
U 1 1 4EB523F4
P 1600 1450
F 0 "D?" H 1600 1550 40  0000 C CNN
F 1 "DIODE" H 1600 1350 40  0000 C CNN
	1    1600 1450
	0    -1   -1   0   
$EndComp
$Comp
L +5V #PWR?
U 1 1 4EB523C5
P 2100 1100
F 0 "#PWR?" H 2100 1190 20  0001 C CNN
F 1 "+5V" H 2100 1190 30  0000 C CNN
	1    2100 1100
	1    0    0    -1  
$EndComp
$Comp
L CONN_2 P?
U 1 1 4EB523A3
P 2450 2100
F 0 "P?" V 2400 2100 40  0000 C CNN
F 1 "CONN_2" V 2500 2100 40  0000 C CNN
	1    2450 2100
	1    0    0    -1  
$EndComp
Text HLabel 1500 1800 0    50   Output ~ 0
Internal
$Comp
L AGND #PWR?
U 1 1 4EB472F1
P 2100 2500
F 0 "#PWR?" H 2100 2500 40  0001 C CNN
F 1 "AGND" H 2100 2430 50  0000 C CNN
	1    2100 2500
	-1   0    0    -1  
$EndComp
$Comp
L C C?
U 1 1 4EB46A9D
P 1850 2100
F 0 "C?" H 1750 2200 50  0000 L CNN
F 1 "100nF" H 1800 1750 50  0000 L CNN
	1    1850 2100
	-1   0    0    -1  
$EndComp
$Comp
L R R?
U 1 1 4EB46A84
P 2100 1450
F 0 "R?" V 2180 1450 50  0000 C CNN
F 1 "10k" V 2100 1450 50  0000 C CNN
	1    2100 1450
	-1   0    0    -1  
$EndComp
$EndSCHEMATC
