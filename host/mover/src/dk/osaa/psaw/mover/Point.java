package dk.osaa.psaw.mover;

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
	double axes[] = new double[Move.AXES];
	
	public void roundToWholeSteps(MovementConstraints mc) {
		for (int i=0;i<Move.AXES;i++) {
			long stepPos = (long)Math.round(getAxes()[i]/mc.getAxes()[i].mmPerStep);
 			axes[i] = stepPos*mc.getAxes()[i].mmPerStep;
		}
	}
 }
