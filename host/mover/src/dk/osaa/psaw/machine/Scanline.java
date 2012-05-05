package dk.osaa.psaw.machine;

import lombok.Data;

/**
 * Wrapper class for a single scanline of 1 bit pixels, ready to be sent to the hardware
 * @author ff
  */
@Data
public class Scanline {
	private Scanline() {
		// TODO: Create constructor which will take an image, a line number and a duration (in ticks)
	}	
	
	Q30 pixelSpeed;
	long pixelWords[];
}
