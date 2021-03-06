EESchema Schematic File Version 4
LIBS:laserctrl-cache
EELAYER 26 0
EELAYER END
$Descr A4 11693 8268
encoding utf-8
Sheet 3 11
Title ""
Date "16 feb 2013"
Rev ""
Comp ""
Comment1 ""
Comment2 ""
Comment3 ""
Comment4 ""
$EndDescr
Wire Wire Line
	900  6200 1050 6200
Wire Wire Line
	1350 5950 1350 6000
Wire Wire Line
	9450 3800 9600 3800
Wire Wire Line
	8400 2000 8550 2000
Wire Wire Line
	8850 2950 8850 2850
Wire Wire Line
	8850 1800 9600 1800
Wire Wire Line
	9400 1400 9500 1400
Wire Wire Line
	9400 1700 9500 1700
Wire Wire Line
	2700 7450 2850 7450
Wire Wire Line
	2850 7450 2850 7400
Connection ~ 2400 4750
Wire Wire Line
	2400 4900 2400 4750
Wire Wire Line
	2400 5300 2400 5400
Wire Wire Line
	2400 5400 2800 5400
Wire Wire Line
	5350 1300 5500 1300
Wire Wire Line
	1500 4750 1650 4750
Wire Wire Line
	1850 6850 1950 6850
Wire Wire Line
	1950 6250 1850 6250
Wire Wire Line
	1650 4150 1650 4200
Wire Wire Line
	1650 4700 1650 4750
Wire Wire Line
	1300 3300 1200 3300
Wire Wire Line
	1200 3700 1200 3600
Wire Wire Line
	1200 3600 1300 3600
Wire Wire Line
	1250 2450 1250 2550
Connection ~ 1500 1950
Wire Wire Line
	1500 2050 1500 1950
Wire Wire Line
	1550 1950 1500 1950
Connection ~ 3200 2150
Wire Wire Line
	3300 2150 3200 2150
Wire Wire Line
	3200 1950 3300 1950
Wire Wire Line
	2050 1950 2450 1950
Wire Wire Line
	2200 1750 2200 1800
Wire Wire Line
	2200 750  2200 800 
Wire Wire Line
	1200 1000 1900 1000
Wire Wire Line
	2200 1200 2200 1250
Wire Wire Line
	2050 1800 2200 1800
Connection ~ 2200 1800
Wire Wire Line
	2450 2150 2450 2050
Wire Wire Line
	3200 2050 3200 2150
Wire Wire Line
	3300 1950 3300 2150
Connection ~ 3300 2150
Wire Wire Line
	1200 1800 1250 1800
Wire Wire Line
	1250 2050 1250 1800
Connection ~ 1250 1800
Wire Wire Line
	1500 2450 1500 2550
Wire Wire Line
	1200 3200 1300 3200
Wire Wire Line
	1650 5500 1650 5400
Connection ~ 1650 4750
Wire Wire Line
	2800 5400 2800 5500
Wire Wire Line
	1850 6100 1950 6100
Wire Wire Line
	1850 6550 1950 6550
Wire Wire Line
	1850 6700 1950 6700
Wire Wire Line
	2800 4800 2800 4750
Wire Wire Line
	4700 1300 4850 1300
Wire Wire Line
	5900 1300 6050 1300
Wire Wire Line
	6050 1300 6050 1450
Wire Wire Line
	2950 4750 2800 4750
Connection ~ 2800 4750
Wire Wire Line
	2400 4150 2400 4200
Wire Wire Line
	2700 7500 2700 7450
Connection ~ 2700 7450
Wire Wire Line
	9600 1600 9500 1600
Wire Wire Line
	9500 1600 9500 1700
Connection ~ 9500 1700
Wire Wire Line
	9600 1500 9500 1500
Wire Wire Line
	9500 1500 9500 1400
Connection ~ 9500 1400
Wire Wire Line
	8850 2300 8850 2200
Wire Wire Line
	8850 2450 9250 2450
Wire Wire Line
	9250 2450 9250 1900
Wire Wire Line
	9250 1900 9600 1900
