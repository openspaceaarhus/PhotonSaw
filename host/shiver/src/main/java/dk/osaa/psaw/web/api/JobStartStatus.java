package dk.osaa.psaw.web.api;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class JobStartStatus {

	@JsonProperty
	private final String name;
}
