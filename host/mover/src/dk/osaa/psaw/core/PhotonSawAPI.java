package dk.osaa.psaw.core;

import dk.osaa.psaw.job.JobManager;
import dk.osaa.psaw.machine.Move;
import dk.osaa.psaw.machine.MoveVector;

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
	
	/**
	 * Sets a jog speed in all directions, the machine stops 250 ms after this call.
	 * 
	 * @param direction The speed in each direction
	 */
	void setJogSpeed(MoveVector direction);

	/**
	 * Used by the planner code to inject a move into the move buffer
	 * 
	 * @param move
	 * @throws InterruptedException
	 */
	public void putMove(Move move) throws InterruptedException;
	
	/**
	 * Optionally appends a Move code into the move buffer
	 */
	public void putAssistAir(boolean assistAir) throws InterruptedException;
}