Wire Wire Line
	8400 2650 8550 2650
Wire Wire Line
	9600 3700 9450 3700
Wire Wire Line
	9450 3900 9600 3900
Wire Wire Line
	1350 6400 1950 6400
Text HLabel 900  6200 0    50   Input ~ 0
SDRESET
$Comp
L 1g08:MOSFET_P Q6
U 1 1 507AC282
P 1250 6200
F 0 "Q6" H 1250 6390 60  0000 R CNN
F 1 "nx3008pbk" H 1250 6020 60  0000 R CNN
F 2 "" H 1250 6200 60  0001 C CNN
F 3 "" H 1250 6200 60  0001 C CNN
	1    1250 6200
	1    0    0    -1  
$EndComp
$Comp
L 1g08:MOSFET_P Q1
U 1 1 507AC25D
P 2100 1000
F 0 "Q1" H 2100 1190 60  0000 R CNN
F 1 "nx3008pbk" H 2100 820 60  0000 R CNN
F 2 "" H 2100 1000 60  0001 C CNN
F 3 "" H 2100 1000 60  0001 C CNN
	1    2100 1000
	1    0    0    -1  
$EndComp
$Comp
L laserctrl-rescue:GND-RESCUE-laserctrl #PWR023
U 1 1 5079779F
P 9450 3700
AR Path="/5079779F" Ref="#PWR023"  Part="1" 
AR Path="/4EB5C316/5079779F" Ref="#PWR023"  Part="1" 
F 0 "#PWR023" H 9450 3700 30  0001 C CNN
F 1 "GND" H 9450 3630 30  0001 C CNN
F 2 "" H 9450 3700 60  0001 C CNN
F 3 "" H 9450 3700 60  0001 C CNN
	1    9450 3700
	0    1    1    0   
$EndComp
$Comp
L 1g08:CONN_3 K1
U 1 1 50797786
P 9950 3800
F 0 "K1" V 9900 3800 50  0000 C CNN
F 1 "Chiller" V 10000 3800 40  0000 C CNN
F 2 "" H 9950 3800 60  0001 C CNN
F 3 "" H 9950 3800 60  0001 C CNN
	1    9950 3800
	1    0    0    -1  
$EndComp
$Comp
L laserctrl-rescue:GND-RESCUE-laserctrl #PWR024
U 1 1 5079770C
P 8850 2950
AR Path="/5079770C" Ref="#PWR024"  Part="1" 
AR Path="/4EB5C316/5079770C" Ref="#PWR024"  Part="1" 
F 0 "#PWR024" H 8850 2950 30  0001 C CNN
F 1 "GND" H 8850 2880 30  0001 C CNN
F 2 "" H 8850 2950 60  0001 C CNN
F 3 "" H 8850 2950 60  0001 C CNN
	1    8850 2950
	1    0    0    -1  
$EndComp
$Comp
L 1g08:MOSFET_N Q3
U 1 1 50797702
P 8750 2650
F 0 "Q3" H 8700 2850 60  0000 R CNN
F 1 "nx3008nbk" H 8650 2450 60  0000 R CNN
F 2 "" H 8750 2650 60  0001 C CNN
F 3 "" H 8750 2650 60  0001 C CNN
	1    8750 2650
	1    0    0    -1  
$EndComp
$Comp
L laserctrl-rescue:GND-RESCUE-laserctrl #PWR025
U 1 1 507976FB
P 8850 2300
AR Path="/507976FB" Ref="#PWR025"  Part="1" 
AR Path="/4EB5C316/507976FB" Ref="#PWR025"  Part="1" 
F 0 "#PWR025" H 8850 2300 30  0001 C CNN
F 1 "GND" H 8850 2230 30  0001 C CNN
F 2 "" H 8850 2300 60  0001 C CNN
F 3 "" H 8850 2300 60  0001 C CNN
	1    8850 2300
	1    0    0    -1  
$EndComp
$Comp
L 1g08:MOSFET_N Q2
U 1 1 507976EB
P 8750 2000
F 0 "Q2" H 8700 2200 60  0000 R CNN
F 1 "nx3008nbk" H 8650 1800 60  0000 R CNN
F 2 "" H 8750 2000 60  0001 C CNN
F 3 "" H 8750 2000 60  0001 C CNN
	1    8750 2000
	1    0    0    -1  
