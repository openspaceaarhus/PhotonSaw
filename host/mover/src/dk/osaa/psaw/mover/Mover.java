package dk.osaa.psaw.mover;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.logging.Level;

import dk.osaa.psaw.config.Configuration;
import dk.osaa.psaw.core.PhotonSaw;
import dk.osaa.psaw.job.Job;
import dk.osaa.psaw.job.SVGRenderTarget;
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
//	    	File cfgFile = new File("simulate.psconfig");
	    	Configuration cfg;
	    	if (cfgFile.exists()) {
	    		cfg = Configuration.load(cfgFile);
	    	} else {
	    		cfg = new Configuration();	
	    		cfg.store(cfgFile);
	    	}
	    	cfg.store();

			ps = new PhotonSaw(cfg);
	    	
			//testJob.loadTest();
			
//			File svgFile = new File("/home/ff/projects/osaa/PhotonSaw/host/testdata/rotated-image.svg");
//			File svgFile = new File("/home/ff/projects/osaa/PhotonSaw/host/testdata/N64_case.svg");
//			File svgFile = new File("/home/ff/projects/osaa/PhotonSaw/host/testdata/casing4-vector.svg");
//			File svgFile = new File("/home/ff/projects/osaa/PhotonSaw/host/testdata/up-engraving.svg");
//			File svgFile = new File("/home/ff/projects/osaa/PhotonSaw/host/testdata/text-and-shapes-as-paths-stroke.svg");
//			File svgFile = new File("/home/ff/projects/osaa/PhotonSaw/host/testdata/simple-stroke.svg");
			
//			File svgFile = new File("/home/ff/projects/osaa/PhotonSaw/host/testdata/casing.svg");
//			File svgFile = new File("/home/ff/projects/osaa/PhotonSaw/host/testdata/au109021.svg");
//			File svgFile = new File("/home/ff/projects/osaa/PhotonSaw/host/testdata/laser-focus-guide.svg");
//			File svgFile = new File("/home/ff/projects/osaa/PhotonSaw/host/testdata/butterfly-3.svg");
//			File svgFile = new File("/home/ff/projects/osaa/PhotonSaw/host/testdata/zoid-color.svg");
//			File svgFile = new File("/home/ff/projects/osaa/PhotonSaw/host/testdata/osaa-10x10.svg"); // Zoid cut
//			File svgFile = new File("/home/ff/projects/osaa/PhotonSaw/host/testdata/OSAA-10x10-laser.svg");
//			File svgFile = new File("/home/ff/projects/osaa/PhotonSaw/host/testdata/lens-ring.svg");
//			File svgFile = new File("/home/ff/projects/osaa/PhotonSaw/host/testdata/speed-guide.svg");
//			File svgFile = new File("/home/ff/projects/osaa/PhotonSaw/host/testdata/power-guide.svg");
//			File svgFile = new File("/home/ff/projects/osaa/PhotonSaw/host/testdata/3mm-box.svg");
//			File svgFile = new File("/home/ff/projects/osaa/PhotonSaw/host/testdata/club-mate-holder.svg");
			File svgFile = new File("/home/ff/projects/osaa/PhotonSaw/host/testdata/casing2.svg");
//			File svgFile = new File("/home/ff/projects/osaa/PhotonSaw/host/testdata/casing-raster.svg"); // Broken
//			File svgFile = new File("/home/ff/projects/osaa/PhotonSaw/host/testdata/casing2-helper.svg");
//			File svgFile = new File("/home/ff/projects/osaa/PhotonSaw/host/testdata/wall.svg");
//			File svgFile = new File("/home/ff/projects/osaa/PhotonSaw/host/testdata/url.svg");
//			File svgFile = new File("/home/ff/projects/osaa/PhotonSaw/host/testdata/MAGENTA_LASER.svg");
//			File svgFile = new File("/home/ff/projects/osaa/PhotonSaw/host/testdata/testpiece.svg");
//			File svgFile = new File("/home/ff/projects/osaa/PhotonSaw/host/testdata/x-end.plate.svg");
//			File svgFile = new File("/home/ff/projects/osaa/PhotonSaw/host/testdata/circle-and-rect.svg");

			
			Job testJob = new Job();
			testJob.loadSVG(cfg, svgFile.getName(), new BufferedInputStream(new FileInputStream(svgFile)));
			
			testJob.logStructure();
		
			testJob.storeJob(new FileOutputStream("/tmp/"+svgFile.getName()+".psjob"));
			
			SVGRenderTarget rt = new SVGRenderTarget(new File("/tmp/"+svgFile.getName()+".svg"));
			testJob.render(rt);
			rt.done();
		
			
			//ps.startJob(id);
			ps.getPlanner().startJob(testJob);			

			// Wait for the job to finish
			while (ps.getPlanner().getCurrentJob() != null) {
				Thread.sleep(1000);
			}
/*
			// Turn off the motors
			for (int i=0;i<Move.AXES;i++) {
				ps.run("me "+i+" 0 0");
			}
			Move.dumpProfile();
*/
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
