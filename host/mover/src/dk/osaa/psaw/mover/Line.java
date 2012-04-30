package dk.osaa.psaw.mover;

import java.util.ArrayList;
import java.util.logging.Level;

import lombok.Data;
import lombok.val;
import lombok.extern.java.Log;

/**
 * A line in 2d space
 * 
 * Units used  this point: mm, mm/s and mm/s/s translation to steps, steps/tick and steps/tick/tick
 * happens later when Moves are generated.
 * 
 * The algorithms are inspired by Smoothie and GRBL, but somewhat rewritten/molested to do 4D.
 * 
 * @author ff
 */
@Data
@Log
public class Line {
	
	/**
	 * Enable to avoid recalculating Move step length after using nudgeSpeed to correct it.
	 * 
	 * Current numbers suggest that about 0.45% of the axis moves need a call to nudgeSpeed,
	 * so the cost of setting this to false is 0.45% more calls to getAxisLength, each of which
	 * takes about 350-600 ns depending on other load on the host.
	 * 
	 * false: getAxisLength calls: 3601 total time: 2032917 ns, avg: 564 ns
	 * true:  getAxisLength calls: 3585 total time: 1242401 ns, avg: 346 ns
	 */
	private static final boolean TRUST_NUDGE_SPEED = false;

	class LineAxis {
		double startPos;
		double endPos; // Where this axis must end up when done
		int direction; // The direction this axis moves in, basically the sign for the speed.
	};
	LineAxis axes[] = new LineAxis[Move.AXES];
	MovementConstraints mc;
	double maxSpeed;
	double minSpeed;
	double entrySpeed;
	double acceleration;
	double length;
	MoveVector unitVector = new MoveVector();
	MoveVector moveVector = new MoveVector();
	double maxEntrySpeed;  // The highest speed we can allow when starting this line
	boolean nominalLength; // This line is long enough to allow full acceleration from 0 to nominalSpeed. 
	boolean recalculate;
	
