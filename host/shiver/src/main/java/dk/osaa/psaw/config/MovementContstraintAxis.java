package dk.osaa.psaw.config;

import lombok.Getter;

@Getter
public class MovementContstraintAxis {
	public String axis; // Just a name for the axis
	public double acceleration; // mm/s/s
	public double maxSpeed;     // mm/s
	public double minSpeed;     // mm/s
	public double maxJerk;      // mm/s 
	public double mmPerStep;    // mm / step
	public int coilCurrent;     // mA
	public int microSteppingMode; // 0=fullstep, 1=halfstep, 2=1/4 step, 3=1/8 step		
}