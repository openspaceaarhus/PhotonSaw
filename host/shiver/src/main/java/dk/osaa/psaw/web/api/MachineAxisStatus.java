package dk.osaa.psaw.web.api;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MachineAxisStatus {
	public MachineAxisStatus(Long pos, Double speed, Double accel) {
		this.position = pos;
		this.speed = speed;
		this.accel = accel;
	}

	@JsonProperty
	Long position;
	
	@JsonProperty
	Double speed;

	@JsonProperty
	Double accel;	
}
