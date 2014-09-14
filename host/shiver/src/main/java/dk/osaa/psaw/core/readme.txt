This package contains the core functionality of the host application,
this is where the thread that feeds Moves into the hardware and the planning thread is started from.


 Planning passes:
 
 * Reverse: Calculate the highest allowable starting speed which still allows slowing down to the highest allowable starting speed of the previous line
 * Forward: Calculate the highest attainable ending speed which doesn't violate the allowable starting speed of the next line
 * Ramps: Calculate the acceleration, plateau and deceleration distance.
 * Move generation: Turn each line into 1-3 moves.
 
 Problems:
 
 Grbl does all calculations on scalar speed and acceleration values, which makes it hard to accurately handle the speed change at end points. 
 
 Solution:
 
 Look directly at the speed vectors when comparing two lines and allow a certain instantaneous change in speed to take place for each axis at the intersection.
 
   
  