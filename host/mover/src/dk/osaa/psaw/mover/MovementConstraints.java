package dk.osaa.psaw.mover;

import lombok.Data;

/**
 * A class for holding the constraints and capabilities of the physical system.
 * 
 * @author ff
 *
 */
@Data
public class MovementConstraints {
	
	static class MovementContstraintAxis {
		double acceleration; // mm/s/s
		double maxSpeed;     // mm/s
		double minSpeed;     // mm/s

		double mmPerStep;    // mm / step
		int coilCurrent;     // mA
		int microSteppingMode; // 0=fullstep, 1=halfstep, 2=1/4 step, 3=1/8 step
	};
	
	MovementContstraintAxis axes[] = new MovementContstraintAxis[Move.AXES];
	public double junctionDeviation;
	int tickHZ;
	
	public MovementConstraints() {
		tickHZ = 50000; // TODO: Read this value from the hardware at setup (1s / sys.irq.interval)

		// TODO: Read this configuration from a config file in stead.
		
		junctionDeviation = 0.1;
		
		for (int i=0;i<Move.AXES;i++) {
			axes[i] = new MovementContstraintAxis();
			axes[i].acceleration = i == 1 ? 2000 : 1000;
			axes[i].maxSpeed     = i == 1 ? 200 : 200;
			axes[i].minSpeed = 150;
			axes[i].microSteppingMode = 3;
		}
		
		axes[0].mmPerStep = 60.0/(200*8);
		axes[0].coilCurrent = 350/2; 

		axes[1].mmPerStep = 60.0/(200*8);
		axes[1].coilCurrent = 1870; 

		// Z-lift
		axes[2].coilCurrent = 350/2; //350*4; // 4 motors for Z-lift 
		axes[2].mmPerStep = 1.25/200; // M8x1.25 @full stepping
		axes[2].microSteppingMode = 0; // Full steps
		axes[2].maxSpeed = 100; // 16000 steps/second

		axes[3].mmPerStep = 60.0/(200*8);
		axes[3].coilCurrent = 350; 

	}
}
