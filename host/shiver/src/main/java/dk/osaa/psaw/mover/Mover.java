package dk.osaa.psaw.mover;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.logging.Level;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import dk.osaa.psaw.config.LegacyConfiguration;
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
			File svgFile = args.length==1 ? new File(args[0]) : null;
			
	    	File cfgFile = new File("test.psconfig");
//	    	File cfgFile = new File("simulate.psconfig");
	    	LegacyConfiguration cfg;
	    	if (cfgFile.exists()) {
	    		cfg = LegacyConfiguration.load(cfgFile);
	    	} else {
	    		cfg = new LegacyConfiguration();	
	    		cfg.store(cfgFile);
	    	}
	    	cfg.store();

			ps = new PhotonSaw(cfg);
	    	
			//testJob.loadTest();
			
			if (svgFile == null) {
				JFileChooser fc = new JFileChooser(System.getProperty("user.dir"));
				FileNameExtensionFilter fcf = new FileNameExtensionFilter("SVG Files", "svg");
				fc.setFileFilter(fcf);
				fc.setMultiSelectionEnabled(false);
				int rv = fc.showOpenDialog(null);
				if(rv != JFileChooser.APPROVE_OPTION) {
					System.exit(0);
				}
	
				svgFile = fc.getSelectedFile();
			}
			
			
			Job testJob = new Job();
			testJob.loadSVG(cfg, svgFile.getName(), new BufferedInputStream(new FileInputStream(svgFile)));
			testJob.optimizeCuts();
			testJob.logStructure();
		
			testJob.storeJob(new FileOutputStream("/tmp/"+svgFile.getName()+".psjob"));
			
			SVGRenderTarget rt = new SVGRenderTarget(new File("/tmp/"+svgFile.getName()+".svg"));
			testJob.render(rt);
			rt.done();
		
			ps.run("aa on");
			ps.run("ex on");
			//ps.startJob(id);
			ps.getPlanner().startJob(testJob);			

			// Wait for the job to finish
			while (ps.getPlanner().getCurrentJob() != null) {
				Thread.sleep(1000);
				ps.run("st");
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