	public Line(MovementConstraints mc, Line prev, Point endPoint, double maxSpeed) {
		this.mc = mc;
		this.maxSpeed=maxSpeed;
		
		endPoint.roundToWholeSteps(mc);

		// Initialize each axis.
		for (int a=0;a<Move.AXES;a++) {
			axes[a] = new LineAxis();
			
			// Round off to whole steps
 			axes[a].endPos = endPoint.getAxes()[a];

 			if (prev != null) {
 				axes[a].startPos = prev.axes[a].endPos;
 			}
		}
		
        if (prev == null) {
        	entrySpeed = 0;
        	maxEntrySpeed = 0;
        	acceleration = 0;
        	return;
        }
		
		for (int a=0;a<Move.AXES;a++) {
			axes[a].direction = prev.axes[a].endPos == axes[a].endPos ? 0 :
			    prev.axes[a].endPos <  axes[a].endPos ? 1 : -1;
		}
		
		// Calculate the unity vector for this line, because it's handy for calculating the maximum speed of each axis during the move.
		for (int a=0;a<Move.AXES;a++) {
			moveVector.setAxis(a, endPoint.getAxes()[a]-prev.axes[a].endPos);
		}

		length = moveVector.length();
		unitVector = moveVector.unit();
		
		// Set default max junction speed, to the minimum speed of the slowest of the axes:
		minSpeed = 0;
		for (int a=0;a<Move.AXES;a++) {
			if (mc.axes[a].minSpeed < Math.abs(minSpeed * unitVector.getAxis(a)) || minSpeed == 0) {
				minSpeed = Math.abs(mc.axes[a].minSpeed / unitVector.getAxis(a));	
			}
		}
		maxEntrySpeed = minSpeed;
		
		// Find the largest acceleration we can use for this line, by letting the most active*slowest axis set the limit 
		acceleration = 0;
		for (int a=0;a<Move.AXES;a++) {		
			if (mc.axes[a].acceleration < Math.abs(acceleration * unitVector.getAxis(a)) || acceleration == 0) {
				acceleration = Math.abs(mc.axes[a].acceleration / unitVector.getAxis(a));	
			}
		}
		if (acceleration == 0) {
			throw new RuntimeException("Accleration ended up being 0 for line to "+endPoint);
		}
		
		// Compute cosine of angle between previous and current path. (prev_unit_vec is negative)
		// NOTE: Max junction velocity is computed without sin() or acos() by trig half angle identity.
		double cosTheta = 0;
		for (int a=0;a<Move.AXES;a++) {
			cosTheta -= prev.unitVector.getAxis(a)*unitVector.getAxis(a);
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
		maxEntrySpeed = Math.max(minSpeed, maxEntrySpeed); 
				   
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
	    recalculate = true;
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
	public void forwardPass(Line prev) {
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
	double decelerateDistance;
	double exitSpeed;
	void calculateTrapezoid(Line next) {
		exitSpeed = Math.max(minSpeed, next==null ? 0 : next.entrySpeed);
		if (acceleration == 0) { // This is a point, not a line.
			return;
		}
		entrySpeed = Math.max(minSpeed, entrySpeed);
		
	    accelerateDistance = estimateAccelerationDistance(entrySpeed, maxSpeed, acceleration);
	    decelerateDistance = estimateAccelerationDistance(maxSpeed, exitSpeed, -acceleration);
	    plateauDistance = length-accelerateDistance-decelerateDistance;
	    if (log.isLoggable(Level.FINE)) {
	    	log.fine("Length:"+length+" a:"+accelerateDistance+" d:"+decelerateDistance+" p:"+plateauDistance);
	    }

	    if (plateauDistance < 0) {
	    	accelerateDistance = intersectionDistance(entrySpeed, exitSpeed, acceleration, length);
	    	accelerateDistance = Math.max(accelerateDistance, 0); 
	    	accelerateDistance = Math.min(accelerateDistance, length);
	    	decelerateDistance = length-accelerateDistance;
	    	plateauDistance = 0;
	    }
	    
	    if (accelerateDistance == 0 && decelerateDistance == 0 && plateauDistance == 0) {
	    	throw new RuntimeException("Line has no length");	    	
	    }
	}

	static long moveId = 0;	
	long stepsMoved[] = new long[Move.AXES];
	Move endcodeMove(MoveVector mmMoveVector, double startSpeedMMS, double endSpeedMMS) {
		val stepVector = mmMoveVector.div(mc.mmPerStep()).round(); // move vector in whole steps
		val unitVector = mmMoveVector.unit();
		MoveVector startSpeedVector = unitVector.mul(startSpeedMMS/mc.tickHZ).div(mc.mmPerStep()); // convert from scalar mm/s to vector step/tick

		// Find the longest axis, so we can use it for calculating the duration of the move, this way we get better accuracy.
		int longAxis = 0;
		double longAxisLength = -1;
		for (int i=0;i<Move.AXES;i++) {
			if (Math.abs(stepVector.getAxis(i)) > longAxisLength) {
				longAxisLength = Math.abs(stepVector.getAxis(i));
				longAxis = i;
			}
		}
				
		MoveVector accel = null;
		long ticks;
		if (startSpeedMMS == endSpeedMMS) {			
			ticks = (long)Math.ceil(stepVector.getAxis(longAxis) / startSpeedVector.getAxis(longAxis));
			
		} else {
			MoveVector endSpeedVector   = unitVector.mul(endSpeedMMS/mc.tickHZ).div(mc.mmPerStep());
			
			// distance = (1/2)*acceleration*time^2
			// d = s0*t+0.5*a*t^2 and a = (s1-s0)/t =>
			// t = 2*d/(s1+s0)    and a = (s1^2-s0^2)/(2*d)
			ticks = (long)Math.ceil(2*stepVector.getAxis(longAxis)/(endSpeedVector.getAxis(longAxis)+startSpeedVector.getAxis(longAxis)));

			accel = new MoveVector();
			for (int axis=0;axis<Move.AXES;axis++) {
				//accel.setAxis(axis, (endSpeedVector.getAxis(axis)-startSpeedVector.getAxis(axis)) / ticks);
				if (stepVector.getAxis(axis) != 0) {
					accel.setAxis(axis, ((Math.pow(endSpeedVector.getAxis(axis),2)-Math.pow(startSpeedVector.getAxis(axis),2))/(2*stepVector.getAxis(axis))));
				} else {
					accel.setAxis(axis, 0);
				}
			}
		}
	
		Move move = new Move(moveId++, ticks);
		for (int a=0; a < Move.AXES; a++) {
			move.setAxisSpeed(a, startSpeedVector.getAxis(a));
			if (accel != null) {
				move.setAxisAccel(a, accel.getAxis(a));
			}
			
			// Check that we got exactly the movement in steps that we wanted,
			// if not adjust the speed until the error is gone.
			long steps = move.getAxisLength(a);
			long stepsWanted = (long)Math.round(mmMoveVector.getAxis(a)/mc.axes[a].mmPerStep);
			
			long diffSteps = steps - stepsWanted;
			if (diffSteps != 0) {
				log.fine("Did not get correct movement in axis "+a+" wanted:"+stepsWanted+" got:"+steps);				
				move.nudgeSpeed(a, -diffSteps);
					
				// TODO: This is a ghastly hack, I know it, but damn it, it works and I don't know what else to do.
				// I'd much rather have a system that's able to calculate the correct speed and acceleration the first time
				// rather than have to rely on nudging the speed parameter up and down after the inaccuracy has been detected.
				while (diffSteps != 0) {
					steps = move.getAxisLength(a);
					diffSteps = steps - stepsWanted;
					if (diffSteps != 0) {
						move.nudgeSpeed(a, -diffSteps/2.0);
						log.warning("Did not get correct movement in axis after correction "+a+" wanted:"+stepsWanted+" got:"+steps+", compensating...");
					}
				}
			}

			stepsMoved[a] += steps;
		}		

		return move;
	}
	
	public void toMoves(ArrayList<Move> output) {
		if (acceleration == 0) { // This is not a move, but a point
			return;
		}

		int mbf = output.size();
		for (int i=0;i<Move.AXES;i++) {
			stepsMoved[i] = 0;
		}
		double topSpeed = entrySpeed;		
		if (accelerateDistance > 0) {
			topSpeed = entrySpeed+acceleration*Math.sqrt(accelerateDistance/acceleration);
			output.add(endcodeMove(unitVector.mul(accelerateDistance), entrySpeed, topSpeed));			
		}
		
		if (plateauDistance > 0) {
			output.add(endcodeMove(unitVector.mul(plateauDistance), topSpeed, topSpeed));
		}
		
		if (decelerateDistance > 0) {
			output.add(endcodeMove(unitVector.mul(decelerateDistance), topSpeed, exitSpeed));			
		}
		
		if (length > 0) {
			if (mbf == output.size()) {
				log.warning("No moves emitted for line with length: "+length+" and accel: "+acceleration);
			} else {
				if (log.isLoggable(Level.FINE)) {
					val sb = new StringBuilder();
					sb.append("Converted line: "+this);
					sb.append(" to "+(output.size()-mbf)+" moves:");
					for (int i=mbf;i<output.size();i++) {
						sb.append("\n     "+output.get(i));
					}				
					log.fine(sb.toString());
				}
			}
		}

		for (int i=0;i<Move.AXES;i++) {
			long stepsWanted = (long)Math.round((axes[i].endPos-axes[i].startPos)/mc.axes[i].mmPerStep); 
			long diffSteps = stepsMoved[i] - stepsWanted;
			
			if (diffSteps != 0) {
				log.fine("Step difference on axis "+i+": "+diffSteps+ " wanted:"+stepsWanted+" got:"+stepsMoved[i]);				
				output.get(output.size()-1).nudgeSpeed(i, -diffSteps); // Modify speed of the last move, whatever it is
			}
		}				
	}
	
	public String toString() {
		return "Line(a:"+acceleration+", ad:"+accelerateDistance+", pd:"+plateauDistance+", dd:"+decelerateDistance+", "+moveVector.toString()+")";
	}
}
