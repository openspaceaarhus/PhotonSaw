package dk.osaa.psaw.core;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.logging.Level;

import dk.osaa.psaw.config.MovementConstraints;
import dk.osaa.psaw.machine.Move;
import dk.osaa.psaw.machine.MoveVector;
import dk.osaa.psaw.machine.Point;
import dk.osaa.psaw.machine.Q16;

import lombok.Getter;
import lombok.Setter;
import lombok.val;
import lombok.extern.java.Log;

/**
 * A line in n-dimensional space, this is the basic building block for all motion and where feed rate and
 * acceleration optimization happens.
 * 
 * Units used this point: mm, mm/s and mm/s/s translation to steps, steps/tick and steps/tick/tick
 * happens when Moves are generated.
 * 
 * The algorithms are inspired by Smoothie and GRBL, but rewritten/molested to do 4D and do vector based
 * feed rate optimization in stead of the more primitive angle based method needed on 8 bit controllers.
 * 
 * @author ff
 */
@Log
public class Line {

	class LineAxis {
		double startPos;
		double endPos; // Where this axis must end up when done
		int direction; // The direction this axis moves in, basically the sign for the speed.
	};
	LineAxis axes[] = new LineAxis[Move.AXES];
	MovementConstraints mc;
	double maxSpeed;     	// The speed limit
	double minSpeed;     	// The speed we can jump to
	double acceleration; 	// The acceleration possible in this direction  

	// Careful now! These two fields are the most important bits to get your head around:
	double maxEntrySpeed;  	// The highest speed we can allow the previous line to leave us with, constrained by the next line
	double entrySpeed;   	// The actual speed we get to start with, contributed by the previous line, constrained by maxEntrySpeed
	double exitSpeed;   	// The actual speed at the end of this line, updated by updateEntrySpeed
	@Getter @Setter
	double mandatoryExitSpeed; // The speed that we must attain at the exit of this line.

	@Getter
	double length;			// The number of mm from start to finish
	MoveVector unitVector = new MoveVector();
	MoveVector moveVector = new MoveVector();
	
	// Optimization flags. 
	boolean nominalLength; 	// This line is long enough to allow full acceleration from 0 to maxSpeed. 

	@Getter
	boolean recalculateNeeded;  	// Has the constraints changed so we need to recalculate 
	
	@Getter @Setter
	double laserIntensity;

	@Getter @Setter
	boolean[] pixels;
	
	@Getter @Setter
	boolean assistAir;
	
	@Getter @Setter
	boolean endPosDirty;

	
	public Line(MovementConstraints mc, Line prev, Point startPoint, Point endPoint, double targetMaxSpeed) {
		this.pixels = null;
		this.mc = mc;
		this.maxSpeed=targetMaxSpeed;
		
		endPoint.roundToWholeSteps(mc);
		endPosDirty = false;
		mandatoryExitSpeed = -1;

		// Initialize each axis.
		for (int a=0;a<Move.AXES;a++) {
			axes[a] = new LineAxis();
 			axes[a].endPos = endPoint.getAxes()[a];
		}
		
		setStartPoint(startPoint);
	}
	
