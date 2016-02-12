package dk.osaa.psaw.machine;

import dk.osaa.psaw.config.PhotonSawMachineConfig;
import lombok.Data;
import lombok.ToString;

/**
 * A Point in n-dimensional space.
 * 
 * @author ff
 */
@Data
@ToString 
public class Point {
	public double axes[] = new double[Move.AXES];
	
	public void roundToWholeSteps(PhotonSawMachineConfig cfg) {
		for (int i=0;i<Move.AXES;i++) {
			double mmPerStep = cfg.getMmPerStep().getAxis(i);
			long stepPos = (long)Math.round(getAxes()[i]/mmPerStep);
 			axes[i] = stepPos*mmPerStep;
		}
	}
 }
