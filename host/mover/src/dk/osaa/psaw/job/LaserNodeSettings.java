package dk.osaa.psaw.job;

import lombok.Getter;

public class LaserNodeSettings {

	@Getter
	boolean assistAir;

	@Getter
	double maxSpeed;
	
	@Getter
	double intensity;
	
	@Getter
	int passes;
	
	@Getter
	int ppmm; // pulses per mm
	
	@Getter
	int pulseDuration; // In micro seconds (us)

	public LaserNodeSettings(double intensity, double maxSpeed, int passes, boolean assistAir, int ppmm, int pulseDuration) {
		this.assistAir = assistAir;
		this.maxSpeed = maxSpeed;
		this.intensity = intensity;
		this.passes = passes;
		this.ppmm = ppmm;
		this.pulseDuration = pulseDuration;
	}
		
}
