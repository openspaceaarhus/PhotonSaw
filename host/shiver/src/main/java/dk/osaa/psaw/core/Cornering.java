package dk.osaa.psaw.core;

import lombok.Getter;
import lombok.extern.java.Log;
import dk.osaa.psaw.config.PhotonSawMachineConfig;
import dk.osaa.psaw.machine.Move;
import dk.osaa.psaw.machine.MoveVector;

/**
 * This class wraps up the algorithm needed to change direction without stopping.
 * 
 * @author Flemming Frandsen <dren.dk@gmail.com> <http://dren.dk>
 */
@Log
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
	
	private final PhotonSawMachineConfig cfg;
	
	
	/**
	 * Calculates the cornering parameters
	 * 
	 * @param cfg The movement constraints that govern the mechanical system that this drives
	 * @param entryVector The direction of the system before the corner
	 * @param entrySpeed The speed of the system before the corner
	 * @param exitVector The direction of the system after the corner
	 * @param maxExitSpeed The absolute maximum exit speed to target
	 */
	public Cornering(PhotonSawMachineConfig cfg, MoveVector entryVector, double entrySpeed, MoveVector exitVector, double maxExitSpeed) {
		this.cfg = cfg;
			
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
					max = cfg.getAxes().getArray()[ax].getMaxJerk() + Math.abs(entrySpeeds.getAxis(ax));							
	
				} else { // Direction change or starting from a standstill				
					max = cfg.getAxes().getArray()[ax].getMaxJerk()/2;				
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
				if (Math.abs(jerk) > cfg.getAxes().getArray()[ax].getMaxJerk()*1.01) {
					done = false;
					
					if (Math.signum(exitVector.getAxis(ax)) == Math.signum(entryVector.getAxis(ax))) {
						if (Math.abs(entrySpeeds.getAxis(ax)) > Math.abs(exitSpeeds.getAxis(ax))+cfg.getAxes().getArray()[ax].getMaxJerk()) {
							entrySpeed /= Math.abs(entrySpeeds.getAxis(ax)) / (Math.abs(exitSpeeds.getAxis(ax))+cfg.getAxes().getArray()[ax].getMaxJerk());
						} else {
							log.info("Fail!"+id);
						}
					} else {
						if (Math.abs(entrySpeeds.getAxis(ax)) > cfg.getAxes().getArray()[ax].getMaxJerk()/2) {
							entrySpeed /= Math.abs(entrySpeeds.getAxis(ax))/(cfg.getAxes().getArray()[ax].getMaxJerk()/2);
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

			if (Math.abs(jerk) > cfg.getAxes().getArray()[ax].getMaxJerk()*1.25) {
				throw new RuntimeException("Jerk was too large in axis: "+ax+" jerk was "+jerk+" max:"+cfg.getAxes().getArray()[ax].getMaxJerk()+" for corner id:"+id);
			}
		}
		return this;
	}
}
