package dk.osaa.psaw.config;

import java.io.File;

import lombok.Data;

@Data
public class HostConfig {
	String serialPort;
	File recordDir;
	boolean simulating;
	boolean recording;
	File jobsDir;
	int jobsInMemory;
		
	HostConfig() {
		serialPort = "/dev/ttyACM0";
		recordDir = null;
		jobsDir = null;
		jobsInMemory = 10;
		simulating = false;
		recording = false;
	}
}
