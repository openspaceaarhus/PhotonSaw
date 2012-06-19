This is the host-side API for operating the Photon Saw.

... or it will be at some point.


The stack looks like this:

* gcode and svn parser, which outputs LinePaths and EngraveableImages to the JobList.
* Gui or command line options are used to set up the Job transformation (translation, axis-mapping, rotation, scaling) 
* Planner which enqueues each Job from the JobList to the LineBuffer.
* Speed planner, operates on the Lines in the LineBuffer and figures out the acceleration ramps and speeds along the path 
* Move calculator, turns paths into 1..3 moves per segment and places them in the MoveBuffer
* Commander (run command and parse output)
* Rxtx -> USB -> Hardware

The whole planner bit should be able to add moves to the output buffer
on demand, so it's possible to keep the hardware buffer filled without
having to generate all of the moves up front.




--- UI TODO ---

Server:

1) Get job list
1.1) Create job
2) Import svg into job
3) Export job as xml
4) Export job as compressed xml
5) Import compressed xml job
6) Set JobNode transformation  
7) 


