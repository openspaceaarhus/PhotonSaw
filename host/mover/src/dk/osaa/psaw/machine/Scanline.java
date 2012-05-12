package dk.osaa.psaw.machine;

import lombok.Data;

/**
 * Wrapper class for a single scanline of 1 bit pixels, ready to be sent to the hardware
 * 
 * @author Flemming Frandsen <dren.dk@gmail.com> <http://dren.dk>
 */
@Data
public class Scanline {
	Q30 pixelSpeed;
	long[] pixelWords;

	private Scanline(Q30 pixelSpeed, long[] pixelWords) {
		this.pixelWords = pixelWords;
		this.pixelSpeed = pixelSpeed;
	}	
}
