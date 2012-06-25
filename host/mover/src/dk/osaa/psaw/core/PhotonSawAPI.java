package dk.osaa.psaw.core;

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

	
	
	// TODO: Add Job control (start job)
	
	// TODO: Add controller to directly jog the axes, command zeroing and stop/start the system.
}
