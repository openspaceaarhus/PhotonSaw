package dk.osaa.psaw.web.config;

import java.io.File;

import javax.validation.Valid;

import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonProperty;

import dk.osaa.psaw.config.PhotonSawMachineConfig;
import lombok.Data;
import io.dropwizard.Configuration;

@Data
public class PhotonSawConfiguration extends Configuration {
	
	@JsonProperty
	@Valid
	PhotonSawMachineConfig machine;
}
