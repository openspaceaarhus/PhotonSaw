package dk.osaa.psaw.mover;

import java.util.ArrayList;

import lombok.Data;
import lombok.val;

/**
 * A line in 2d space
 * 
 * Notice that we keep track of the x and y speed and acceleration independently, because x and y are orthogonal and
 * the acceleration of one has no bearing on the other, this leads to 1.4 times higher acceleration when both axes
 * are doing full acceleration, compared to a global acceleration limit.
 * 
 * As the weight of the axes are different they are bound to have different physical capabilities.
 * 
 * Units used at this point: mm, mm/s and mm/s/s translation to steps, steps/tick and steps/tick/tick
 * happens later when Moves are generated.
 * 
 * The algorithms are inspired by Smoothie and GRBL, but significantly rewritten/molested to do 4D.
 * 
 * @author ff
 */
@Data
public class Line {
	
	Line prev;
	
	class LineAxis {
		double endPos; // Where this axis must end up when done
		int direction; // The direction this axis moves in, basically the sign for the speed.
	};
	LineAxis axes[] = new LineAxis[Move.AXES];
	MovementConstraints mc;
	double maxSpeed;	
	double entrySpeed;
	double acceleration;
	double length;
	double unityVector[] = new double[Move.AXES];
	double maxEntrySpeed;  // The highest speed we can allow when starting this line
	boolean nominalLength; // This line is long enough to allow full acceleration from 0 to nominalSpeed. 
	boolean recalculate;
	
	public Line(MovementConstraints mc, Line prev, Point endPoint, double maxSpeed) {
		this.mc = mc;
		this.prev=prev;
		this.maxSpeed=maxSpeed;
		
		// Calculate the unity vector for this line, because it's handy for calculating the maximum speed of each axis during the move.
		length = 0;		
		for (int a=0;a<Move.AXES;a++) {
			length += Math.pow(endPoint.getAxes()[a]-prev.axes[a].endPos, 2);
		}
		length = Math.sqrt(length);		
		for (int a=0;a<Move.AXES;a++) {
			unityVector[a] = (endPoint.getAxes()[a]-prev.axes[a].endPos)/length;
		}
		
		// Initialize each axis.
		for (int a=0;a<Move.AXES;a++) {
			axes[a].endPos = endPoint.getAxes()[a];
			axes[a].direction = prev.axes[a].endPos == axes[a].endPos ? 0 :
							    prev.axes[a].endPos <  axes[a].endPos ? 1 : -1;
		}
		
		// Set default max junction speed, to the minimum speed of the slowest of the axes:
		maxEntrySpeed = 0;
		for (int a=0;a<Move.AXES;a++) {		
			if (mc.axes[a].minSpeed < Math.abs(maxEntrySpeed * unityVector[a]) || maxEntrySpeed == 0) {
				maxEntrySpeed = Math.abs(mc.axes[a].minSpeed / unityVector[a]);	
			}
		}
		
		// Find the largest acceleration we can use for this line, by letting the most active*slowest axis set the limit 
		acceleration = 0;
		for (int a=0;a<Move.AXES;a++) {		
			if (mc.axes[a].acceleration < Math.abs(acceleration * unityVector[a]) || acceleration == 0) {
				acceleration = Math.abs(mc.axes[a].acceleration / unityVector[a]);	
			}
		}
		
		// Compute cosine of angle between previous and current path. (prev_unit_vec is negative)
		// NOTE: Max junction velocity is computed without sin() or acos() by trig half angle identity.
		double cosTheta = 0;
		for (int a=0;a<Move.AXES;a++) {
			cosTheta -= prev.unityVector[a]*unityVector[a];
		}
		                       
		// Skip and use default max junction speed for 0 degree acute junction.
		if (cosTheta < 0.95) {
			maxEntrySpeed = Math.min(prev.maxSpeed, maxSpeed);

			// Skip and avoid divide by zero for straight junctions at 180 degrees. Limit to min() of nominal speeds.
			if (cosTheta > -0.95) {
				// Compute maximum junction velocity based on maximum acceleration and junction deviation
				double sin_theta_d2 = Math.sqrt(0.5*(1.0-cosTheta)); // Trig half angle identity. Always positive.
				maxEntrySpeed = Math.min(maxEntrySpeed,
						Math.sqrt(acceleration * mc.junctionDeviation * sin_theta_d2/(1.0-sin_theta_d2))); 
		    }
		}
				   
	    // Initialize Line entry speed. Compute based on deceleration to user-defined MINIMUM_PLANNER_SPEED.
	    double allowableSpeed = maxAllowableSpeed(-acceleration,0.0,length); 
	    entrySpeed = Math.min(maxEntrySpeed, allowableSpeed);

	    // Initialize planner efficiency flags
	    // Set flag if block will always reach maximum junction speed regardless of entry/exit speeds.
	    // If a block can de/ac-celerate from nominal speed to zero within the length of the block, then
	    // the current block and next block junction speeds are guaranteed to always be at their maximum
	    // junction speeds in deceleration and acceleration, respectively. This is due to how the current
	    // block nominal speed limits both the current and next maximum junction speeds. Hence, in both
	    // the reverse and forward planners, the corresponding block junction speed will always be at the
	    // the maximum junction speed and may always be ignored for any speed reduction checks.
	    nominalLength = maxSpeed <= allowableSpeed;
	}
	
