package dk.osaa.psaw.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import lombok.Getter;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.StaxDriver;

public class Configuration {
	public MovementConstraints movementConstraints;
	public JettyConfig jettyConfig;
	public HostConfig hostConfig;
	
	@Getter
	File configFile;
		
	public Configuration() {
		movementConstraints = new MovementConstraints();
		jettyConfig = new JettyConfig();
		hostConfig = new HostConfig();
	} 

	static XStream xstreamInstance = null;
	private static synchronized XStream getXStream() {
		if (xstreamInstance == null) {
			xstreamInstance = new XStream(new StaxDriver());
			xstreamInstance.setMode(XStream.NO_REFERENCES);
			xstreamInstance.omitField(Configuration.class, "configFile");
			xstreamInstance.aliasType("movementconstraint", MovementConstraints.MovementContstraintAxis.class);
		}
		return xstreamInstance;
	}
	
	/**
	 * Loads a previously stored config file.
	 * @param configFile The configuration file to load
	 * @return The loaded configuration file.
	 * @throws FileNotFoundException
	 */
	static public Configuration load(File configFile) throws FileNotFoundException {
		Configuration cfg = (Configuration)getXStream().fromXML(new FileInputStream(configFile));
		cfg.configFile = configFile;
		return cfg;
	}
	
	/**
	 * Store the configuration in a new file.
	 * @param configFile The file to store to
	 * @throws FileNotFoundException
	 */
	public void store(File configFile) throws FileNotFoundException {
		this.configFile = configFile;
		getXStream().toXML(this, new FileOutputStream(configFile));
	}

	/**
	 * Stores the configuration to the file it was originally loaded from
	 * @throws FileNotFoundException
	 */
	public void store() throws FileNotFoundException {
		store(configFile);
	}
}
