package dk.osaa.psaw.web.config;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;

import dk.osaa.psaw.config.PhotonSawMachineConfig;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import io.dropwizard.Configuration;

public class PhotonSawConfiguration extends Configuration {
	
	@JsonProperty()
	@Getter
	@Setter
	@NotNull
	private PhotonSawMachineConfig machine;
}
