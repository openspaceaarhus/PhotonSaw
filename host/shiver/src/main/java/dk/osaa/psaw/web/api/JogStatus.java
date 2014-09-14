package dk.osaa.psaw.web.api;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Result being returned from the jogger
 * 
 * @author Flemming Frandsen <dren.dk@gmail.com> <http://dren.dk>
 */
@Getter
@AllArgsConstructor
public class JogStatus {
	@JsonProperty
	String hest;
}
