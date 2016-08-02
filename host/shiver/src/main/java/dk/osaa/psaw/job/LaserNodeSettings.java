package dk.osaa.psaw.job;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Value;

/**
 * This class wraps the parameters needed when firing the laser
 * @author Flemming Frandsen <dren.dk@gmail.com> <http://dren.dk>
 */
@Value
public class LaserNodeSettings {
	private double intensity;
	private double maxSpeed;
	private int passes;
	private boolean assistAir;
	private int pulsesPermm;
	private double pulseDuration;
	private double rasterLinePitch;
	private double rasterSpeed; // In mm/s
	private boolean scalePowerWithSpeed;
	private RasterOptimization rasterOptimization;

	public boolean equalsRaster(LaserNodeSettings other) {
		return other.assistAir == assistAir
				&& other.rasterLinePitch == rasterLinePitch
				&& other.rasterSpeed==rasterSpeed
				&& other.rasterOptimization == rasterOptimization;
	}
}
