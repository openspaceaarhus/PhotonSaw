package dk.osaa.psaw.config;

import lombok.Data;

@Data
public class AxisConstraints {
	private double acceleration; // mm/s/s
	private double maxSpeed;     // mm/s
	private double minSpeed;     // mm/s
	private double maxJerk;      // mm/s 
	private double mmPerStep;    // mm / step
	private int coilCurrent;     // mA
	private int microSteppingMode; // 0=fullstep, 1=halfstep, 2=1/4 step, 3=1/8 step			
}