$EndComp
$Comp
L power:+24V #PWR026
U 1 1 507976CA
P 9400 1700
F 0 "#PWR026" H 9400 1650 20  0001 C CNN
F 1 "+24V" H 9400 1800 30  0000 C CNN
F 2 "" H 9400 1700 60  0001 C CNN
F 3 "" H 9400 1700 60  0001 C CNN
	1    9400 1700
	0    -1   -1   0   
$EndComp
$Comp
L laserctrl-rescue:GND-RESCUE-laserctrl #PWR027
U 1 1 507976C5
P 9400 1400
AR Path="/507976C5" Ref="#PWR027"  Part="1" 
AR Path="/4EB5C316/507976C5" Ref="#PWR027"  Part="1" 
F 0 "#PWR027" H 9400 1400 30  0001 C CNN
F 1 "GND" H 9400 1330 30  0001 C CNN
F 2 "" H 9400 1400 60  0001 C CNN
F 3 "" H 9400 1400 60  0001 C CNN
	1    9400 1400
	0    1    1    0   
$EndComp
$Comp
L 1g08:CONN_6 P3
U 1 1 507976B9
P 9950 1650
F 0 "P3" V 9900 1650 60  0000 C CNN
F 1 "Power" V 10000 1650 60  0000 C CNN
F 2 "" H 9950 1650 60  0001 C CNN
F 3 "" H 9950 1650 60  0001 C CNN
	1    9950 1650
	1    0    0    -1  
$EndComp
NoConn ~ 3150 7400
NoConn ~ 3000 7400
$Comp
L laserctrl-rescue:GND-RESCUE-laserctrl #PWR028
U 1 1 4ECEBCDA
P 2700 7500
AR Path="/4ECEBCDA" Ref="#PWR028"  Part="1" 
AR Path="/4EB5C316/4ECEBCDA" Ref="#PWR028"  Part="1" 
F 0 "#PWR028" H 2700 7500 30  0001 C CNN
F 1 "GND" H 2700 7430 30  0001 C CNN
F 2 "" H 2700 7500 60  0001 C CNN
F 3 "" H 2700 7500 60  0001 C CNN
	1    2700 7500
	1    0    0    -1  
$EndComp
$Comp
L laserctrl-rescue:+3.3V-RESCUE-laserctrl #PWR029
U 1 1 4EC159D5
P 2400 4150
AR Path="/4EC159D5" Ref="#PWR029"  Part="1" 
AR Path="/4EB5C316/4EC159D5" Ref="#PWR029"  Part="1" 
F 0 "#PWR029" H 2400 4110 30  0001 C CNN
F 1 "+3.3V" H 2400 4260 30  0000 C CNN
F 2 "" H 2400 4150 60  0001 C CNN
F 3 "" H 2400 4150 60  0001 C CNN
	1    2400 4150
	1    0    0    -1  
$EndComp
$Comp
L laserctrl-rescue:R-RESCUE-laserctrl R1
U 1 1 4EC1599F
P 2400 4450
AR Path="/4EC1599F" Ref="R1"  Part="1" 
AR Path="/4EB5C316/4EC1599F" Ref="R1"  Part="1" 
F 0 "R1" V 2480 4450 50  0000 C CNN
F 1 "10k" V 2400 4450 50  0000 C CNN
F 2 "" H 2400 4450 60  0001 C CNN
F 3 "" H 2400 4450 60  0001 C CNN
	1    2400 4450
	1    0    0    -1  
$EndComp
$Comp
L laserctrl-rescue:C-RESCUE-laserctrl C1
U 1 1 4EC1599E
P 2400 5100
AR Path="/4EC1599E" Ref="C1"  Part="1" 
AR Path="/4EB5C316/4EC1599E" Ref="C1"  Part="1" 
F 0 "C1" H 2450 5200 50  0000 L CNN
F 1 "100nF" H 2150 5000 50  0000 L CNN
F 2 "" H 2400 5100 60  0001 C CNN
F 3 "" H 2400 5100 60  0001 C CNN
	1    2400 5100
	1    0    0    -1  
