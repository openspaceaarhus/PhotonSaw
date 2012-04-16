This is the host-side API for operating the Photon Saw.

... or it will be at some point.

http://rxtx.qbang.org/wiki/index.php/Main_Page


The stack looks like this:

* gcode-parser
* gcode to Line converter
* Line optimizer (merge lines that have the same direction or are very short)
* Speed planner 
* Move encoder (one line becomes 1..3 moves)
* Rxtx -> USB -> Hardware

The whole planner bit should be able to add moves to the output buffer
on demand, so it's possible to keep the hardware buffer filled without
having to generate all of the moves up front.
