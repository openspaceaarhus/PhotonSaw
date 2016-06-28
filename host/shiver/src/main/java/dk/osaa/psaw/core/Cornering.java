package dk.osaa.psaw.core;

import dk.osaa.psaw.config.AxisConstraints;
import lombok.Getter;
import lombok.extern.java.Log;
import dk.osaa.psaw.config.PhotonSawMachineConfig;
import dk.osaa.psaw.machine.Move;
import dk.osaa.psaw.machine.MoveVector;

import java.util.ArrayList;
import java.util.List;

/**
 * This class wraps up the algorithm needed to change direction without stopping.
 * 
 * @author Flemming Frandsen <dren.dk@gmail.com> <http://dren.dk>
 */
@Log
public class Cornering {

	private final List<Double> maxJerks;
	private long lineNumber;
	@Getter
	private final double limitedEntrySpeed;
	/**
	 * The speed along the exit vector after the corner.
	 */
	@Getter
	double exitSpeed;
	
	@Getter
	MoveVector jerks;
	
	@Getter
	MoveVector exitSpeeds;

	@Getter
	double maxEntrySpeed;

	static int id = 0;

	/**
	 * Calculates the cornering parameters
	 *  @param cfg The movement constraints that govern the mechanical system that this drives
	 * @param entryVector The direction of the system before the corner
	 * @param entrySpeed The speed of the system before the corner
	 * @param exitVector The direction of the system after the corner
	 * @param maxExitSpeed The absolute maximum exit speed to target
	 * @param lineNumber
	 */
	public static Cornering corner(PhotonSawMachineConfig cfg, MoveVector entryVector, double entrySpeed, MoveVector exitVector, double maxExitSpeed, long lineNumber) {
		List<Double> maxJerks = new ArrayList<>();
		for (AxisConstraints ac : cfg.getAxes().getArray()) {
			maxJerks.add(ac.getMaxJerk());
		}

		return new Cornering(maxJerks, entryVector, entrySpeed, exitVector, maxExitSpeed, lineNumber);
	}

	/**
	 * Calculates the cornering parameters
	 *  @param maxJerks The max jerk limits for all the axes that govern the mechanical system that this drives
	 * @param entryVector The direction of the system before the corner
	 * @param entrySpeed The speed of the system before the corner
	 * @param exitVector The direction of the system after the corner
	 * @param maxExitSpeed The absolute maximum exit speed to target
	 * @param lineNumber
	 */
	public Cornering(List<Double> maxJerks, MoveVector entryVector, double entrySpeed, MoveVector exitVector, double maxExitSpeed, long lineNumber) {
		this.maxJerks = maxJerks;
		this.lineNumber = lineNumber;

		id++;

		//if (lineNumber == 286) {
		//	log.info("Hit "+id);
		//}

		// Find the axis that's moving the fastest in the same direction before and after the corner
		Integer fixedAxis = null;
		double fixedPreservation = 0;
		MoveVector preservation = entryVector.mul(exitVector);
		for (int ax=0;ax<Move.AXES;ax++) {
			double preserved = preservation.getAxis(ax)*entrySpeed;
			if (preserved > fixedPreservation) {
				fixedPreservation = preserved;
				fixedAxis = ax;
			}
		}

		// Limit the entry speed until all the jerks are under the max jerk limits
		while (true) {
			exitSpeed = 0;

			// An axis was found that moves in the same direction as before, so we'll start out trying to keep that axis moving at exactly the same speed
			if (fixedAxis != null) {
				exitSpeed = entrySpeed*preservation.getAxis(fixedAxis);
			}

			// Limit the exit speed according to the absolute limit passed:
			if (exitSpeed > maxExitSpeed) {
				exitSpeed = maxExitSpeed;
			}

			exitSpeeds = exitVector.mul(exitSpeed);
			MoveVector entrySpeeds = entryVector.mul(entrySpeed);
			jerks = new MoveVector();
			double biggestOverspeed = 1;
			for (int ax = 0; ax < Move.AXES; ax++) {
				double jerk = Math.abs(exitSpeeds.getAxis(ax) - entrySpeeds.getAxis(ax));
				jerks.setAxis(ax, jerk);
				double overspeed = jerk/maxJerks.get(ax);
				if (overspeed > biggestOverspeed) {
					biggestOverspeed = overspeed;
				}
			}

			entrySpeed = entrySpeed/biggestOverspeed;
			if (biggestOverspeed <= 1) {
				break;
			}
		}

		limitedEntrySpeed = entrySpeed;
	}

	public Cornering checkJerks() {
		for (int ax=0;ax<Move.AXES;ax++) {
			double jerk = jerks.getAxis(ax);

			if (Math.abs(jerk) > maxJerks.get(ax)*1.25) {
				throw new RuntimeException("Jerk was too large in axis: "+ax+" jerk was "+jerk+" max:"+maxJerks.get(ax)+" for corner id:"+id);
			}
		}
		return this;
	}
}
