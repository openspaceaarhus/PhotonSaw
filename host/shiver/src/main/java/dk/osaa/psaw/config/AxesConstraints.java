package dk.osaa.psaw.config;

import javax.validation.constraints.NotNull;

import lombok.Data;

import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonProperty;

@Data
public class AxesConstraints {
	@JsonProperty
	@NotNull
	private AxisConstraints x;
	
	@JsonProperty
	@NotNull
	private AxisConstraints y;
	
	@JsonProperty
	@NotNull
	private AxisConstraints z;
	
	@JsonProperty
	@NotNull
	private AxisConstraints a;
		
	private AxisConstraints[] asArray = null;
	public AxisConstraints[] getArray() {
		if (asArray == null) {
			asArray = new AxisConstraints[] {x,y,z,a};
		}
		return asArray;
	}
}
