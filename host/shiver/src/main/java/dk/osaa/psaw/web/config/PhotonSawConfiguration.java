package dk.osaa.psaw.web.config;

import java.io.File;

import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import io.dropwizard.Configuration;

@Data
public class PhotonSawConfiguration extends Configuration {
	@JsonProperty
	@NotEmpty
	private String test;

	@JsonProperty
	@NotEmpty
	private String legacyConfig;
	
	public File getLegacyConfigFile() {
		File f = new File(legacyConfig);
		if (!f.canRead()) {
			throw new RuntimeException("Error: Missing config file: "+f);
		}
		return f;
	}
}
