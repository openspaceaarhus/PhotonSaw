package dk.osaa.psaw.core;

import dk.osaa.psaw.job.JobManager;

/**
 * This is the interface that's used by the UI to operate the system.
 * 
 * @author Flemming Frandsen <dren.dk@gmail.com> <http://dren.dk>
 */
public interface PhotonSawAPI {
	
	/**
	 * @return The current status of the system
	 */
	PhotonSawStatus getStatus();

	/**
	 * @return The JobManager in charge of managing Jobs on disk and in RAM.
	 */
	JobManager getJobManager();
	
	// TODO: Add Job control (start job)
	// boolean startJob(String id);
	
	// TODO: Add controller to directly jog the axes, command zeroing and stop/start the system.
	// JogController getJogController();
}
