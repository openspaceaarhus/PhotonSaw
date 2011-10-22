EESchema Schematic File Version 2  date 2011-10-22T21:04:28 CEST
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
EELAYER 25  0
EELAYER END
$Descr A4 11700 8267
Sheet 1 5
Title ""
Date "22 oct 2011"
Rev ""
Comp ""
Comment1 ""
Comment2 ""
Comment3 ""
Comment4 ""
$EndDescr
Text Notes 1150 3475 0    50   ~ 0
FTDI serial
NoConn ~ 1225 4000
NoConn ~ 1225 3900
NoConn ~ 1225 3600
Wire Wire Line
	1100 3700 1225 3700
Wire Wire Line
	1100 4225 1100 4100
Wire Wire Line
	1100 4100 1225 4100
Wire Wire Line
	6350 2775 6550 2775
Wire Wire Line
	6550 2475 6350 2475
Wire Wire Line
	6350 1350 6550 1350
Wire Wire Line
	6350 1650 6550 1650
Wire Wire Line
	6350 3475 6550 3475
Wire Wire Line
	2725 5775 2675 5775
Wire Wire Line
	2125 5925 2175 5925
Wire Wire Line
	2125 5625 2175 5625
Wire Wire Line
	1225 5675 1275 5675
Wire Wire Line
	1575 4850 1250 4850
Wire Wire Line
	2200 5100 2200 4950
Wire Wire Line
	2200 4950 1825 4950
Wire Wire Line
	2200 4600 2200 4750
Wire Wire Line
	2200 4750 1825 4750
Wire Wire Line
	4600 3900 4450 3900
Wire Wire Line
	4600 3700 4450 3700
Wire Wire Line
	4600 3500 4450 3500
Wire Wire Line
	4600 3300 4450 3300
Wire Wire Line
	4450 3000 4600 3000
Wire Wire Line
	4600 2800 4450 2800
Wire Wire Line
	4600 2600 4450 2600
Wire Wire Line
	4600 2400 4450 2400
Wire Wire Line
	4450 2100 4600 2100
Wire Wire Line
	4450 1900 4600 1900
Wire Wire Line
	4450 1700 4600 1700
Connection ~ 3200 1300
Wire Wire Line
	3200 1200 3200 1400
Wire Wire Line
	2550 1800 2550 1300
Wire Wire Line
	1450 2900 1450 3000
Wire Wire Line
	2050 2500 2050 2200
Wire Wire Line
	1450 2400 2550 2400
Wire Wire Line
	2550 3400 2350 3400
Wire Wire Line
	3100 4300 3100 4350
Wire Wire Line
	3100 4350 3300 4350
Wire Wire Line
	3200 4300 3200 4450
Wire Wire Line
	3300 4350 3300 4300
Connection ~ 3200 4350
Wire Wire Line
	2450 2650 2450 2750
Wire Wire Line
	2450 3250 2450 3550
Connection ~ 2450 3400
Wire Wire Line
	2450 3950 2450 4100
Wire Wire Line
	2050 2200 2550 2200
Wire Wire Line
	1450 2200 1450 2500
Connection ~ 1450 2400
Wire Wire Line
	2050 2900 2050 3000
Connection ~ 2050 2200
Wire Wire Line
	3100 1400 3100 1300
Connection ~ 3100 1300
Wire Wire Line
	3300 1400 3300 1300
Wire Wire Line
	3300 1300 2550 1300
Wire Wire Line
	4600 1800 4450 1800
Wire Wire Line
	4600 2000 4450 2000
Wire Wire Line
	4450 2200 4600 2200
Wire Wire Line
	4450 2500 4600 2500
Wire Wire Line
	4450 2700 4600 2700
Wire Wire Line
	4450 2900 4600 2900
Wire Wire Line
	4450 3100 4600 3100
Wire Wire Line
	4450 3400 4600 3400
Wire Wire Line
	4450 3600 4600 3600
Wire Wire Line
	4450 3800 4600 3800
Wire Wire Line
	4450 4000 4600 4000
Wire Wire Line
	2200 4850 1825 4850
Wire Wire Line
	1250 4950 1575 4950
Wire Wire Line
	1250 4750 1575 4750
Wire Wire Line
	1275 5875 1225 5875
Wire Wire Line
	1675 5350 1675 5375
Wire Wire Line
	1675 6175 1675 6225
Wire Wire Line
	2175 5775 2125 5775
Wire Wire Line
	2725 5625 2675 5625
Wire Wire Line
	2675 5925 2725 5925
