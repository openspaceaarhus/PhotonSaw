package dk.osaa.psaw.web;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.logging.Level;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import lombok.extern.java.Log;
import net.sourceforge.argparse4j.impl.Arguments;
import net.sourceforge.argparse4j.inf.Namespace;
import net.sourceforge.argparse4j.inf.Subparser;
import io.dropwizard.cli.ConfiguredCommand;
import io.dropwizard.setup.Bootstrap;
import dk.osaa.psaw.config.obsolete.LegacyConfiguration;
import dk.osaa.psaw.core.PhotonSaw;
import dk.osaa.psaw.job.Job;
import dk.osaa.psaw.job.SVGRenderTarget;
import dk.osaa.psaw.machine.Move;
import dk.osaa.psaw.web.config.PhotonSawConfiguration;

@Log
public class SvgCommand extends ConfiguredCommand<PhotonSawConfiguration> {

	public SvgCommand(String name, String desc) {
		super(name, desc);
	}
	
	@Override
	public void configure(net.sourceforge.argparse4j.inf.Subparser subparser) {
		super.configure(subparser);
		
		subparser.addArgument("--svg")
				 .help("The SVG file to execute, if omitted, you will be prompted for the svg file")
				 .type(Arguments.fileType().verifyCanRead())
				 .nargs("?");		
	}
	
	@Override
	protected void run(Bootstrap<PhotonSawConfiguration> bootstrap,
			Namespace namespace, PhotonSawConfiguration configuration)
			throws Exception {
	
		File svgFile = namespace.get("svg");
			    	
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
		
    	PhotonSaw ps = new PhotonSaw(configuration.getMachine());
		
		try {			
			Job testJob = new Job();
			testJob.loadSVG(configuration.getMachine(), svgFile.getName(), new BufferedInputStream(new FileInputStream(svgFile)));
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
			// Turn off the motors
			for (int i=0;i<Move.AXES;i++) {					
				try {
					ps.run("me "+i+" 0 0");
				} catch (Exception e) {
				}
			}
			Move.dumpProfile();
		}
	
		System.exit(0);
    	
	}

}