	public void setStartPoint(Point startPoint) {
		for (int a=0;a<Move.AXES;a++) {
			axes[a].startPos = startPoint.getAxes()[a];
		}		
		
		for (int a=0;a<Move.AXES;a++) {
			axes[a].direction = axes[a].startPos == axes[a].endPos ? 0 :
				axes[a].startPos <  axes[a].endPos ? 1 : -1;
		}
		
		// Calculate the unity vector for this line, because it's handy for calculating the maximum speed of each axis during the move.
		for (int a=0;a<Move.AXES;a++) {
			moveVector.setAxis(a, axes[a].endPos-axes[a].startPos);
		}

		length = moveVector.length();
		if (length == 0) {
			return;
		}
		unitVector = moveVector.unit();
		
		/**
		 * Find the max speed, min speed and acceleration limits for movement along this vector
		 * by constricting each value to accommodate the slowest involved axis.   
		 */		
		minSpeed = Double.NaN;
		acceleration = Double.NaN;
		for (int a=0;a<Move.AXES;a++) {
			if (unitVector.getAxis(a) == 0) {
				continue;
			}
			if (mc.getAxes()[a].maxSpeed < Math.abs(maxSpeed * unitVector.getAxis(a))) {
				maxSpeed = Math.abs(mc.getAxes()[a].maxSpeed / unitVector.getAxis(a));	
			}
			if (Double.isNaN(minSpeed) || mc.getAxes()[a].minSpeed < Math.abs(minSpeed * unitVector.getAxis(a))) {
				minSpeed = Math.abs(mc.getAxes()[a].minSpeed / unitVector.getAxis(a));	
			}
			if (Double.isNaN(acceleration) || mc.getAxes()[a].acceleration < Math.abs(acceleration * unitVector.getAxis(a))) {
				acceleration = Math.abs(mc.getAxes()[a].acceleration / unitVector.getAxis(a));	
			}
		}
		log.fine("Calculated maxSpeed for line to: "+maxSpeed);

		if (Double.isNaN(minSpeed)){
			throw new RuntimeException("Failed to calculate minimum speed for: mc:"+mc+" unit vector: "+unitVector);
		} 
		
		if (Double.isNaN(acceleration) || acceleration == 0) {
			throw new RuntimeException("Failed to calculate accleration for line from "+startPoint);
		}
		maxEntrySpeed = minSpeed;

		
	    // Initialize planner efficiency flags
	    // Set flag if block will always reach maximum junction speed regardless of entry/exit speeds.
	    // If a block can de/ac-celerate from nominal speed to zero within the length of the block, then
	    // the current block and next block junction speeds are guaranteed to always be at their maximum
	    // junction speeds in deceleration and acceleration, respectively. This is due to how the current
	    // block nominal speed limits both the current and next maximum junction speeds. Hence, in both
	    // the reverse and forward planners, the corresponding block junction speed will always be at the
	    // the maximum junction speed and may always be ignored for any speed reduction checks.
	    nominalLength = maxSpeed <= maxAllowableSpeed(-acceleration, minSpeed, length);
	    recalculateNeeded = true;

	    //log.info("a:"+acceleration + "s:"+ allowableSpeed + " for line from " + startPoint+" to "+endPoint);

       	maxEntrySpeed = entrySpeed = 0; // We start at a standstill.
       	
       	updateMaxEntrySpeed(null);       	
         /*       
        // Verify that we have not exceeded the limits for each axis
	    MoveVector accelerationVector = unitVector.mul(acceleration);
	    MoveVector speedVector = unitVector.mul(maxSpeed);
	    for (int i=0;i<Move.AXES;i++) {
	    	if (Math.abs(accelerationVector.getAxis(i)) > mc.getAxes()[i].acceleration+10) {
	    	    log.severe("Too high acceleration:"+acceleration + " is too great for axis:"+ i + " aa: "+ Math.abs(accelerationVector.getAxis(i))+ " for line from " + startPoint+" to "+endPoint);
	    	} 	    	
	    	if (Math.abs(speedVector.getAxis(i)) > mc.getAxes()[i].maxSpeed+10) {
	    	    log.severe("Too high speed:"+ maxSpeed + " is too great for axis:"+ i + " as: "+ Math.abs(speedVector.getAxis(i)) + " for line from " + startPoint+" to "+endPoint);
	    	} 	    	
	    }	
	    */    
	}

	public Point getEndPoint() {
		Point ep = new Point();
		for (int i=0;i<Move.AXES;i++) {
			ep.axes[i] = axes[i].endPos;
		}
		return ep;
	}
	
	// Calculates the maximum allowable speed at this point when you must be able to reach target_velocity
	// using the acceleration within the allotted distance.
	static double maxAllowableSpeed(double acceleration, double targetVelocity, double distance) {
	  return Math.sqrt(
			  Math.pow(targetVelocity, 2)-
			  2*acceleration*distance);
	}
	