Wire Wire Line
	6550 3625 6350 3625
Wire Wire Line
	6550 3325 6350 3325
Wire Wire Line
	6550 1500 6350 1500
Wire Wire Line
	6350 2625 6550 2625
Wire Wire Line
	6350 2325 6550 2325
Wire Wire Line
	6350 3775 6550 3775
Wire Wire Line
	6350 1800 6550 1800
Wire Wire Line
	1225 3800 1100 3800
$Comp
L GND #PWR1
U 1 1 4EA297C6
P 1100 4225
F 0 "#PWR1" H 1100 4225 30  0001 C CNN
F 1 "GND" H 1100 4155 30  0001 C CNN
	1    1100 4225
	1    0    0    -1  
$EndComp
Text GLabel 1100 3800 0    50   Input ~ 0
RXD
Text GLabel 1100 3700 0    50   Input ~ 0
TXD
$Comp
L CONN_6 P1
U 1 1 4EA29785
P 1575 3850
F 0 "P1" V 1525 3850 60  0000 C CNN
F 1 "CONN_6" V 1625 3850 60  0000 C CNN
	1    1575 3850
	1    0    0    -1  
$EndComp
$Comp
L CONN_5 P2
U 1 1 4EA29595
P 4900 5075
F 0 "P2" V 4850 5075 50  0000 C CNN
F 1 "CONN_5" V 4950 5075 50  0000 C CNN
	1    4900 5075
	1    0    0    -1  
$EndComp
Text GLabel 6350 3775 0    50   Input ~ 0
PD7
Text GLabel 6350 2775 0    50   Input ~ 0
PD6
Text GLabel 6350 1800 0    50   Input ~ 0
PD5
Text Notes 4825 4025 0    50   ~ 0
Limit Z
Text Notes 4825 1925 0    50   ~ 0
STOP
Text Notes 4825 3925 0    50   ~ 0
Limit Y
Text Notes 4825 3825 0    50   ~ 0
Limit X
Text Notes 4825 3725 0    50   ~ 0
LASER - ~Fire
Text Notes 4825 3625 0    50   ~ 0
LASER - PWM
Text Notes 4825 3525 0    50   ~ 0
RAM ~CS
Text Notes 4825 2925 0    50   ~ 0
Motor Z - STEP
Text Notes 4825 2825 0    50   ~ 0
Motor Z - DIR
Text Notes 4825 2725 0    50   ~ 0
Motor Y - STEP
Text Notes 4825 2625 0    50   ~ 0
Motor Y - DIR
Text Notes 4825 2525 0    50   ~ 0
Motor X - STEP
Text Notes 4825 2425 0    50   ~ 0
Motor X - DIR
Text Notes 4825 1725 0    50   ~ 0
Enable motor drivers
Text GLabel 6350 3325 0    50   Input ~ 0
PB0
Text GLabel 6350 2325 0    50   Input ~ 0
PB0
Text GLabel 6350 1350 0    50   Input ~ 0
PB0
Text GLabel 6350 3625 0    50   Input ~ 0
PC5
Text GLabel 6350 3475 0    50   Input ~ 0
PC4
Text GLabel 6350 2625 0    50   Input ~ 0
PC3
Text GLabel 6350 2475 0    50   Input ~ 0
PC2
Text GLabel 6350 1650 0    50   Input ~ 0
PC1
Text GLabel 6350 1500 0    50   Input ~ 0
PC0
$Sheet
S 6550 3150 700  825 
U 4EA1F56E
F0 "driver-Z" 60
F1 "driver.sch" 60
F2 "DIR" I L 6550 3475 60 
F3 "STEP" I L 6550 3625 60 
F4 "~ENABLE" I L 6550 3325 60 
F5 "Limit" I L 6550 3775 60 
$EndSheet
$Sheet
S 6550 2150 700  800 
U 4EA1D353
F0 "driver-Y" 60
F1 "driver.sch" 60
F2 "DIR" I L 6550 2475 60 
F3 "STEP" I L 6550 2625 60 
F4 "~ENABLE" I L 6550 2325 60 
F5 "Limit" I L 6550 2775 60 
$EndSheet
$Sheet
S 6550 1175 700  775 
U 4EA1D331
F0 "driver-X" 60
F1 "driver.sch" 60
F2 "DIR" I L 6550 1650 60 
F3 "STEP" I L 6550 1500 60 
F4 "~ENABLE" I L 6550 1350 60 
F5 "Limit" I L 6550 1800 60 
$EndSheet
$Comp
L R R3
U 1 1 4EA1C995
P 2425 5925
F 0 "R3" V 2505 5925 50  0000 C CNN
F 1 "200R" V 2425 5925 50  0000 C CNN
	1    2425 5925
	0    1    1    0   