$EndComp
$Comp
L laserctrl-rescue:GND-RESCUE-laserctrl #PWR030
U 1 1 4EBEC7BB
P 6050 1450
AR Path="/4EBEC7BB" Ref="#PWR030"  Part="1" 
AR Path="/4EB5C316/4EBEC7BB" Ref="#PWR030"  Part="1" 
F 0 "#PWR030" H 6050 1450 30  0001 C CNN
F 1 "GND" H 6050 1380 30  0001 C CNN
F 2 "" H 6050 1450 60  0001 C CNN
F 3 "" H 6050 1450 60  0001 C CNN
	1    6050 1450
	1    0    0    -1  
$EndComp
$Comp
L laserctrl-rescue:LED-RESCUE-laserctrl D30
U 1 1 4EBEC7B7
P 5700 1300
AR Path="/4EBEC7B7" Ref="D30"  Part="1" 
AR Path="/4EB5C316/4EBEC7B7" Ref="D30"  Part="1" 
F 0 "D30" H 5700 1400 50  0000 C CNN
F 1 "LED" H 5700 1200 50  0000 C CNN
F 2 "" H 5700 1300 60  0001 C CNN
F 3 "" H 5700 1300 60  0001 C CNN
	1    5700 1300
	1    0    0    -1  
$EndComp
$Comp
L laserctrl-rescue:R-RESCUE-laserctrl R87
U 1 1 4EBEC7B3
P 5100 1300
AR Path="/4EBEC7B3" Ref="R87"  Part="1" 
AR Path="/4EB5C316/4EBEC7B3" Ref="R87"  Part="1" 
F 0 "R87" V 5180 1300 50  0000 C CNN
F 1 "330R" V 5100 1300 50  0000 C CNN
F 2 "" H 5100 1300 60  0001 C CNN
F 3 "" H 5100 1300 60  0001 C CNN
	1    5100 1300
	0    1    1    0   
$EndComp
Text HLabel 4700 1300 0    50   Input ~ 0
LED
Text HLabel 9450 3800 0    50   Input ~ 0
TXDC
Text HLabel 9450 3900 0    50   Output ~ 0
RXDC
Text HLabel 8400 2000 0    50   Input ~ 0
Exhaust
Text HLabel 8400 2650 0    50   Input ~ 0
Assist Air
Text HLabel 1850 6850 0    50   Input ~ 0
MISO
$Comp
L microsd:MICROSD J2
U 1 1 4EB997F7
P 2900 6450
F 0 "J2" H 2900 6950 60  0000 C CNN
F 1 "MICROSD" H 2850 6450 60  0000 C CNN
F 2 "" H 2900 6450 60  0001 C CNN
F 3 "" H 2900 6450 60  0001 C CNN
	1    2900 6450
	1    0    0    -1  
$EndComp
$Comp
L laserctrl-rescue:GND-RESCUE-laserctrl #PWR031
U 1 1 4EB6DA7E
P 1850 6700
AR Path="/4EB6DA7E" Ref="#PWR031"  Part="1" 
AR Path="/4EB5C316/4EB6DA7E" Ref="#PWR031"  Part="1" 
F 0 "#PWR031" H 1850 6700 30  0001 C CNN
F 1 "GND" H 1850 6630 30  0001 C CNN
F 2 "" H 1850 6700 60  0001 C CNN
F 3 "" H 1850 6700 60  0001 C CNN
	1    1850 6700
	0    1    1    0   
$EndComp
$Comp
L laserctrl-rescue:+3.3V-RESCUE-laserctrl #PWR032
U 1 1 4EB6DA5E
P 1350 5950
AR Path="/4EB6DA5E" Ref="#PWR032"  Part="1" 
AR Path="/4EB5C316/4EB6DA5E" Ref="#PWR032"  Part="1" 
F 0 "#PWR032" H 1350 5910 30  0001 C CNN
F 1 "+3.3V" H 1350 6060 30  0000 C CNN
F 2 "" H 1350 5950 60  0001 C CNN
F 3 "" H 1350 5950 60  0001 C CNN
	1    1350 5950
	1    0    0    -1  