	// Calculates the maximum allowable speed at this point when you must be able to reach target_velocity
	// using the acceleration within the allotted distance.
	static double maxAllowableSpeed(double acceleration, double targetVelocity, double distance) {
	  return Math.sqrt(
			  Math.pow(targetVelocity, 2)-
			  2*acceleration*distance);
	}

	// Called by Planner::recalculate() when scanning the plan from last to first entry.
	public void reversePass(Line next) {
		if (next == null) return; // This is the last line.
		
        // If entry speed is already at the maximum entry speed, no need to recheck. Block is cruising.
        // If not, block in state of acceleration or deceleration. Reset entry speed to maximum and
        // check for maximum allowable speed reductions to ensure maximum possible planned speed.
        if (entrySpeed != maxEntrySpeed) {

            // If nominal length true, max junction speed is guaranteed to be reached. Only compute
            // for max allowable speed if block is decelerating and nominal length is false.
            if ((!nominalLength) && (maxEntrySpeed > next.entrySpeed)) {
                entrySpeed = Math.min( maxEntrySpeed,
                		Line.maxAllowableSpeed(-acceleration, entrySpeed, length));
            } else {
                entrySpeed = maxEntrySpeed;
            }
            recalculate = true;
        }
	}
	
	// Called by Planner::recalculate() when scanning the plan from first to last entry.
	public void forwardPass() {
	    if (prev == null) return; // This is the very first line  

	    // If the previous block is an acceleration block, but it is not long enough to complete the
	    // full speed change within the block, we need to adjust the entry speed accordingly. Entry
	    // speeds have already been reset, maximized, and reverse planned by reverse planner.
	    // If nominal length is true, max junction speed is guaranteed to be reached. No need to recheck.
	    if (!prev.nominalLength) {
	        if (prev.entrySpeed < entrySpeed) {
	          double newEntrySpeed = Math.min( entrySpeed,
	            maxAllowableSpeed(-acceleration, prev.entrySpeed, prev.length) );

	          // Check for junction speed change
	          if (entrySpeed != newEntrySpeed) {
	            entrySpeed = newEntrySpeed;
	            recalculate = true;
	          }
	        }
	    }
	}

	
	// Calculates the distance (not time) it takes to accelerate from initial_rate to target_rate using the
	// given acceleration:
	double estimateAccelerationDistance(double initialrate, double targetrate, double acceleration) {
	      return (Math.pow(targetrate, 2) - Math.pow(initialrate, 2))/(2*acceleration);
	}

	// This function gives you the point at which you must start braking (at the rate of -acceleration) if
	// you started at speed initial_rate and accelerated until this point and want to end at the final_rate after
	// a total travel of distance. This can be used to compute the intersection point between acceleration and
	// deceleration in the cases where the trapezoid has no plateau (i.e. never reaches maximum speed)
	//
	/*                          + <- some maximum rate we don't care about
	                           /|\
	                          / | \
	                         /  |  + <- final_rate
	                        /   |  |
	       initial_rate -> +----+--+
	                            ^ ^
	                            | |
	        intersection_distance distance */
	double intersectionDistance(double initialrate, double finalrate, double acceleration, double distance) {
	   return (2*acceleration*distance - Math.pow(initialrate, 2) + Math.pow(finalrate, 2))/(4*acceleration);
	}	
	
	// Calculates trapezoid parameters so that the entry- and exit-speed is compensated by the provided factors.
	// The factors represent a factor of braking and must be in the range 0.0-1.0.
	//	                                +--------+ <- nominal_rate
	//	                               /          \
	//   nominal_rate*entry_factor -> +            \
	//	                              |             + <- nominal_rate*exit_factor
	//	                              +-------------+
	//	                                  time -->
	
	double accelerateDistance;
	double plateauDistance;
	void calculateTrapezoid(Line next) {
		double exitSpeed = next==null ? 0 : next.entrySpeed;
		
	    accelerateDistance = estimateAccelerationDistance(entrySpeed, maxSpeed, acceleration);
	    double decelerateDistance = estimateAccelerationDistance(maxSpeed, next.entrySpeed, -acceleration);
	    plateauDistance = length-accelerateDistance-decelerateDistance;

	    if (plateauDistance < 0) {
	    	accelerateDistance = intersectionDistance(entrySpeed, exitSpeed, acceleration, length);
	    	accelerateDistance = Math.max(accelerateDistance, 0); 
	    	accelerateDistance = Math.min(accelerateDistance, length);
	    	plateauDistance = 0;
	    }
	}
		
	public void toMoves(ArrayList<Move> output) {
		// TODO: Generate the moves needed.
		
		
	}
}