	/**
	 * Answers the question: How fast can we allow the previous line to be moving in our direction when handing off to us.
	 * The field updated is maxEntrySpeed
	 * 
	 * There are two limiting factors
	 *  * The absolute maximum speeds at the start of the next line 
	 *  * The 
	 * @param next The next line which we have to hand off to when we're done, if null, then we have to come to a stop.
	 */
	double maxExitSpeed;
	Line nextStored;
	private void updateMaxEntrySpeed(Line next) {	
		MoveVector maxEndSpeeds; // The speed limits as imposed by the max entry speed of the next line		
		if (next == null || assistAir != next.assistAir) { // We must come to a stop, because there isn't a next move to handle stopping for us.			
			maxEndSpeeds = new MoveVector();
		} else {
			maxEndSpeeds = next.unitVector.mul(next.maxEntrySpeed);
			nextStored = next;			
		}

		// Find the maximum speed along this normal vector which doesn't exceed the maxEndSpeed limit.
		maxExitSpeed = maxSpeed;
		for (int i=0;i<Move.AXES;i++) {
			// Scale down end speed until we can transition to the target vector without violating the jerk limit 
        	double maxEndSpeedAxis     = unitVector.getAxis(i) * maxExitSpeed;
        	double nextStartSpeedAxis  = maxEndSpeeds.getAxis(i);
        	
        	//double jerk = maxEndSpeedAxis-nextStartSpeedAxis;
        	
        	if (Math.signum(maxEndSpeedAxis) == Math.signum(nextStartSpeedAxis)) {
        		// We're going in the same direction as the next line, so just make sure we aren't going too fast.
    			double jerk = Math.abs(maxEndSpeedAxis) - Math.abs(nextStartSpeedAxis);

    			if (jerk > mc.getAxes()[i].maxJerk) {
    				double jerkFactor = jerk/mc.getAxes()[i].maxJerk;
        			maxExitSpeed /= jerkFactor;
        		}
	        		
        	} else { // Direction change, so limit the end speed to a half jerk, thus leaving the other half of the jerk for the acceleration
        		if (unitVector.getAxis(i) != 0) {
        			double jerkLimit = Math.abs((mc.getAxes()[i].maxJerk/2) / unitVector.getAxis(i));
        			if (maxExitSpeed > jerkLimit) {
        				maxExitSpeed = jerkLimit;
        			}
        		}
        	}
        }
		
		if (mandatoryExitSpeed >= 0) {
			double diffExit = mandatoryExitSpeed-maxExitSpeed;
			
			if (diffExit > 0.1) {
				throw new RuntimeException("The maxExitSpeed of this line was too low by "+diffExit+" mm/s, it was planned to be "+maxExitSpeed+" but should have been at least"+mandatoryExitSpeed);				
			}			
		}

		// Figure out how fast we can allow the previous line to run when handing off:
		maxEntrySpeed = Line.maxAllowableSpeed(-acceleration, maxExitSpeed, length);
		if (maxEntrySpeed > maxSpeed) {
			maxEntrySpeed = maxSpeed;
		}

		if (maxEntrySpeed < 0) {
			throw new RuntimeException("Calculated a negative maxEntrySpeed: "+maxEntrySpeed);			
		}
		if (maxExitSpeed < 0) {
			throw new RuntimeException("Calculated a negative maxExitSpeed: "+maxExitSpeed);			
		}
	}

	/**
	 * Answers the question: How fast is the system going when control is being handed over by the previous line
	 * The answer is provided in the entrySpeed field.
	 * 
	 * @param prev the previous line, if null then we're starting from a standstill.
	 */
	private void updateEntrySpeed(Line prev) {
		
		MoveVector prevSpeeds = new MoveVector();
		if (prev != null && prev.assistAir == assistAir) {
			prevSpeeds = prev.unitVector.mul(prev.exitSpeed);
		}		
		
		entrySpeed = maxEntrySpeed;
		for (int i=0;i<Move.AXES;i++) {
        	double axisSpeed = unitVector.getAxis(i) * entrySpeed;
        	double prevSpeed = prevSpeeds.getAxis(i);
        	
        	if (Math.signum(axisSpeed) == Math.signum(prevSpeed)) {
    			double jerk = Math.abs(axisSpeed) - Math.abs(prevSpeed);
    			if (jerk > mc.getAxes()[i].maxJerk) {
        			double jerkFactor = jerk / mc.getAxes()[i].maxJerk;
        			entrySpeed /= jerkFactor;
    			}

        	} else {
        		if (unitVector.getAxis(i) != 0) {
        			double halfJerk = Math.abs((mc.getAxes()[i].maxJerk/2) / unitVector.getAxis(i));
        			if (entrySpeed > halfJerk) {
        				entrySpeed = halfJerk;        			
        			}
        		}
        	}        	
		}
		
		if (entrySpeed > maxEntrySpeed) {
			throw new RuntimeException("Calculated too high an entry speed: "+entrySpeed+" maxEntrySpeed="+maxEntrySpeed);			
		}
		
		if (entrySpeed < 0) {
			throw new RuntimeException("Calculated a negative speed: "+entrySpeed);			
		}
		
		// Calculate our own exit speed
		exitSpeed = Line.maxAllowableSpeed(-acceleration, entrySpeed, length);			
		if (exitSpeed > maxExitSpeed) {
			exitSpeed = maxExitSpeed;
		}		
		if (exitSpeed < 0) {
			throw new RuntimeException("Calculated a negative exitSpeed: "+exitSpeed);			
		}
		
		if (mandatoryExitSpeed >= 0) {
			double diffExit = mandatoryExitSpeed-exitSpeed;
			
			if (Math.abs(diffExit) > 0.1) {
				throw new RuntimeException("The exit speed of this line was off by "+diffExit+" mm/s, it was planned to be "+exitSpeed+" but should have been "+mandatoryExitSpeed);				
			}			
		}		
	}
	
