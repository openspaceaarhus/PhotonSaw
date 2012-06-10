package dk.osaa.psaw.config;

import java.io.File;

import lombok.Data;

@Data
public class HostConfig {
	String serialPort;
	File recordDir;
	boolean simulating;
	boolean recording;
	
	HostConfig() {
		serialPort = "/dev/ttyACM0";
		recordDir = null;
		simulating = false;
		recording = false;
	}
}
