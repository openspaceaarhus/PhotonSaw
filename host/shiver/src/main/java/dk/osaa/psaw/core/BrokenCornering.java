package dk.osaa.psaw.core;

import dk.osaa.psaw.config.AxisConstraints;
import dk.osaa.psaw.config.PhotonSawMachineConfig;
import dk.osaa.psaw.machine.Move;
import dk.osaa.psaw.machine.MoveVector;
import lombok.Getter;
import lombok.extern.java.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * This class wraps up the algorithm needed to change direction without stopping.
 * 
 * @author Flemming Frandsen <dren.dk@gmail.com> <http://dren.dk>
 */
@Log
public class BrokenCornering {

	private final List<Double> maxJerks;
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

	/**
	 * Calculates the cornering parameters
	 *
	 * @param cfg The movement constraints that govern the mechanical system that this drives
	 * @param entryVector The direction of the system before the corner
	 * @param entrySpeed The speed of the system before the corner
	 * @param exitVector The direction of the system after the corner
	 * @param maxExitSpeed The absolute maximum exit speed to target
	 */
	public BrokenCornering corner(PhotonSawMachineConfig cfg, MoveVector entryVector, double entrySpeed, MoveVector exitVector, double maxExitSpeed) {
		List<Double> maxJerks = new ArrayList<>();
		for (AxisConstraints ac : cfg.getAxes().getArray()) {
			maxJerks.add(ac.getMaxJerk());
		}

		return new BrokenCornering(maxJerks, entryVector, entrySpeed, exitVector, maxExitSpeed);
	}

	/**
	 * Calculates the cornering parameters
	 *
	 * @param maxJerks The max jerk limits for all the axes that govern the mechanical system that this drives
	 * @param entryVector The direction of the system before the corner
	 * @param entrySpeed The speed of the system before the corner
	 * @param exitVector The direction of the system after the corner
	 * @param maxExitSpeed The absolute maximum exit speed to target
	 */
	public BrokenCornering(List<Double> maxJerks, MoveVector entryVector, double entrySpeed, MoveVector exitVector, double maxExitSpeed) {
		this.maxJerks = maxJerks;
			
		id++;

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
					max = maxJerks.get(ax) + Math.abs(entrySpeeds.getAxis(ax));
	
				} else { // Direction change or starting from a standstill				
					max = maxJerks.get(ax)/2;
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
				if (Math.abs(jerk) > maxJerks.get(ax)*1.01) {
					done = false;
					
					if (Math.signum(exitVector.getAxis(ax)) == Math.signum(entryVector.getAxis(ax))) {
						if (Math.abs(entrySpeeds.getAxis(ax)) > Math.abs(exitSpeeds.getAxis(ax))+maxJerks.get(ax)) {
							entrySpeed /= Math.abs(entrySpeeds.getAxis(ax)) / (Math.abs(exitSpeeds.getAxis(ax))+maxJerks.get(ax));
						} else {
							log.info("Fail!"+id);
						}
					} else {
						if (Math.abs(entrySpeeds.getAxis(ax)) > maxJerks.get(ax)/2) {
							entrySpeed /= Math.abs(entrySpeeds.getAxis(ax))/(maxJerks.get(ax)/2);
						} else {
							log.info("Fail!"+id);							
						}
					}
				}
			}
		}
	
	}

	public BrokenCornering checkJerks() {
		for (int ax=0;ax<Move.AXES;ax++) {
			double jerk = jerks.getAxis(ax);

			if (Math.abs(jerk) > maxJerks.get(ax)*1.25) {
				throw new RuntimeException("Jerk was too large in axis: "+ax+" jerk was "+jerk+" max:"+maxJerks.get(ax)+" for corner id:"+id);
			}
		}
		return this;
	}
}