	// Called by Planner::recalculate() when scanning the plan from last to first entry.
	public void reversePass(Line next) {
		if (next == null) return; // This is the last line.
		updateMaxEntrySpeed(next);
        recalculateNeeded = true;

        
        // If entry speed is already at the maximum entry speed, no need to recheck. Block is cruising.
        // If not, block in state of acceleration or deceleration. Reset entry speed to maximum and
        // check for maximum allowable speed reductions to ensure maximum possible planned speed.        
/*
		if (entrySpeed != maxEntrySpeed) {

            // If nominal length true, max junction speed is guaranteed to be reached. Only compute
            // for max allowable speed if block is decelerating and nominal length is false.
            if ((!nominalLength) && (maxEntrySpeed > next.entrySpeed)) {
                entrySpeed = Math.min( maxEntrySpeed,
                		Line.maxAllowableSpeed(-acceleration, entrySpeed, length));
            } else {
                entrySpeed = maxEntrySpeed;
            }
            recalculateNeeded = true;
        }
        */
	}
	
	// Called by Planner::recalculate() when scanning the plan from first to last entry.
	public void forwardPass(Line prev) {
	    if (prev == null) return; // This is the very first line or the previous line is no longer available, because it has been processed 

	    updateEntrySpeed(prev);
        recalculateNeeded = true;
/*	    
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
	            recalculateNeeded = true;
	          }
	        }
	    }
	    */
	}

	
	// Calculates the distance (not time) it takes to accelerate from initial_rate to target_rate using the
	// given acceleration:
	static public double estimateAccelerationDistance(double initialrate, double targetrate, double acceleration) {
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
	static double intersectionDistance(double initialrate, double finalrate, double acceleration, double distance) {
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
	void calculateTrapezoid(Line next) {
		if (acceleration == 0) { // This is a point, not a line.
			return;
		}
		
		if (mandatoryExitSpeed >= 0 && next != null) {
			double diffExit = mandatoryExitSpeed-exitSpeed;
			
			if (Math.abs(diffExit) > 0.1) {
				throw new RuntimeException("The exit speed of this line was off by "+diffExit+" mm/s, it was planned to be "+exitSpeed+" but should have been "+mandatoryExitSpeed);				
			}			
		}		
		
	    accelerateDistance = estimateAccelerationDistance(entrySpeed, maxSpeed, acceleration);
	    decelerateDistance = estimateAccelerationDistance(maxSpeed, exitSpeed, -acceleration);
	    
	    if (accelerateDistance < 0) {
	    	accelerateDistance = 0;
	    }
	    if (decelerateDistance < 0) {
	    	decelerateDistance = 0;
	    }
	    
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

	    if (accelerateDistance == 0 && entrySpeed == 0) {
	    	throw new RuntimeException("Fail, entry speed is lower than maxSpeed, but no acceleration distance found: entry:"+entrySpeed+" max:"+maxSpeed);
	    }
	    	    
	    if (accelerateDistance < 0) {
	    	throw new RuntimeException("Calculated negative length for accelerateDistance: "+accelerateDistance
	    			+" entrySpeed="+entrySpeed
	    			+" maxSpeed="+maxSpeed
	    			+" acceleration="+acceleration
	    			+" exitSpeed="+exitSpeed
	    			);	    		    	
	    }	
	    if (decelerateDistance < 0) {
	    	throw new RuntimeException("Calculated negative length for decelerateDistance: "+decelerateDistance);	    		    	
	    }	
	    if (plateauDistance < 0) {
	    	throw new RuntimeException("Calculated negative length for plateauDistance: "+plateauDistance);	    		    	
	    }	
	    
	    recalculateNeeded = false;
	}
	
	 
	static Writer logWriter;
	static void logLine(String l) {
		try {
			if (logWriter == null) {
				logWriter = new BufferedWriter(new FileWriter("line.log"));
			}
			logWriter.append(l);
			logWriter.append("\n");
			logWriter.flush();
		} catch (IOException e) {
			log.log(Level.SEVERE, "Failed to write data to log file", e);
		}
	}

	static long moveId = 0;
	static public long getMoveId() {
		return moveId++;
	}
	
	long stepsMoved[] = new long[Move.AXES];
	private static long stepCount = 0;
	
	Move endcodeMove(MoveVector mmMoveVector, double startSpeedMMS, double endSpeedMMS) {
		if (startSpeedMMS < 0) {
			throw new RuntimeException("start speed cannot be negative");
		}
		if (endSpeedMMS < 0) {
			throw new RuntimeException("end speed cannot be negative");
		}		
		
		//logLine(mmMoveVector+"\t"+startSpeedMMS+"\t"+endSpeedMMS);
		
		val stepVector = mmMoveVector.div(mc.mmPerStep()).round(); // move vector in whole steps
		
		val unitVector = mmMoveVector.unit();
		MoveVector startSpeedVector = unitVector.mul(startSpeedMMS/mc.getTickHZ()).div(mc.mmPerStep()); // convert from scalar mm/s to vector step/tick

		// Find the longest axis, so we can use it for calculating the duration of the move, this way we get better accuracy.
		int longAxis = 0;
		double longAxisLength = -1;
		for (int i=0;i<Move.AXES;i++) {
			if (Math.abs(stepVector.getAxis(i)) > longAxisLength) {
				longAxisLength = Math.abs(stepVector.getAxis(i));
				longAxis = i;
			}
		}
		
		if (longAxisLength <= 0) {
			return null; // No steps in any direction, so don't generate a move.  
		}		
				
		MoveVector accel = null;
		long ticks;
		if (startSpeedMMS == endSpeedMMS) {
			if (startSpeedVector.getAxis(longAxis) == 0) {
				throw new RuntimeException("Fail! start speed and end speed is zero, but move has a length along axis "+longAxis+" "+longAxisLength+" steps");
			}
			
			ticks = (long)Math.ceil(stepVector.getAxis(longAxis) / startSpeedVector.getAxis(longAxis));
			
		} else {
			MoveVector endSpeedVector   = unitVector.mul(endSpeedMMS/mc.getTickHZ()).div(mc.mmPerStep());
			
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
		
		if (ticks == 0) {
			throw new RuntimeException("The number of ticks in a move cannot be 0: "+stepVector);
		}
		if (ticks < 0) {
			throw new RuntimeException("The number of ticks in a move cannot be less than 0: "+stepVector);
		}
	
		Move move = new Move(moveId++, ticks);
		
		int startIntensity = (int)Math.round(Math.max(0, Math.min(255,laserIntensity*255*startSpeedMMS/maxSpeed)));
		int endIntensity   = (int)Math.round(Math.max(0, Math.min(255,laserIntensity*255*endSpeedMMS  /maxSpeed)));
		move.setLaserIntensity(startIntensity);
		move.setLaserAcceleration(new Q16((endIntensity-startIntensity)/ticks));
		
		for (int a=0; a < Move.AXES; a++) {
			move.setAxisStartPos(a, Math.round(axes[a].startPos / mc.getAxes()[a].mmPerStep) + stepsMoved[a]);
			
			move.setAxisSpeed(a, startSpeedVector.getAxis(a));
			if (accel != null) {
				move.setAxisAccel(a, accel.getAxis(a));
			}
			
			// Check that we got exactly the movement in steps that we wanted,
			// if not adjust the speed until the error is gone.
			long steps = move.getAxisLength(a);
			long stepsWanted = (long)Math.round(mmMoveVector.getAxis(a)/mc.getAxes()[a].mmPerStep);
			
			long diffSteps = steps - stepsWanted;
			if (diffSteps != 0) {
				log.fine("Did not get correct movement in axis "+a+" wanted:"+stepsWanted+" got:"+steps);				
				move.nudgeSpeed(a, -diffSteps);
					
				// TODO: This is a ghastly hack, I know, but damn it, it works and I don't know what else to do.
				// I'd much rather have a system that's able to calculate the correct speed and acceleration the first time
				// rather than have to rely on nudging the speed parameter up and down after the inaccuracy has been detected.
				if (diffSteps != 0) {
					steps = move.getAxisLength(a);
					diffSteps = steps - stepsWanted;
					if (diffSteps != 0) {						 
						move.nudgeSpeed(a, -diffSteps/2);
						log.warning("Did not get correct movement in axis after correction "+a+" wanted:"+stepsWanted+" got:"+steps+", compensating...");
						steps = move.getAxisLength(a);
					}
				}
			}

			stepsMoved[a] += steps;
			move.setAxisEndPos(a, Math.round(axes[a].startPos / mc.getAxes()[a].mmPerStep) + stepsMoved[a]);
		}		

		if (pixels != null) {			
			move.setScanline(pixels);
		}
				
		return move;
	}
	
	public void toMoves(PhotonSaw photonSaw) throws InterruptedException {
		if (acceleration == 0) { // This is not a move, but a point
			return;
		}
		
		if (exitSpeed < 0) {
			throw new RuntimeException("exitSpeed cannot be negative");
		}
		if (entrySpeed < 0) {
			throw new RuntimeException("entrySpeed cannot be negative");
		}
		
		if (mandatoryExitSpeed >= 0) {
			double diffExit = mandatoryExitSpeed-exitSpeed;
			
			if (Math.abs(diffExit) > 0.1) {
				throw new RuntimeException("The exit speed of this line was off by "+diffExit+" mm/s, it was planned to be "+exitSpeed+" but should have been "+mandatoryExitSpeed);				
			}			
		}
		
		ArrayList<Move> output = new ArrayList<Move>(); 
		
		photonSaw.putAssistAir(assistAir);

		int mbf = output.size();
		for (int i=0;i<Move.AXES;i++) {
			stepsMoved[i] = 0;
		}
		double topSpeed = entrySpeed;
		
		if ((accelerateDistance > 0 || decelerateDistance > 0) && pixels != null) {
			throw new RuntimeException("Engraving line contains acceleration or deceleration ramps. ad="+accelerateDistance+" dd="+decelerateDistance);
		}
		
		if (entrySpeed != maxSpeed && pixels != null) {
			throw new RuntimeException("Engraving line does not start at full speed: entrySpeed != maxSpeed: "+entrySpeed+" != "+ maxSpeed);			
		}
		
		if (accelerateDistance > 0) {
			topSpeed = entrySpeed+acceleration*Math.sqrt(accelerateDistance/acceleration);
			output.add(endcodeMove(unitVector.mul(accelerateDistance), entrySpeed, topSpeed));			
		}
		
		if (plateauDistance > 0) {
			if (topSpeed == 0) {
				throw new RuntimeException("Fail");
			}
			output.add(endcodeMove(unitVector.mul(plateauDistance), topSpeed, topSpeed));
		}
		
		if (decelerateDistance > 0) {
			output.add(endcodeMove(unitVector.mul(decelerateDistance), topSpeed, exitSpeed));			
		}
		
		int index = 0;
		while (index < output.size()) {
			if (output.get(index) == null) {
				output.remove(index);
			} else {
				index++;
			}
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
			long stepsWanted = (long)Math.round((axes[i].endPos-axes[i].startPos)/mc.getAxes()[i].mmPerStep); 
			long diffSteps = stepsMoved[i] - stepsWanted;
			
			if (diffSteps != 0) {
				if (Math.abs(diffSteps) != 0) {
					log.warning("Step difference on axis "+i+": "+diffSteps+ " wanted:"+stepsWanted+" got:"+stepsMoved[i]+" a:"+accelerateDistance+" p:"+plateauDistance+" d:"+decelerateDistance);
				}

				/*
				 *  This signals the planner that this move didn't actually land where we wanted to, so the planner has to update the start point of
				 *  the next line to compensate and recalculate all the lines in the buffer.
				 */				
				axes[i].endPos += diffSteps*mc.getAxes()[i].mmPerStep;
				endPosDirty = true;
			}
		}
				
		for (Move m : output) {
			stepCount += m.getDuration();
			photonSaw.putMove(m);			
		}
		
		logLine(stepCount+" "+maxSpeed+" "+maxEntrySpeed+" "+maxExitSpeed+" "+entrySpeed+" "+exitSpeed+" "+ accelerateDistance +" "+ plateauDistance +" "+ decelerateDistance);
	}
	
	public String toString() {
		return "Line(a:"+acceleration+", ad:"+accelerateDistance+", pd:"+plateauDistance+", dd:"+decelerateDistance+", "+moveVector.toString()+")";
	}
}
