This is the host-side API for operating the Photon Saw.

... or it will be at some point.


The stack looks like this:

* gcode and svn parser, which outputs Paths to the PathBuffer, each path is a series of points.
* Path optimizer (merge segments that are very short and sort so the shortest paths are run first)
* Speed planner, operates on the paths and figures out the acceleration ramps and speeds along the path 
* Move calculator, turns paths into 1..3 moves per segment and place them in the MoveBuffer
* Commander (run command and parse output)
* Rxtx -> USB -> Hardware

The whole planner bit should be able to add moves to the output buffer
on demand, so it's possible to keep the hardware buffer filled without
having to generate all of the moves up front.



