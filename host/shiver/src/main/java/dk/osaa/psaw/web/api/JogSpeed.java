package dk.osaa.psaw.web.api;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import com.fasterxml.jackson.annotation.JsonProperty;

import dk.osaa.psaw.machine.MoveVector;
import lombok.Getter;

@Getter
public class JogSpeed {
	@JsonProperty @Max(1) @Min(-1)
	Double x;

	@JsonProperty @Max(1) @Min(-1)
	Double y;

	@JsonProperty @Max(1) @Min(-1)
	Double z;

	@JsonProperty @Max(1) @Min(-1)
	Double a;
	
	public MoveVector toMoveVector() {
		
		MoveVector speed = new MoveVector();
		
		if (x != null) {
			speed.setAxis(0, x);
		}
		
		if (y != null) {
			speed.setAxis(1, y);
		}
		
		if (z != null) {
			speed.setAxis(2, z);
		}
		
		if (a != null) {
			speed.setAxis(3, a);
		}
		
		return speed;
	}	
}
