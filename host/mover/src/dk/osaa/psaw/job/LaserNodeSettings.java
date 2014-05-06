package dk.osaa.psaw.job;

import lombok.Getter;

/**
 * This class wraps the parameters needed when firing the laser
 * @author Flemming Frandsen <dren.dk@gmail.com> <http://dren.dk>
 */
public class LaserNodeSettings {
	@Getter
	private double intensity;

	@Getter
	private double maxSpeed;
	
	@Getter
	private int passes;
	
	@Getter
	private boolean assistAir;
	
	@Getter
	private int pulsesPermm; 
	
	@Getter
	private double pulseDuration; 
	
	@Getter
	private double rasterLinePitch;

	@Getter
	private double rasterSpeed; // In mm/s
	
	public LaserNodeSettings(double intensity, double maxSpeed, int passes, boolean assistAir,
			                 int pulsesPermm, double pulseDuration, double rasterLinePitch, double rasterSpeed) {
		this.assistAir = assistAir;		
		this.intensity=intensity;
		this.maxSpeed=maxSpeed;
		this.rasterLinePitch = rasterLinePitch;
		this.passes=passes;
		this.pulseDuration = pulseDuration;
		this.pulsesPermm = pulsesPermm; 
		this.rasterSpeed = rasterSpeed;
	}
	
	public boolean equalsRaster(LaserNodeSettings other) {
		return other.assistAir == assistAir 
				&& other.intensity == intensity 
				&& other.maxSpeed == maxSpeed
				&& other.rasterLinePitch == rasterLinePitch
				&& other.passes == passes 
				&& other.rasterSpeed==rasterSpeed
				&& other.pulseDuration == pulseDuration
				&& other.pulsesPermm == pulsesPermm;
	}
}
