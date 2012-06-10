package dk.osaa.psaw.core;

import lombok.Data;
import dk.osaa.psaw.machine.CommandReply;

/**
 * This is the interface that's used by the front end to get at the current status of the system.
 * 
 * @author Flemming Frandsen <dren.dk@gmail.com> <http://dren.dk>
 */
@Data
public class PhotonSawStatus implements Comparable {
	
	/**
	 * The time this status was generated, used for timing simulations and to check for freshness in the UI
	 */
	Long timestamp;
	
	/**
	 * Returns a copy of the last status values returned from the hardware, notice that
	 * some may be updated quite a bit more often than others.
	 * 
	 * The last seen value for each status variable.
	 */
	CommandReply hardwareStatus;
	
	/**
	 * The maximum number of moves to buffer in the host software
	 */
	int moveBufferSize;
	
	/**
	 * The number of moves buffered in the host software
	 */
	int moveBufferInUse;
	
	/**
	 * The number of Lines in the LineBuffer
	 */
	int lineBufferSize;

	/**
	 * The number of lines the system tries to keep in the buffer
	 */
	int lineBufferSizeTarget;	
	
	/**
	 * The length in mm of the LineBuffer
	 */
	double lineBufferLength;
	
	/**
	 * The length the system tries to keep in the buffer
	 */
	double lineBufferLengthTarget;

	/**
	 * The length of the job in mm 
	 */
	double jobLength;
	
	/**
	 * The number of lines in the current job
	 */
	int jobSize;
	
	/**
	 * The line number last buffered from the job 
	 */
	int jobRenderingProgressLineCount;

	@Override
	public int compareTo(Object arg0) {
		PhotonSawStatus other = (PhotonSawStatus)arg0;

		if (other.timestamp < timestamp) {
			return 1;
		} else if (other.timestamp > timestamp) {
			return -1;
		} else {
			return 0;			
		}
	}
}
