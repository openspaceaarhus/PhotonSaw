package dk.osaa.psaw.core;

import javax.management.RuntimeErrorException;

import org.eclipse.jetty.util.log.Log;

import lombok.Getter;
import dk.osaa.psaw.config.MovementConstraints;
import dk.osaa.psaw.machine.Move;
import dk.osaa.psaw.machine.MoveVector;

/**
 * This class wraps up the algorithm needed to change direction without stopping.
 * 
 * @author Flemming Frandsen <dren.dk@gmail.com> <http://dren.dk>
 */
@lombok.extern.java.Log
public class Cornering {

	/**
	 * The speed along the exit vector after the corner.
	 */
	@Getter
	double exitSpeed;
	
	@Getter
	MoveVector jerks;
	
	@Getter
	MoveVector exitSpeeds;
	
	static int id = 0;
	
	MovementConstraints mc;
	
	/**
	 * Calculates the cornering parameters
	 * 
	 * @param mc The movement constraints that govern the mechanical system that this drives
	 * @param entryVector The direction of the system before the corner
	 * @param entrySpeed The speed of the system before the corner
	 * @param exitVector The direction of the system after the corner
	 * @param maxExitSpeed The absolute maximum exit speed to target
	 */
	public Cornering(MovementConstraints mc, MoveVector entryVector, double entrySpeed, MoveVector exitVector, double maxExitSpeed) {
		this.mc = mc;
			
		id++;
		if (id == 43) {
			log.info("Hit");
		}
		
		boolean done = false;
		while (!done) {
			MoveVector entrySpeeds = entryVector.mul(entrySpeed);
			double overSpeed = -1;
			exitSpeed = maxExitSpeed;
			MoveVector maxExitSpeeds = exitVector.mul(maxExitSpeed);
			
			/*
			 * Find the speed limit for each axis,
			 * then compare that to the entry speed
			 * and pick the axis that is going to constrain the corner   
			 */		
			for (int ax=0;ax<Move.AXES;ax++) {			
				
				double max;
				if (Math.signum(exitVector.getAxis(ax)) == Math.signum(entryVector.getAxis(ax))) { // Continuing along in the same direction.
					max = mc.getAxes()[ax].maxJerk + Math.abs(entrySpeeds.getAxis(ax));							
	
				} else { // Direction change or starting from a standstill				
					max = mc.getAxes()[ax].maxJerk/2;				
				}
				
				// This limits the exit speed under the assumption that entry speed is low enough to be able to corner within the mc.
				double os = Math.abs(maxExitSpeeds.getAxis(ax))/max;		
				if (os > 1 && os > overSpeed) {
					overSpeed = os;
				}
			}
			
			if (overSpeed > 1) {
				exitSpeed = exitSpeed/overSpeed;
			}
			
			exitSpeeds = exitVector.mul(exitSpeed);		
			jerks = new MoveVector();
		
			done = true;
			for (int ax=0;ax<Move.AXES;ax++) {
				double jerk = exitSpeeds.getAxis(ax)-entrySpeeds.getAxis(ax);
				jerks.setAxis(ax, jerk);
				if (Math.abs(jerk) > mc.getAxes()[ax].maxJerk*1.01) {
					done = false;
					
					if (Math.signum(exitVector.getAxis(ax)) == Math.signum(entryVector.getAxis(ax))) {
						if (Math.abs(entrySpeeds.getAxis(ax)) > Math.abs(exitSpeeds.getAxis(ax))+mc.getAxes()[ax].maxJerk) {
							entrySpeed /= Math.abs(entrySpeeds.getAxis(ax)) / (Math.abs(exitSpeeds.getAxis(ax))+mc.getAxes()[ax].maxJerk);
						} else {
							log.info("Fail!"+id);
						}
					} else {
						if (Math.abs(entrySpeeds.getAxis(ax)) > mc.getAxes()[ax].maxJerk/2) {
							entrySpeed /= Math.abs(entrySpeeds.getAxis(ax))/(mc.getAxes()[ax].maxJerk/2);
						} else {
							log.info("Fail!"+id);							
						}
					}
				}
			}
		}
	
	}

	public Cornering checkJerks() {
		for (int ax=0;ax<Move.AXES;ax++) {
			double jerk = jerks.getAxis(ax);

			if (Math.abs(jerk) > mc.getAxes()[ax].maxJerk*1.25) {
				throw new RuntimeException("Jerk was too large in axis: "+ax+" jerk was "+jerk+" max:"+mc.getAxes()[ax].maxJerk+" for corner id:"+id);
			}
		}
		return this;
	}
}
