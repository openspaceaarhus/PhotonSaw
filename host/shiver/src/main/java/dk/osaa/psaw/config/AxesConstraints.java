package dk.osaa.psaw.config;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import com.fasterxml.jackson.annotation.JsonProperty;

@Getter
@Setter
public class AxesConstraints {
	@JsonProperty
	@NotNull
	@Valid
	private AxisConstraints x;
	
	@JsonProperty
	@NotNull
	@Valid
	private AxisConstraints y;
	
	@JsonProperty
	@NotNull
	@Valid
	private AxisConstraints z;
	
	@JsonProperty
	@NotNull
	@Valid
	private AxisConstraints a;
		
	private AxisConstraints[] asArray = null;
	public AxisConstraints[] getArray() {
		if (asArray == null) {
			asArray = new AxisConstraints[] {x,y,z,a};
		}
		return asArray;
	}
}
