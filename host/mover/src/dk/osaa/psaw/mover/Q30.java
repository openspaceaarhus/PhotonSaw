package dk.osaa.psaw.mover;

import lombok.Data;

/**
 * A Q2.30 integer
 * 
 * @author ff
 */
@Data
public class Q30 {
	static final int ONE = 1<<30;
	long value;
	
	public Q30(double floating) {
		value = 0xffffffffL & Math.round(floating * ONE);
	} 
	
	public double toDouble() {
		return ((double)value)/ONE;
	}
	
	public String toString() {
		return Long.toHexString(value).toLowerCase();
	}	
}
