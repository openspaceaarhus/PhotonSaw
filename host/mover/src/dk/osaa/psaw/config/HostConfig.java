package dk.osaa.psaw.config;

import lombok.Data;

@Data
public class HostConfig {
	String serialPort;
	
	HostConfig() {
		serialPort = "/dev/ttyACM0";
	}
}
