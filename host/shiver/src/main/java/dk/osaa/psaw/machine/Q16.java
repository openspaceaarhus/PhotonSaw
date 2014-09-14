package dk.osaa.psaw.machine;

import lombok.Data;

/**
 * A Q16.16 integer which fits in a 32 bit word
 * 
 * @author ff
 */
@Data
public class Q16 {
	static final int ONE = 1<<16;
	long value;
	
	public Q16(double floating) {
		value = 0xffffffffL & Math.round(floating * ONE);
	} 
	
	public double toDouble() {
		return ((double)value)/ONE;
	}
	
	public String toString() {
		return Long.toHexString(value).toLowerCase();
	}	
}
