package dk.osaa.psaw.core;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.logging.Level;

import dk.osaa.psaw.machine.Move;
import dk.osaa.psaw.machine.MoveVector;
import dk.osaa.psaw.machine.MovementConstraints;
import dk.osaa.psaw.machine.Point;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.val;
import lombok.extern.java.Log;

/**
 * A line in n-dimensional space, this is the basic building block for all motion and where speed and
 * acceleration optimization happens.
 * 
 * Units used  this point: mm, mm/s and mm/s/s translation to steps, steps/tick and steps/tick/tick
 * happens later when Moves are generated.
 * 
 * The algorithms are inspired by Smoothie and GRBL, but somewhat rewritten/molested to do 4D.
 * 
 * I want to mostly get rid of the scalar speed calculations and switch to vectors so each axis 
 * gets optimized individually in stead of the current approach of mixing scalar speeds in different
 * directions in the same calculation, which is a horrible mess. 
 * 
 * @author ff
 */
@Data
@Log
public class Line {

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

	@Getter @Setter
	double laserIntensity;

	MoveVector prevUnitVector;
	
	
	public Line(MovementConstraints mc, Line prev, Point startPoint, Point endPoint, double targetMaxSpeed) {
		this.mc = mc;
		this.maxSpeed=targetMaxSpeed;
		
		endPoint.roundToWholeSteps(mc);

		// Initialize each axis.
		for (int a=0;a<Move.AXES;a++) {
			axes[a] = new LineAxis();
			
			// Round off to whole steps
 			axes[a].endPos = endPoint.getAxes()[a];
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

		// Limit maxSpeed to the speed obtainable with the motors
		for (int a=0;a<Move.AXES;a++) {
			if (unitVector.getAxis(a) == 0) {
				continue;
			}
			if (mc.getAxes()[a].maxSpeed < Math.abs(maxSpeed * unitVector.getAxis(a))) {
				maxSpeed = Math.abs(mc.getAxes()[a].maxSpeed / unitVector.getAxis(a));	
			}
		}
		log.fine("Calculated maxSpeed for line to: "+maxSpeed);
		
		// Set default max junction speed, to the minimum speed of the slowest of the axes:
		minSpeed = Double.NaN;
		for (int a=0;a<Move.AXES;a++) {
			if (mc.getAxes()[a].minSpeed < Math.abs(minSpeed * unitVector.getAxis(a)) || (Double.isNaN(minSpeed) && unitVector.getAxis(a) != 0)) {
				minSpeed = Math.abs(mc.getAxes()[a].minSpeed / unitVector.getAxis(a));	
			}
		}
		if (Double.isNaN(minSpeed)){
			throw new RuntimeException("Failed to calculate minimum speed for: mc:"+mc+" unit vector: "+unitVector);
		} 
		maxEntrySpeed = minSpeed;
		
		// Find the largest acceleration we can use for this line, by letting the most active*slowest axis set the limit 
		acceleration = Double.NaN;
		for (int a=0;a<Move.AXES;a++) {		
			if (mc.getAxes()[a].acceleration < Math.abs(acceleration * unitVector.getAxis(a)) || Double.isNaN(acceleration)) {
				acceleration = Math.abs(mc.getAxes()[a].acceleration / unitVector.getAxis(a));	
			}
		}
		if (Double.isNaN(acceleration) || acceleration == 0) {
			throw new RuntimeException("Failed to calculate accleration for line from "+startPoint+" to "+endPoint);
		}

	    // Initialize planner efficiency flags
	    // Set flag if block will always reach maximum junction speed regardless of entry/exit speeds.
	    // If a block can de/ac-celerate from nominal speed to zero within the length of the block, then
	    // the current block and next block junction speeds are guaranteed to always be at their maximum
	    // junction speeds in deceleration and acceleration, respectively. This is due to how the current
	    // block nominal speed limits both the current and next maximum junction speeds. Hence, in both
	    // the reverse and forward planners, the corresponding block junction speed will always be at the
	    // the maximum junction speed and may always be ignored for any speed reduction checks.
	    nominalLength = maxSpeed <= maxAllowableSpeed(-acceleration, minSpeed, length);
	    recalculate = true;

	    //log.info("a:"+acceleration + "s:"+ allowableSpeed + " for line from " + startPoint+" to "+endPoint);
	    
	    // The rest of this code is here to figure out how much speed the the previous line left us with, if any.
        if (prev == null) {
        	maxEntrySpeed = entrySpeed = minSpeed; // We start at a standstill.
        	return;
        }
        
        prevUnitVector = prev.unitVector;
        updateMaxEntrySpeed(minSpeed);
        entrySpeed = maxEntrySpeed;
        
        
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
	    
	    
	    // This method is borrowed from Smoothie and thus GRBL, I have no idea why they use such a complicated method though	    
        
        
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
//	    entrySpeed = Math.min(maxEntrySpeed, allowableSpeed);
	    entrySpeed = maxEntrySpeed;
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

	double exitSpeedFormaxAllowableSpeed = -1;
	private void updateMaxEntrySpeed(double exitSpeed) {
		if (exitSpeedFormaxAllowableSpeed == exitSpeed) {
			return;
		}
		exitSpeedFormaxAllowableSpeed = exitSpeed;
		
		double maxAllowableSpeed = Line.maxAllowableSpeed(-acceleration, exitSpeed, length);
		maxAllowableSpeed = Math.min(maxAllowableSpeed, maxSpeed);
		
        maxEntrySpeed = Double.NaN;
        if (prevUnitVector == null) {
        	maxEntrySpeed = minSpeed;
        	return;
        }
        
        for (int i=0;i<Move.AXES;i++) { 
        	if ((prevUnitVector.getAxis(i) == 0 && unitVector.getAxis(i) == 0)) {
        		continue;
        	}
        	
        	// Calculate the axis-allowable speed in the direction of the previous line   
        	double aa = (unitVector.getAxis(i) / prevUnitVector.getAxis(i)) * maxAllowableSpeed;
        	if (aa < 0) { 
        		aa = mc.getAxes()[i].minSpeed/3; // Direction change, so stop almost completely
        	} else {
        		aa = Math.max(mc.getAxes()[i].minSpeed, aa);
        	}        	
        	
        	// Figure out what the max entry speed would be if this was the limiting axis
        	double mes = unitVector.getAxis(i) == 0 
        				? minSpeed 
        				: Math.abs(aa / unitVector.getAxis(i));
        	
        	if (Double.isNaN(maxEntrySpeed) || maxEntrySpeed > mes) {
        		maxEntrySpeed = mes;
        	}        	
        }
        
        if (Double.isNaN(maxEntrySpeed)) {
        	maxEntrySpeed = minSpeed;
        } else {
        	maxEntrySpeed = Math.min(maxEntrySpeed, maxAllowableSpeed);
        }
	}
	
	// Called by Planner::recalculate() when scanning the plan from last to first entry.
	public void reversePass(Line next) {
		if (next == null) return; // This is the last line.
		
        // If entry speed is already at the maximum entry speed, no need to recheck. Block is cruising.
        // If not, block in state of acceleration or deceleration. Reset entry speed to maximum and
        // check for maximum allowable speed reductions to ensure maximum possible planned speed.
		//updateMaxEntrySpeed(next.entrySpeed);
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
	    if (prev == null) return; // This is the very first line or the previous line is no longer available, because it has been processed 

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
		exitSpeed = next==null ? 0 : next.entrySpeed;
		if (acceleration == 0) { // This is a point, not a line.
			return;
		}
		
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
	
	 
	static Writer logWriter;
	static void logLine(String l) {
		try {
			if (logWriter == null) {
				logWriter = new BufferedWriter(new FileWriter("/tmp/Line.log"));
			}
			logWriter.append(l);
			logWriter.append("\n");
			logWriter.flush();
		} catch (IOException e) {
			log.log(Level.SEVERE, "Failed to write data to log file", e);
		}
	}

	static long moveId = 0;	
	long stepsMoved[] = new long[Move.AXES];
	Move endcodeMove(MoveVector mmMoveVector, double startSpeedMMS, double endSpeedMMS) {		
		logLine(mmMoveVector+"\t"+startSpeedMMS+"\t"+endSpeedMMS);
		
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
				
		MoveVector accel = null;
		long ticks;
		if (startSpeedMMS == endSpeedMMS) {			
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
	
		Move move = new Move(moveId++, ticks);
		
		// TODO: handle laser acceleration too in stead of just setting it to max: 
		move.setLaserIntensity((int)Math.round(Math.max(0, Math.min(255, 255*laserIntensity))));
		
		for (int a=0; a < Move.AXES; a++) {
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
	
	public void toMoves(PhotonSaw photonSaw) throws InterruptedException {
		if (acceleration == 0) { // This is not a move, but a point
			return;
		}
		
		ArrayList<Move> output = new ArrayList<Move>(); 

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
			long stepsWanted = (long)Math.round((axes[i].endPos-axes[i].startPos)/mc.getAxes()[i].mmPerStep); 
			long diffSteps = stepsMoved[i] - stepsWanted;
			
			if (diffSteps != 0) {
				log.fine("Step difference on axis "+i+": "+diffSteps+ " wanted:"+stepsWanted+" got:"+stepsMoved[i]);				
				output.get(output.size()-1).nudgeSpeed(i, -diffSteps); // Modify speed of the last move, whatever it is
			}
		}
		
		for (Move m : output) {
			photonSaw.putMove(m);			
		}
	}
	
	public String toString() {
		return "Line(a:"+acceleration+", ad:"+accelerateDistance+", pd:"+plateauDistance+", dd:"+decelerateDistance+", "+moveVector.toString()+")";
	}
}