$EndComp
$Comp
L R R2
U 1 1 4EA1C98F
P 2425 5775
F 0 "R2" V 2505 5775 50  0000 C CNN
F 1 "200R" V 2425 5775 50  0000 C CNN
	1    2425 5775
	0    1    1    0   
$EndComp
$Comp
L R R1
U 1 1 4EA1C97E
P 2425 5625
F 0 "R1" V 2505 5625 50  0000 C CNN
F 1 "200R" V 2425 5625 50  0000 C CNN
	1    2425 5625
	0    -1   -1   0   
$EndComp
$Comp
L GND #PWR5
U 1 1 4EA1C962
P 1675 6225
F 0 "#PWR5" H 1675 6225 30  0001 C CNN
F 1 "GND" H 1675 6155 30  0001 C CNN
	1    1675 6225
	1    0    0    -1  
$EndComp
$Comp
L +3.3V #PWR2
U 1 1 4EA1C8DE
P 1225 5675
F 0 "#PWR2" H 1225 5635 30  0001 C CNN
F 1 "+3.3V" H 1225 5785 30  0000 C CNN
	1    1225 5675
	0    -1   -1   0   
$EndComp
$Comp
L +3.3V #PWR4
U 1 1 4EA1C8CD
P 1675 5350
F 0 "#PWR4" H 1675 5310 30  0001 C CNN
F 1 "+3.3V" H 1675 5460 30  0000 C CNN
	1    1675 5350
	1    0    0    -1  
$EndComp
Text GLabel 1225 5875 0    50   Input ~ 0
PD2
Text GLabel 2725 5925 2    50   Input ~ 0
MOSI
Text GLabel 2725 5775 2    50   Input ~ 0
MISO
Text GLabel 2725 5625 2    50   Input ~ 0
SCK
$Comp
L 23K256 IC1
U 1 1 4EA1C521
P 1675 5775
F 0 "IC1" H 1675 5875 60  0000 C CNN
F 1 "23K256" H 1675 5775 60  0000 C CNN
	1    1675 5775
	1    0    0    -1  
$EndComp
$Comp
L GND #PWR8
U 1 1 4EA1C108
P 2200 5100
F 0 "#PWR8" H 2200 5100 30  0001 C CNN
F 1 "GND" H 2200 5030 30  0001 C CNN
	1    2200 5100
	1    0    0    -1  
$EndComp
$Comp
L +5V #PWR7
U 1 1 4EA1C100
P 2200 4600
F 0 "#PWR7" H 2200 4690 20  0001 C CNN
F 1 "+5V" H 2200 4690 30  0000 C CNN
	1    2200 4600
	1    0    0    -1  
$EndComp
Text GLabel 1250 4950 0    50   Input ~ 0
~Reset
Text GLabel 1250 4850 0    50   Input ~ 0
SCK
Text GLabel 1250 4750 0    50   Input ~ 0
MISO
Text GLabel 2200 4850 2    50   Input ~ 0
MOSI
$Comp
L AVR-ISP-6 CON1
U 1 1 4EA1C0B8
P 1700 4850
F 0 "CON1" H 1620 5090 50  0000 C CNN
F 1 "AVR-ISP-6" H 1460 4620 50  0000 L BNN
F 2 "AVR-ISP-6" V 1180 4890 50  0001 C CNN
	1    1700 4850
	1    0    0    -1  
$EndComp
Text GLabel 4600 4000 2    50   Input ~ 0
PD7
Text GLabel 4600 3900 2    50   Input ~ 0
PD6
Text GLabel 4600 3800 2    50   Input ~ 0
PD5
Text GLabel 4600 3700 2    50   Input ~ 0
PD4
Text GLabel 4600 3600 2    50   Input ~ 0
PD3
Text GLabel 4600 3500 2    50   Input ~ 0
PD2
Text GLabel 4600 2200 2    50   Input ~ 0
SCK
Text GLabel 4600 2100 2    50   Input ~ 0
MISO
Text GLabel 4600 2000 2    50   Input ~ 0
MOSI
Text GLabel 4600 1900 2    50   Input ~ 0
PB2
Text GLabel 4600 1800 2    50   Input ~ 0
PB1
Text GLabel 4600 1700 2    50   Input ~ 0
PB0
Text GLabel 4600 3100 2    50   Input ~ 0
ADC7
Text GLabel 4600 3000 2    50   Input ~ 0
ADC6
Text GLabel 4600 2900 2    50   Input ~ 0
PC5
Text GLabel 4600 2800 2    50   Input ~ 0
PC4
Text GLabel 4600 2700 2    50   Input ~ 0
PC3
Text GLabel 4600 2600 2    50   Input ~ 0
PC2
Text GLabel 4600 2500 2    50   Input ~ 0
PC1
Text GLabel 4600 2400 2    50   Input ~ 0
PC0
Text GLabel 4600 3400 2    50   Input ~ 0
TXD
Text GLabel 4600 3300 2    50   Input ~ 0
RXD
$Comp
L GND #PWR6
U 1 1 4EA1B8D4
P 2050 3000
F 0 "#PWR6" H 2050 3000 30  0001 C CNN
F 1 "GND" H 2050 2930 30  0001 C CNN
	1    2050 3000
	1    0    0    -1  
