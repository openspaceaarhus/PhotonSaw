package dk.osaa.psaw.config;

import lombok.Data;

@Data
public class JettyConfig {
	int port;
	
	JettyConfig() {
		port = 8080;
		
	};	
}