$EndComp
Text HLabel 1850 6550 0    50   Input ~ 0
SCK
Text HLabel 1850 6250 0    50   Input ~ 0
MOSI
Text HLabel 1850 6100 0    50   Input ~ 0
CS
$Comp
L laserctrl-rescue:GND-RESCUE-laserctrl #PWR033
U 1 1 4EB6D76C
P 2800 5500
AR Path="/4EB6D76C" Ref="#PWR033"  Part="1" 
AR Path="/4EB5C316/4EB6D76C" Ref="#PWR033"  Part="1" 
F 0 "#PWR033" H 2800 5500 30  0001 C CNN
F 1 "GND" H 2800 5430 30  0001 C CNN
F 2 "" H 2800 5500 60  0001 C CNN
F 3 "" H 2800 5500 60  0001 C CNN
	1    2800 5500
	1    0    0    -1  
$EndComp
$Comp
L 1g08:SW_PUSH SW2
U 1 1 4EB6D763
P 2800 5100
F 0 "SW2" H 2950 5210 50  0000 C CNN
F 1 "Reset" H 2800 5020 50  0000 C CNN
F 2 "" H 2800 5100 60  0001 C CNN
F 3 "" H 2800 5100 60  0001 C CNN
	1    2800 5100
	0    1    1    0   
$EndComp
$Comp
L laserctrl-rescue:+3.3V-RESCUE-laserctrl #PWR034
U 1 1 4EB6D47F
P 1650 4150
AR Path="/4EB6D47F" Ref="#PWR034"  Part="1" 
AR Path="/4EB5C316/4EB6D47F" Ref="#PWR034"  Part="1" 
F 0 "#PWR034" H 1650 4110 30  0001 C CNN
F 1 "+3.3V" H 1650 4260 30  0000 C CNN
F 2 "" H 1650 4150 60  0001 C CNN
F 3 "" H 1650 4150 60  0001 C CNN
	1    1650 4150
	1    0    0    -1  
$EndComp
$Comp
L laserctrl-rescue:GND-RESCUE-laserctrl #PWR035
U 1 1 4EB6D436
P 1650 5500
AR Path="/4EB6D436" Ref="#PWR035"  Part="1" 
AR Path="/4EB5C316/4EB6D436" Ref="#PWR035"  Part="1" 
F 0 "#PWR035" H 1650 5500 30  0001 C CNN
F 1 "GND" H 1650 5430 30  0001 C CNN
F 2 "" H 1650 5500 60  0001 C CNN
F 3 "" H 1650 5500 60  0001 C CNN
	1    1650 5500
	1    0    0    -1  
$EndComp
$Comp
L 1g08:SW_PUSH SW1
U 1 1 4EB6D42E
P 1650 5100
F 0 "SW1" H 1800 5210 50  0000 C CNN
F 1 "Program" H 1650 5020 50  0000 C CNN
F 2 "" H 1650 5100 60  0001 C CNN
F 3 "" H 1650 5100 60  0001 C CNN
	1    1650 5100
	0    1    1    0   
$EndComp
$Comp
L laserctrl-rescue:R-RESCUE-laserctrl R15
U 1 1 4EB6D417
P 1650 4450
AR Path="/4EB6D417" Ref="R15"  Part="1" 
AR Path="/4EB5C316/4EB6D417" Ref="R15"  Part="1" 
F 0 "R15" V 1730 4450 50  0000 C CNN
F 1 "10k" V 1650 4450 50  0000 C CNN
F 2 "" H 1650 4450 60  0001 C CNN
F 3 "" H 1650 4450 60  0001 C CNN
	1    1650 4450
	1    0    0    -1  
