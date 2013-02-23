package dk.osaa.psaw.mover;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.logging.Level;

import lombok.extern.java.Log;

import dk.osaa.psaw.config.Configuration;
import dk.osaa.psaw.core.PhotonSaw;
import dk.osaa.psaw.job.Job;
import dk.osaa.psaw.job.SVGRenderTarget;
import dk.osaa.psaw.machine.Move;
import dk.osaa.psaw.machine.SimulatedCommander;

@Log
public class Simulate {
	
	static final String[] files = {
		"raspberry-pi-box.svg",
		/*
		"fl.svg",
		"iss.svg",
		
		"casing2.svg", "casing2-orig.svg", "casing2-opt.svg", "casing2-opt-group.svg",
	    "up-engraving.svg",
	    "rotated-image.svg",
	    "casing-raster.svg",
	    "zoid-color.svg",
	    */
		};
	static final String ROOT = "/home/ff/projects/osaa/PhotonSaw/host/testdata";
	
	public static void main(String[] args) {
		PhotonSaw ps = null;
		try {
	    	File cfgFile = new File("simulate.psconfig");
	    	Configuration cfg;
	    	if (cfgFile.exists()) {
	    		cfg = Configuration.load(cfgFile);
	    	} else {
	    		cfg = new Configuration();	
	    		cfg.store(cfgFile);
	    	}
	    	cfg.store();

			ps = new PhotonSaw(cfg);

			
			for (String name : files) {
				File svgFile = new File(ROOT+"/"+name);

				Job testJob = new Job();
				testJob.loadSVG(cfg, svgFile.getName(), new BufferedInputStream(new FileInputStream(svgFile)));				
				//testJob.logStructure();			
				testJob.storeJob(new FileOutputStream(ROOT+"/out/"+svgFile.getName()+".psjob"));
				
				
				SVGRenderTarget rt = new SVGRenderTarget(new File(ROOT+"/out/"+svgFile.getName()+".svg"));
				testJob.render(rt);
				rt.done();
				
				SimulatedCommander.setLog(new File(ROOT+"/out/"+svgFile.getName()+".moves"));
			
				ps.getPlanner().startJob(testJob);			
	
				// Wait for the job to finish
				while (ps.getPlanner().getCurrentJob() != null) {
					Thread.sleep(1000);
				}
			}
			SimulatedCommander.setLog(null);
			
		} catch (Exception e) {
			log.log(Level.SEVERE, "Failed while running command", e);
			System.exit(2);

		} finally {
			if (ps != null) {
				// Turn off the motors
				for (int i=0;i<Move.AXES;i++) {					
					try {
						ps.run("me "+i+" 0 0");
					} catch (Exception e) {
					}
				}
				Move.dumpProfile();
			}
		}
	
		System.exit(0);
	}
}
