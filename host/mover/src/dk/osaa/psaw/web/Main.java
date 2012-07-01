package dk.osaa.psaw.web;

import java.io.File;
import java.util.logging.Level;

import lombok.extern.java.Log;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;

import dk.osaa.psaw.config.Configuration;
import dk.osaa.psaw.core.PhotonSaw;
import dk.osaa.psaw.core.PhotonSawAPI;

@Log
public class Main {
	
	public static void main(String args[]) {
		if (args.length != 1) {
			System.err.println("Need config file argument, if the file doesn't exist it will be created");
			System.exit(1);
		}
				
	    try {
	    	File cfgFile = new File(args[0]);
	    	Configuration cfg;
	    	if (cfgFile.exists()) {
	    		cfg = Configuration.load(cfgFile);
		    	cfg.store();
	    	} else {
	    		cfg = new Configuration();	
	    		cfg.store(cfgFile);
	    	}
	    	
	    	PhotonSawAPI api;
	    	if (cfg.hostConfig.isSimulating()) {
	    		api = new SimulatedPhotonSaw(cfg);	    		
	    	} else {
	    		api = new PhotonSaw(cfg);
	    	}    	
	    	
	    	Server server = new Server(cfg.jettyConfig.getPort());
	    
	        ResourceHandler resource_handler = new ResourceHandler();
	        resource_handler.setDirectoriesListed(true);
	        resource_handler.setWelcomeFiles(new String[]{ "index.html" });
	        File root = new File(cfg.getConfigFile().getParentFile(), "static").getAbsoluteFile();
	        System.err.println("Serving files out of "+root);
	        resource_handler.setResourceBase(root.getAbsolutePath());
	        
	        HandlerList handlers = new HandlerList();
	        handlers.setHandlers(new Handler[] {
	        			resource_handler,
	        			new StatusHandler(api),
	        			new JobHandler(api),
	        			new DefaultHandler()
	        			});
	        server.setHandler(handlers);
	    	
			server.start();
	        server.join();
	        cfg.store();
	        System.exit(0);
	        
		} catch (Exception e) {
			log.log(Level.SEVERE, "Got exception, giving up", e);
			System.exit(2);
		}
	}
}