$EndComp
$Comp
L laserctrl-rescue:GND-RESCUE-laserctrl #PWR036
U 1 1 4EB6D3CA
P 1200 3700
AR Path="/4EB6D3CA" Ref="#PWR036"  Part="1" 
AR Path="/4EB5C316/4EB6D3CA" Ref="#PWR036"  Part="1" 
F 0 "#PWR036" H 1200 3700 30  0001 C CNN
F 1 "GND" H 1200 3630 30  0001 C CNN
F 2 "" H 1200 3700 60  0001 C CNN
F 3 "" H 1200 3700 60  0001 C CNN
	1    1200 3700
	1    0    0    -1  
$EndComp
$Comp
L 1g08:CONN_6 P5
U 1 1 4EB6D3C3
P 1650 3350
F 0 "P5" V 1600 3350 60  0000 C CNN
F 1 "ftdi serial" V 1700 3350 60  0000 C CNN
F 2 "" H 1650 3350 60  0001 C CNN
F 3 "" H 1650 3350 60  0001 C CNN
	1    1650 3350
	1    0    0    1   
$EndComp
Text HLabel 1200 3300 0    50   Output ~ 0
RXD
Text HLabel 1200 3200 0    50   Input ~ 0
TXD
Text HLabel 1500 4750 0    50   Output ~ 0
~ISP
Text HLabel 2950 4750 2    50   Output ~ 0
~RESET
$Comp
L laserctrl-rescue:GND-RESCUE-laserctrl #PWR037
U 1 1 4EB6D202
P 1500 2550
AR Path="/4EB6D202" Ref="#PWR037"  Part="1" 
AR Path="/4EB5C316/4EB6D202" Ref="#PWR037"  Part="1" 
F 0 "#PWR037" H 1500 2550 30  0001 C CNN
F 1 "GND" H 1500 2480 30  0001 C CNN
F 2 "" H 1500 2550 60  0001 C CNN
F 3 "" H 1500 2550 60  0001 C CNN
	1    1500 2550
	1    0    0    -1  
$EndComp
$Comp
L laserctrl-rescue:GND-RESCUE-laserctrl #PWR038
U 1 1 4EB6D1FB
P 1250 2550
AR Path="/4EB6D1FB" Ref="#PWR038"  Part="1" 
AR Path="/4EB5C316/4EB6D1FB" Ref="#PWR038"  Part="1" 
F 0 "#PWR038" H 1250 2550 30  0001 C CNN
F 1 "GND" H 1250 2480 30  0001 C CNN
F 2 "" H 1250 2550 60  0001 C CNN
F 3 "" H 1250 2550 60  0001 C CNN
	1    1250 2550
	1    0    0    -1  
$EndComp
$Comp
L laserctrl-rescue:C-RESCUE-laserctrl C6
U 1 1 4EB6D1E9
P 1250 2250
AR Path="/4EB6D1E9" Ref="C6"  Part="1" 
AR Path="/4EB5C316/4EB6D1E9" Ref="C6"  Part="1" 
F 0 "C6" H 1300 2350 50  0000 L CNN
F 1 "18pF" H 1300 2150 50  0000 L CNN
F 2 "" H 1250 2250 60  0001 C CNN
F 3 "" H 1250 2250 60  0001 C CNN
	1    1250 2250
	1    0    0    -1  
$EndComp
$Comp
L laserctrl-rescue:C-RESCUE-laserctrl C7
U 1 1 4EB6D1E4
P 1500 2250
AR Path="/4EB6D1E4" Ref="C7"  Part="1" 
AR Path="/4EB5C316/4EB6D1E4" Ref="C7"  Part="1" 
F 0 "C7" H 1550 2350 50  0000 L CNN
F 1 "18pF" H 1550 2150 50  0000 L CNN
F 2 "" H 1500 2250 60  0001 C CNN
F 3 "" H 1500 2250 60  0001 C CNN
	1    1500 2250
	1    0    0    -1  
$EndComp
$Comp
L laserctrl-rescue:R-RESCUE-laserctrl R14
U 1 1 4EB6D1D5
P 1800 1950
AR Path="/4EB6D1D5" Ref="R14"  Part="1" 
AR Path="/4EB5C316/4EB6D1D5" Ref="R14"  Part="1" 
F 0 "R14" V 1880 1950 50  0000 C CNN
F 1 "33R" V 1800 1950 50  0000 C CNN
F 2 "" H 1800 1950 60  0001 C CNN
F 3 "" H 1800 1950 60  0001 C CNN
	1    1800 1950
	0    1    1    0   
