package dk.osaa.psaw.web;

import io.dropwizard.Application;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.federecio.dropwizard.swagger.SwaggerDropwizard;

import java.io.File;
import java.util.logging.Level;

import lombok.extern.java.Log;
import dk.osaa.psaw.config.LegacyConfiguration;
import dk.osaa.psaw.core.PhotonSaw;
import dk.osaa.psaw.web.config.PhotonSawConfiguration;
import dk.osaa.psaw.web.resources.ImmediateJob;
import dk.osaa.psaw.web.resources.Jogger;
import dk.osaa.psaw.web.resources.Status;

@Log
public class PhotonSawUI extends Application<PhotonSawConfiguration> {	
	private final SwaggerDropwizard swaggerDropwizard = new SwaggerDropwizard();
	
	@Override
	public String getName() {
		return "PhotonSaw User Interface";
	}
	
	public static void main(String[] args) {
		try {
			new PhotonSawUI().run(args);

		} catch (Exception e) {
			System.err.println("Failed while bootstrapping"+ e);
			e.printStackTrace(System.err);
			System.exit(1);	
		}
	}

	@Override
	public void initialize(Bootstrap<PhotonSawConfiguration> bootstrap) {
		bootstrap.addBundle(new AssetsBundle("/static/", "/static/", "index.html", "static"));		
		swaggerDropwizard.onInitialize(bootstrap);
	}

	@Override
	public void run(PhotonSawConfiguration configuration, Environment environment) throws Exception {
		
		File legacyConfigFile = configuration.getLegacyConfigFile();
    	LegacyConfiguration cfg = LegacyConfiguration.load(legacyConfigFile);
    	PhotonSaw psaw = new PhotonSaw(cfg);
		environment.lifecycle().manage(new ManagedPhotonSaw(psaw));

		// All the live API resources live under /api
		//environment.jersey().setUrlPattern("/api/*");
		swaggerDropwizard.onRun(configuration, environment);

		// Register the resources:		
		environment.jersey().register(new Jogger(psaw));
		environment.jersey().register(new Status(psaw));
		environment.jersey().register(new ImmediateJob(psaw));
	}
}
