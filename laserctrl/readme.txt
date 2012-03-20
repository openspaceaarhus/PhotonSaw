Bugs found in V1:

* Fixed for V2: Too small/few power vias (on the +24V/GND main power rails)
* Fixed for V2: Wrong foot print for Q3 (BC807 for USB pull up): Swapped pin 1 and 2
* Mirrored silkscreen text on bottom side
* Water flow sensor on P1.16 cannot generate interrupts, needs to be moved to port 0 or port 2.
* Footprint for 32768 Hz xtal: 3x7mm solder pad in stead of glue.
* Switch the small mini-melf diodes to SOT-23, even where only one diode is needed (easier to mount).
* Add diode to the main fire signal that goes to the WD to make it impossible for the WD to fire the laser on its own.
* Add a power control FET for the microSD to be able to reset it.
* Add intrusion switch input(s), don't start if open.

* More LEDs
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
