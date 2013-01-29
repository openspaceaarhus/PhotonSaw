package dk.osaa.psaw.machine;

import java.math.BigInteger;

public class ReplyValue {
	String[] parts;
	String raw;
	public ReplyValue(String str) {
		raw = str;
		parts = str.split("\\s+", 3);
	}
	
	public String getName() {
		return parts[0];
	}
	
	public String getUnit() {
		return parts.length > 2 ? parts[2] : "";
	}
	
	public boolean isOk() {
		return parts[1].toLowerCase().startsWith("ok");
	}

	public String getString() {
		return parts[1];
	}
	
	public long getHex() {
		return new BigInteger(parts[1], 16).longValue();
	}

	public double getDouble() {
		return Double.parseDouble(parts[1]);
	}
	
	public long getInteger() {
		return new BigInteger(parts[1], 10).longValue();
	}
	
	public String toString() {
		return raw;
	}

	public boolean getBoolean() {
		return parts[1].equals("Yes");
	}
}
