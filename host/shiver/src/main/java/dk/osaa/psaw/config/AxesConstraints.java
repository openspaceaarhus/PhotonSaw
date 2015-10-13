package dk.osaa.psaw.config;

import lombok.Data;

import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonProperty;

@Data
public class AxesConstraints {
	@JsonProperty
	@NotEmpty
	private AxisConstraints x = new AxisConstraints();
	
	@JsonProperty
	@NotEmpty
	private AxisConstraints y = new AxisConstraints();
	
	@JsonProperty
	@NotEmpty
	private AxisConstraints z = new AxisConstraints();
	
	@JsonProperty
	@NotEmpty
	private AxisConstraints a = new AxisConstraints();
		
	private final AxisConstraints[] array=new AxisConstraints[] {x,y,z,a};
}
