package dk.osaa.psaw.config;

import javax.validation.Valid;

import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonProperty;

import dk.osaa.psaw.machine.Move;

public class AxesConstraints {
	@JsonProperty
	@Valid
	@NotEmpty
	AxisConstraints x;
	
	@JsonProperty
	@Valid
	@NotEmpty
	AxisConstraints y;
	
	@JsonProperty
	@Valid
	@NotEmpty
	AxisConstraints z;
	
	@JsonProperty
	@Valid
	@NotEmpty
	AxisConstraints a;
	
	
	private AxisConstraints[] asArray;
	public AxisConstraints[] getArray() {
		if (asArray == null) {
			asArray = new AxisConstraints[] {x,y,z,a};
			assert(asArray.length == Move.AXES);
		}
		return asArray;
	}
}
