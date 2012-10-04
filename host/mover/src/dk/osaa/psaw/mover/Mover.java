package dk.osaa.psaw.mover;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.logging.Level;

import dk.osaa.psaw.config.Configuration;
import dk.osaa.psaw.core.PhotonSaw;
import dk.osaa.psaw.job.Job;
import dk.osaa.psaw.machine.Move;

import lombok.extern.java.Log;

/**
 * Test class to get the API to do something.
 * @author ff
 */
@Log
public class Mover {
	public static void main(String[] args) {
		PhotonSaw ps = null;
		try {
	    	File cfgFile = new File("test.psconfig");
	    	Configuration cfg;
	    	if (cfgFile.exists()) {
	    		cfg = Configuration.load(cfgFile);
	    	} else {
	    		cfg = new Configuration();	
	    		cfg.store(cfgFile);
	    	}
	    	cfg.store();


			Job testJob = new Job();
			//testJob.loadTest();
			
//			File svgFile = new File("/home/ff/projects/osaa/PhotonSaw/host/testdata/up-engraving.svg");
//			File svgFile = new File("/home/ff/projects/osaa/PhotonSaw/host/testdata/text-and-shapes-as-paths-stroke.svg");
//			File svgFile = new File("/home/ff/projects/osaa/PhotonSaw/host/testdata/simple-stroke.svg");
			
//			File svgFile = new File("/home/ff/projects/osaa/PhotonSaw/host/testdata/casing.svg");
//			File svgFile = new File("/home/ff/projects/osaa/PhotonSaw/host/testdata/au109021.svg");
			File svgFile = new File("/home/ff/projects/osaa/PhotonSaw/host/testdata/laser-focus-guide.svg");
//			File svgFile = new File("/home/ff/projects/osaa/PhotonSaw/host/testdata/butterfly-2.svg");
//			File svgFile = new File("/home/ff/projects/osaa/PhotonSaw/host/testdata/OSAA-10x10.svg");
//			File svgFile = new File("/home/ff/projects/osaa/PhotonSaw/host/testdata/testpiece.svg");
//			File svgFile = new File("/home/ff/projects/osaa/PhotonSaw/host/testdata/x-end.plate.svg");
//			File svgFile = new File("/home/ff/projects/osaa/PhotonSaw/host/testdata/circle-and-rect.svg");
			testJob.loadSVG(cfg, svgFile.getName(), new BufferedInputStream(new FileInputStream(svgFile)));
			testJob.logStructure();
			
			testJob.storeJob(new FileOutputStream("/tmp/"+svgFile.getName()+".psjob"));
				
			
			ps = new PhotonSaw(cfg);
			
			ps.getPlanner().startJob(testJob);			

			// Wait for the job to finish
			while (ps.getPlanner().getCurrentJob() != null) {
				Thread.sleep(1000);
			}

			// Turn off the motors
			for (int i=0;i<Move.AXES;i++) {
				ps.run("me "+i+" 0 0");
			}
			Move.dumpProfile();

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
