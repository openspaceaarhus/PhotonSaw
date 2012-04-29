package dk.osaa.psaw.mover;

import lombok.Data;

/**
 * A Q2.30 fixed point number which fits in a 32 bit word
 * 
 * @author ff
 */
@Data
public class Q30 {
	static final int ONE = 1<<30;
	int intValue;
	long value;

	public Q30(double floating) {
		setDouble(floating);
	}
	
	public void setDouble(double floating) {
		double f = Math.ceil(floating * ONE);
		if (Math.abs(f) > ONE) {
			throw new RuntimeException("Overflow, Q30 cannot contain values larger than 1: "+floating);
		}
		intValue = (int)f;
		value = 0xffffffffL & (long)f;		
	}
	
	public double toDouble() {
		return ((double)intValue)/ONE;
	}
	
	public String toString() {
		return Long.toHexString(value).toLowerCase();
	}

	public void addDouble(double d) {
		setDouble(toDouble()+d);
	}	
}
