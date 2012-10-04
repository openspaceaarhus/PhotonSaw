Fixed for V2:

* Too small/few power vias (on the +24V/GND main power rails)
* Wrong foot print for Q3 (BC807 for USB pull up): Swapped pin 1 and 2


Bugs found in V1:

* The PWM and Fire optocouplers are too slow replace them with one VO 2630-X007T which can do 10 MHz.
* Water flow sensor on P1.16 cannot generate interrupts, needs to be moved to port 0 or port 2.
* Mirrored silkscreen text on bottom side
* Add diode to the main fire signal that goes to the WD to make it impossible for the WD to fire the laser on its own.
* Add a power control FET for the microSD to be able to reset it.
* Footprint for 32768 Hz xtal: 3x7mm solder pad in stead of glue.
* Switch the small mini-melf diodes to SOT-23, even where only one diode is needed (easier to mount).


Features missing in V1:

* A 6p header connector to bring +24 V on-board and connect to the Assist Air and Exhaust relays to avoid screw terminals entirely

* A 3-pin serial connector for the chiller.

* More bulkhead header pins:
 * Power output pins: +24V / +3.3V. / GND
 * I2C pins to allow connecting a couple of TMP006 fire sensors or GPIO extenders.
 * Add extra GPIO pins to the bulkhead header for things like:
  * Intrusion switch inputs: don't start if open.

* More LEDs, perhaps?
 * LASER PWM outout
 * Watchdog SPI lines (a total of 5 LEDs for the WD)
 * Power 
 * Assist air
 * Exhaust
 * Motor enable (from watchdog)
 * Water flow sensor
 * For each axis
  * Motor enable (from main)
  * Step 
  * Direction
  * End stops