$EndComp
$Comp
L laserctrl-rescue:R-RESCUE-laserctrl R13
U 1 1 4EB6D1D2
P 1800 1800
AR Path="/4EB6D1D2" Ref="R13"  Part="1" 
AR Path="/4EB5C316/4EB6D1D2" Ref="R13"  Part="1" 
F 0 "R13" V 1700 1800 50  0000 C CNN
F 1 "33R" V 1800 1800 50  0000 C CNN
F 2 "" H 1800 1800 60  0001 C CNN
F 3 "" H 1800 1800 60  0001 C CNN
	1    1800 1800
	0    1    1    0   
$EndComp
$Comp
L laserctrl-rescue:GND-RESCUE-laserctrl #PWR039
U 1 1 4EB6D1B5
P 3300 2250
AR Path="/4EB6D1B5" Ref="#PWR039"  Part="1" 
AR Path="/4EB5C316/4EB6D1B5" Ref="#PWR039"  Part="1" 
F 0 "#PWR039" H 3300 2250 30  0001 C CNN
F 1 "GND" H 3300 2180 30  0001 C CNN
F 2 "" H 3300 2250 60  0001 C CNN
F 3 "" H 3300 2250 60  0001 C CNN
	1    3300 2250
	1    0    0    -1  
$EndComp
$Comp
L laserctrl-rescue:R-RESCUE-laserctrl R12
U 1 1 4EB6D122
P 2200 1500
AR Path="/4EB6D122" Ref="R12"  Part="1" 
AR Path="/4EB5C316/4EB6D122" Ref="R12"  Part="1" 
F 0 "R12" V 2280 1500 50  0000 C CNN
F 1 "1k5" V 2200 1500 50  0000 C CNN
F 2 "" H 2200 1500 60  0001 C CNN
F 3 "" H 2200 1500 60  0001 C CNN
	1    2200 1500
	1    0    0    -1  
$EndComp
$Comp
L laserctrl-rescue:+3.3V-RESCUE-laserctrl #PWR040
U 1 1 4EB6D11C
P 2200 750
AR Path="/4EB6D11C" Ref="#PWR040"  Part="1" 
AR Path="/4EB5C316/4EB6D11C" Ref="#PWR040"  Part="1" 
F 0 "#PWR040" H 2200 710 30  0001 C CNN
F 1 "+3.3V" H 2200 860 30  0000 C CNN
F 2 "" H 2200 750 60  0001 C CNN
F 3 "" H 2200 750 60  0001 C CNN
	1    2200 750 
	1    0    0    -1  
$EndComp
Text HLabel 1200 1000 0    50   Input ~ 0
USB_VBUS
Text HLabel 1200 1950 0    50   Input ~ 0
USB-
Text HLabel 1200 1800 0    50   Input ~ 0
USB+
$Comp
L 1g08:USB J1
U 1 1 4EB6D09A
P 2800 1600
F 0 "J1" H 2750 2000 60  0000 C CNN
F 1 "USB" V 2550 1750 60  0000 C CNN
F 2 "" H 2800 1600 60  0001 C CNN
F 3 "" H 2800 1600 60  0001 C CNN
	1    2800 1600
	-1   0    0    -1  
$EndComp
Wire Wire Line
	2400 4750 2400 4700
Wire Wire Line
	1500 1950 1200 1950
Wire Wire Line
	3200 2150 2450 2150
Wire Wire Line
	2200 1800 2450 1800
Wire Wire Line
	3300 2150 3300 2250
Wire Wire Line
	1250 1800 1550 1800
Wire Wire Line
	1650 4750 1650 4800
Wire Wire Line
	2800 4750 2400 4750
Wire Wire Line
	2700 7450 2700 7400
Wire Wire Line
	9500 1700 9600 1700
Wire Wire Line
	9500 1400 9600 1400
$EndSCHEMATC
