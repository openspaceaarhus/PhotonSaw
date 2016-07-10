package dk.osaa.psaw.job;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * This class wraps the parameters needed when firing the laser
 * @author Flemming Frandsen <dren.dk@gmail.com> <http://dren.dk>
 */
@AllArgsConstructor
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
	
	@Getter
	private boolean scalePowerWithSpeed;

	@Getter
	private RasterOptimization rasterOptimization;
		
	public boolean equalsRaster(LaserNodeSettings other) {
		return other.assistAir == assistAir 
				&& other.intensity == intensity 
				&& other.maxSpeed == maxSpeed
				&& other.rasterLinePitch == rasterLinePitch
				&& other.passes == passes 
				&& other.rasterSpeed==rasterSpeed
				&& other.pulseDuration == pulseDuration
				&& other.scalePowerWithSpeed == scalePowerWithSpeed
				&& other.rasterOptimization == rasterOptimization
				&& other.pulsesPermm == pulsesPermm;
	}
}
