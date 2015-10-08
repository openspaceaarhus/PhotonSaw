package dk.osaa.psaw.config.obsolete;

import lombok.Data;

@Data
public class MachineConfig {
	MachineConfig() {
		assistAirDelay = 250;
		maximumLaserPower = 80;
	}
	long assistAirDelay;
	double maximumLaserPower;
}
