package dk.osaa.psaw.mover;

import java.io.IOException;
import java.util.logging.Level;

import dk.osaa.psaw.core.PhotonSaw;
import dk.osaa.psaw.job.Job;
import dk.osaa.psaw.machine.Move;
import dk.osaa.psaw.machine.PhotonSawCommandFailed;
import dk.osaa.psaw.machine.ReplyTimeout;

import lombok.val;
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
			ps = new PhotonSaw();

			Job testJob = new Job();
			testJob.loadTest();
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
