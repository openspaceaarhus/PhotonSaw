package dk.osaa.psaw.mover;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.logging.Level;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import lombok.extern.java.Log;
import dk.osaa.psaw.config.LegacyConfiguration;
import dk.osaa.psaw.core.PhotonSaw;
import dk.osaa.psaw.job.Job;
import dk.osaa.psaw.job.SVGRenderTarget;
import dk.osaa.psaw.machine.Move;
import dk.osaa.psaw.machine.SimulatedCommander;

@Log
public class Simulate {
	
	public static void main(String[] args) {
		PhotonSaw ps = null;
		try {
	    	File cfgFile = new File("simulate.psconfig");
	    	LegacyConfiguration cfg;
	    	if (cfgFile.exists()) {
	    		cfg = LegacyConfiguration.load(cfgFile);
	    	} else {
	    		cfg = new LegacyConfiguration();	
	    		cfg.store(cfgFile);
	    	}
	    	cfg.store();

			ps = new PhotonSaw(cfg);

			JFileChooser fc = new JFileChooser(System.getProperty("user.dir"));
			FileNameExtensionFilter fcf = new FileNameExtensionFilter("SVG Files", "svg");
			fc.setFileFilter(fcf);
			fc.setMultiSelectionEnabled(true);
			int rv = fc.showOpenDialog(null);
			if(rv != JFileChooser.APPROVE_OPTION) {
				System.exit(0);
			}
			
			for (File svgFile : fc.getSelectedFiles()) {
				File savepath = new File(svgFile.getParent() + "/out");
				if(!savepath.exists()) {
					savepath.mkdir();
				}
				Job testJob = new Job();
				testJob.loadSVG(cfg, svgFile.getName(), new BufferedInputStream(new FileInputStream(svgFile)));				
				//testJob.logStructure();			
				testJob.storeJob(new FileOutputStream(savepath+"/"+svgFile.getName()+".psjob"));
				
				
				SVGRenderTarget rt = new SVGRenderTarget(new File(savepath+"/"+svgFile.getName()+".svg"));
				testJob.render(rt);
				rt.done();
				
				SimulatedCommander.setLog(new File(savepath+"/"+svgFile.getName()+".moves"));
			
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
