package dk.osaa.psaw.config.obsolete;

import dk.osaa.psaw.machine.Move;
import dk.osaa.psaw.machine.MoveVector;
import lombok.Data;
import lombok.val;

/**
 * A class for holding the constraints and capabilities of the physical system.
 * 
 * @author ff
 *
 */
@Data
public class MovementConstraints {
	
	MovementContstraintAxis axes[] = new MovementContstraintAxis[Move.AXES];
	public double junctionDeviation;
	double rapidMoveSpeed;
	int tickHZ;
	double shortestMove;
	
	public MoveVector mmPerStep() {
		val v = new MoveVector();
		for (int ax=0;ax<Move.AXES;ax++) {
			v.setAxis(ax, axes[ax].mmPerStep);
		}
		return v;
	}
	
	public MovementConstraints() {
		tickHZ = 50000;
		
		junctionDeviation = 0.1;
		
		for (int i=0;i<Move.AXES;i++) {
			axes[i] = new MovementContstraintAxis();
			axes[i].acceleration = 5000;
			axes[i].maxSpeed     = 300;
			axes[i].minSpeed = 50;
			axes[i].maxJerk = 50;
			axes[i].microSteppingMode = 3;
			axes[i].axis = Move.AXIS_NAMES[i];
		}
		
		axes[0].mmPerStep = 60.0/(200*8);
		axes[0].coilCurrent = 350; 

		axes[1].mmPerStep = 60.0/(200*8);
		axes[1].coilCurrent = 350; 

		// Z-lift
		axes[2].coilCurrent = 350/2; //350*4; // 4 motors for Z-lift 
		axes[2].mmPerStep = 1.25/200; // M8x1.25 @full stepping
		axes[2].microSteppingMode = 0; // Full steps
		axes[2].maxSpeed = 100; // 16000 steps/second

		axes[3].mmPerStep = 60.0/(200*8);
		axes[3].coilCurrent = 350; 

		rapidMoveSpeed = 5000; // Just go!
		shortestMove = 0.1; // Any move shorter than this gets rounded off to 0 and dropped
	}
	
	public void fixAfterLoad() {
		for (int i=0;i<Move.AXES;i++) {
			axes[i].axis = Move.AXIS_NAMES[i];
			if (axes[i].maxJerk == 0 || axes[i].maxJerk == Double.NaN) {
				axes[i].maxJerk = axes[i].minSpeed;
			}
		}
	}

	/**
	 * @return The minimum speed of the system, as a vector.
	 */
	public MoveVector getMinSpeed() {
		MoveVector res = new MoveVector();
		for (int i=0;i<Move.AXES;i++) {
			res.setAxis(i, axes[i].minSpeed);
		}
		return res;
	}
}
