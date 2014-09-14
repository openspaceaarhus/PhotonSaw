package dk.osaa.psaw.web.api;

import com.fasterxml.jackson.annotation.JsonProperty;

import dk.osaa.psaw.machine.MoveVector;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class JogSpeed {
	@JsonProperty
	Double x;
	@JsonProperty
	Double y;
	@JsonProperty
	Double z;
	@JsonProperty
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
