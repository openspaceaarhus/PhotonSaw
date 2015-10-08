package dk.osaa.psaw.config.obsolete;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

import lombok.Getter;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.PrettyPrintWriter;
import com.thoughtworks.xstream.io.xml.StaxDriver;

public class LegacyConfiguration {
	
	public MovementConstraints movementConstraints;
	public HostConfig hostConfig;
	public MachineConfig machineConfig;

	@Getter
	private File configFile;
	
	public LegacyConfiguration() {
	} 

	static XStream xstreamInstance = null;
	private static synchronized XStream getXStream() {
		if (xstreamInstance == null) {
			xstreamInstance = new XStream(new StaxDriver());
			xstreamInstance.setMode(XStream.NO_REFERENCES);
			xstreamInstance.omitField(LegacyConfiguration.class, "configFile");
			xstreamInstance.aliasType("movementconstraint", MovementContstraintAxis.class);
			xstreamInstance.aliasType("photonsaw-config", LegacyConfiguration.class);
			xstreamInstance.useAttributeFor(MovementContstraintAxis.class, "axis");
		}
		return xstreamInstance;
	}
	
	/**
	 * Loads a previously stored config file.
	 * @param configFile The configuration file to load
	 * @return The loaded configuration file.
	 * @throws FileNotFoundException
	 */
	static public LegacyConfiguration load(File configFile) throws FileNotFoundException {
		LegacyConfiguration cfg = (LegacyConfiguration)getXStream().fromXML(new FileInputStream(configFile));
		cfg.configFile = configFile;
		cfg.movementConstraints.fixAfterLoad();
		if (cfg.machineConfig == null) {
			cfg.machineConfig = new MachineConfig();
		}
		return cfg;
	}
	
	/**
	 * Store the configuration in a new file.
	 * @param configFile The file to store to
	 * @throws IOException if anything goes wrong with the file.
	 */
	public void store(File configFile) throws IOException {
		this.configFile = configFile;
		getXStream().marshal(this, new PrettyPrintWriter(new FileWriter(configFile)));
	}

	/**
	 * Stores the configuration to the file it was originally loaded from
	 * @throws IOException 
	 */
	public void store() throws IOException {
		store(configFile);
	}
}
