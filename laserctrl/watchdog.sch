EESchema Schematic File Version 2  date 2011-10-30T14:11:45 CET
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
LIBS:laserctrl-cache
EELAYER 25  0
EELAYER END
$Descr A4 11700 8267
Sheet 2 7
Title ""
Date "30 oct 2011"
Rev ""
Comp ""
Comment1 ""
Comment2 ""
Comment3 ""
Comment4 ""
$EndDescr
$Comp
L ATMEGA328-A IC?
U 1 1 4EA3E4C7
P 4675 3250
F 0 "IC?" H 3975 4500 50  0000 L BNN
F 1 "ATMEGA328-A" H 4925 1850 50  0000 L BNN
F 2 "TQFP32" H 4125 1900 50  0001 C CNN
	1    4675 3250
	1    0    0    -1  
$EndComp
$Comp
L +5V #PWR?
U 1 1 4EA3E4C6
P 4425 1650
F 0 "#PWR?" H 4425 1740 20  0001 C CNN
F 1 "+5V" H 4425 1740 30  0000 C CNN
	1    4425 1650
	1    0    0    -1  
$EndComp
$Comp
L GND #PWR?
U 1 1 4EA3E4C5
P 4425 4900
F 0 "#PWR?" H 4425 4900 30  0001 C CNN
F 1 "GND" H 4425 4830 30  0001 C CNN
	1    4425 4900
	1    0    0    -1  
$EndComp
$Comp
L C C?
U 1 1 4EA3E4C4
P 3675 4200
F 0 "C?" H 3725 4300 50  0000 L CNN
F 1 "100nF" H 3725 4100 50  0000 L CNN
	1    3675 4200
	1    0    0    -1  
$EndComp
$Comp
L GND #PWR?
U 1 1 4EA3E4C3
P 3675 4550
F 0 "#PWR?" H 3675 4550 30  0001 C CNN
F 1 "GND" H 3675 4480 30  0001 C CNN
	1    3675 4550
	1    0    0    -1  
$EndComp
$Comp
L R R?
U 1 1 4EA3E4C2
P 3675 3450
F 0 "R?" V 3755 3450 50  0000 C CNN
F 1 "10k" V 3675 3450 50  0000 C CNN
	1    3675 3450
	1    0    0    -1  
$EndComp
$Comp
L +5V #PWR?
U 1 1 4EA3E4C1
P 3675 3100
F 0 "#PWR?" H 3675 3190 20  0001 C CNN
F 1 "+5V" H 3675 3190 30  0000 C CNN
	1    3675 3100
	1    0    0    -1  
$EndComp
Text GLabel 3575 3850 0    60   Input ~ 0
~Reset
Text Label 3675 3850 3    60   ~ 0
~reset
$Comp
L CRYSTAL X?
U 1 1 4EA3E4C0
P 2975 2650
F 0 "X?" H 2975 2800 60  0000 C CNN
F 1 "10MHz" H 2975 2500 60  0000 C CNN
	1    2975 2650
	1    0    0    -1  
$EndComp
$Comp
L C C?
U 1 1 4EA3E4BF
P 2675 3150
F 0 "C?" H 2725 3250 50  0000 L CNN
F 1 "15pF" H 2725 3050 50  0000 L CNN
	1    2675 3150
	1    0    0    -1  
$EndComp
$Comp
L C C?
U 1 1 4EA3E4BE
P 3275 3150
F 0 "C?" H 3325 3250 50  0000 L CNN
F 1 "15pF" H 3325 3050 50  0000 L CNN
	1    3275 3150
	1    0    0    -1  
$EndComp
$Comp
L GND #PWR?
U 1 1 4EA3E4BD
P 2675 3450
F 0 "#PWR?" H 2675 3450 30  0001 C CNN
F 1 "GND" H 2675 3380 30  0001 C CNN
	1    2675 3450
	1    0    0    -1  
$EndComp
$Comp
L GND #PWR?
U 1 1 4EA3E4BC
P 3275 3450
F 0 "#PWR?" H 3275 3450 30  0001 C CNN
F 1 "GND" H 3275 3380 30  0001 C CNN
	1    3275 3450
	1    0    0    -1  
$EndComp
$Comp
L AVR-ISP-6 CON?
U 1 1 4EA3E4BB
P 3350 5650
F 0 "CON?" H 3270 5890 50  0000 C CNN
F 1 "AVR-ISP-6" H 3110 5420 50  0000 L BNN
F 2 "AVR-ISP-6" V 2830 5690 50  0001 C CNN
	1    3350 5650
	1    0    0    -1  
$EndComp
Text GLabel 3850 5650 2    50   Input ~ 0
MOSI
Text GLabel 2900 5550 0    50   Input ~ 0
MISO
Text GLabel 2900 5650 0    50   Input ~ 0
SCK
Text GLabel 2900 5750 0    50   Input ~ 0
~Reset
$Comp
L +5V #PWR?
U 1 1 4EA3E4BA
P 3850 5400
F 0 "#PWR?" H 3850 5490 20  0001 C CNN
F 1 "+5V" H 3850 5490 30  0000 C CNN
	1    3850 5400
	1    0    0    -1  
$EndComp
$Comp
L GND #PWR?
U 1 1 4EA3E4B9
P 3850 5900
F 0 "#PWR?" H 3850 5900 30  0001 C CNN
F 1 "GND" H 3850 5830 30  0001 C CNN
	1    3850 5900
	1    0    0    -1  
$EndComp
Wire Wire Line
	2900 5550 3225 5550
Wire Wire Line
	2900 5750 3225 5750
Wire Wire Line
	3850 5650 3475 5650
Wire Wire Line
	4525 1750 3775 1750
Wire Wire Line
	4525 1850 4525 1750
Connection ~ 4325 1750
Wire Wire Line
	4325 1850 4325 1750
Connection ~ 3275 2650
Wire Wire Line
	3275 3350 3275 3450
Connection ~ 2675 2850
Wire Wire Line
	2675 2650 2675 2950
Wire Wire Line
	3275 2650 3775 2650
Wire Wire Line
	3675 4400 3675 4550
Connection ~ 3675 3850
Wire Wire Line
	3675 3700 3675 4000
Wire Wire Line
	3675 3100 3675 3200
Connection ~ 4425 4800
Wire Wire Line
	4525 4800 4525 4750
Wire Wire Line
	4425 4750 4425 4900
Wire Wire Line
	4325 4800 4525 4800
Wire Wire Line
	4325 4750 4325 4800
Wire Wire Line
	3775 3850 3575 3850
Wire Wire Line
	2675 2850 3775 2850
Wire Wire Line
	3275 2950 3275 2650
Wire Wire Line
	2675 3350 2675 3450
Wire Wire Line
	3775 2250 3775 1750
Wire Wire Line
	4425 1650 4425 1850
Connection ~ 4425 1750
Wire Wire Line
	3850 5550 3475 5550
Wire Wire Line
	3850 5400 3850 5550
Wire Wire Line
	3850 5750 3475 5750
Wire Wire Line
	3850 5900 3850 5750
Wire Wire Line
	3225 5650 2900 5650
$EndSCHEMATC
