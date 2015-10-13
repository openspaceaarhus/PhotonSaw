package dk.osaa.psaw.web.config;

import com.fasterxml.jackson.annotation.JsonProperty;

import dk.osaa.psaw.config.PhotonSawMachineConfig;
import lombok.Getter;
import lombok.Setter;
import io.dropwizard.Configuration;

public class PhotonSawConfiguration extends Configuration {
	
	@JsonProperty()
	@Getter
	@Setter
	private PhotonSawMachineConfig machine = new PhotonSawMachineConfig();
}
