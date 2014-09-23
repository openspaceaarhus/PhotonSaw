package dk.osaa.psaw.core;

import lombok.Data;
import dk.osaa.psaw.machine.CommandReply;

/**
 * This is the interface that's used by the front end to get at the current status of the system.
 * 
 * @author Flemming Frandsen <dren.dk@gmail.com> <http://dren.dk>
 */
@Data
public class PhotonSawStatus  {
	
	/**
	 * The time this status was generated, used for timing simulations and to check for freshness in the UI
	 */
	private Long timestamp;
	
	/**
	 * Returns a copy of the last status values returned from the hardware, notice that
	 * some may be updated quite a bit more often than others.
	 * 
	 * The last seen value for each status variable.
	 */
	private CommandReply hardwareStatus;
	
	/**
	 * The maximum number of moves to buffer in the host software
	 */
	private int moveBufferSize;
	
	/**
	 * The number of moves buffered in the host software
	 */
	private int moveBufferInUse;
	
	/**
	 * The number of Lines in the LineBuffer
	 */
	private int lineBufferSize;

	/**
	 * The number of lines the system tries to keep in the buffer
	 */
	private int lineBufferSizeTarget;	
	
	/**
	 * The length in mm of the LineBuffer
	 */
	private double lineBufferLength;
	
	/**
	 * The length the system tries to keep in the buffer
	 */
	private double lineBufferLengthTarget;

	/**
	 * The length of the job in mm 
	 */
	private double jobLength;
	
	/**
	 * The number of lines in the current job
	 */
	private int jobSize;
	
	/**
	 * The line number last buffered from the job 
	 */
	private int jobRenderingProgressLineCount;
}