$EndComp
$Comp
L GND #PWR3
U 1 1 4EA1B8D0
P 1450 3000
F 0 "#PWR3" H 1450 3000 30  0001 C CNN
F 1 "GND" H 1450 2930 30  0001 C CNN
	1    1450 3000
	1    0    0    -1  
$EndComp
$Comp
L C C2
U 1 1 4EA1B8C4
P 2050 2700
F 0 "C2" H 2100 2800 50  0000 L CNN
F 1 "15pF" H 2100 2600 50  0000 L CNN
	1    2050 2700
	1    0    0    -1  
$EndComp
$Comp
L C C1
U 1 1 4EA1B8BB
P 1450 2700
F 0 "C1" H 1500 2800 50  0000 L CNN
F 1 "15pF" H 1500 2600 50  0000 L CNN
	1    1450 2700
	1    0    0    -1  
$EndComp
$Comp
L CRYSTAL X1
U 1 1 4EA1B8A7
P 1750 2200
F 0 "X1" H 1750 2350 60  0000 C CNN
F 1 "20MHz" H 1750 2050 60  0000 C CNN
	1    1750 2200
	1    0    0    -1  
$EndComp
Text Label 2450 3400 3    60   ~ 0
~reset
Text GLabel 2350 3400 0    60   Input ~ 0
~Reset
$Comp
L +5V #PWR9
U 1 1 4EA1B75C
P 2450 2650
F 0 "#PWR9" H 2450 2740 20  0001 C CNN
F 1 "+5V" H 2450 2740 30  0000 C CNN
	1    2450 2650
	1    0    0    -1  
$EndComp
$Comp
L R R4
U 1 1 4EA1B741
P 2450 3000
F 0 "R4" V 2530 3000 50  0000 C CNN
F 1 "10k" V 2450 3000 50  0000 C CNN
	1    2450 3000
	1    0    0    -1  
$EndComp
$Comp
L GND #PWR10
U 1 1 4EA1B73A
P 2450 4100
F 0 "#PWR10" H 2450 4100 30  0001 C CNN
F 1 "GND" H 2450 4030 30  0001 C CNN
	1    2450 4100
	1    0    0    -1  
$EndComp
$Comp
L C C3
U 1 1 4EA1B731
P 2450 3750
F 0 "C3" H 2500 3850 50  0000 L CNN
F 1 "100nF" H 2500 3650 50  0000 L CNN
	1    2450 3750
	1    0    0    -1  
$EndComp
$Comp
L GND #PWR12
U 1 1 4EA1B63A
P 3200 4450
F 0 "#PWR12" H 3200 4450 30  0001 C CNN
F 1 "GND" H 3200 4380 30  0001 C CNN
	1    3200 4450
	1    0    0    -1  
$EndComp
$Comp
L +5V #PWR11
U 1 1 4EA1AD2E
P 3200 1200
F 0 "#PWR11" H 3200 1290 20  0001 C CNN
F 1 "+5V" H 3200 1290 30  0000 C CNN
	1    3200 1200
	1    0    0    -1  
$EndComp
$Comp
L ATMEGA328-A IC2
U 1 1 4EA1AD10
P 3450 2800
F 0 "IC2" H 2750 4050 50  0000 L BNN
F 1 "ATMEGA328-A" H 3700 1400 50  0000 L BNN
F 2 "TQFP32" H 2900 1450 50  0001 C CNN
	1    3450 2800
	1    0    0    -1  
$EndComp
$Sheet
S 6300 5375 950  725 
U 4EA1A825
F0 "Power Supply" 60
F1 "psu.sch" 60
$EndSheet
$EndSCHEMATC
