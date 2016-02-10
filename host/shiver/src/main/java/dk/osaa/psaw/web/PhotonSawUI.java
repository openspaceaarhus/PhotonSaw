package dk.osaa.psaw.web;

import io.dropwizard.Application;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.bundles.webjars.WebJarBundle;
import io.dropwizard.forms.MultiPartBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.federecio.dropwizard.swagger.SwaggerBundle;
import io.federecio.dropwizard.swagger.SwaggerBundleConfiguration;
import lombok.extern.java.Log;

import dk.osaa.psaw.core.PhotonSaw;
import dk.osaa.psaw.web.config.PhotonSawConfiguration;
import dk.osaa.psaw.web.resources.ImmediateJobResource;
import dk.osaa.psaw.web.resources.JoggerResource;
import dk.osaa.psaw.web.resources.StartPageResource;
import dk.osaa.psaw.web.resources.StatusResource;

@Log
public class PhotonSawUI extends Application<PhotonSawConfiguration> {	
	
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
		bootstrap.addBundle(new WebJarBundle("org.webjars.bower"));
		bootstrap.addCommand(new SvgCommand("svg", "Executes an svg"));
		bootstrap.addBundle(new SwaggerBundle<PhotonSawConfiguration>() {
	        @Override
	        protected SwaggerBundleConfiguration getSwaggerBundleConfiguration(PhotonSawConfiguration configuration) {
	            return configuration.swaggerBundleConfiguration;
	        }
	    });
		bootstrap.addBundle(new MultiPartBundle());		
	}

	@Override
	public void run(PhotonSawConfiguration configuration, Environment environment) throws Exception {
    	PhotonSaw psaw = new PhotonSaw(configuration.getMachine());
		environment.lifecycle().manage(new ManagedPhotonSaw(psaw));

		// All the live API resources live under /api

		// Register the resources:		
		environment.jersey().register(new JoggerResource(psaw));
		environment.jersey().register(new StatusResource(psaw));
		environment.jersey().register(new ImmediateJobResource(psaw));
		environment.jersey().register(new StartPageResource());
	}
}
