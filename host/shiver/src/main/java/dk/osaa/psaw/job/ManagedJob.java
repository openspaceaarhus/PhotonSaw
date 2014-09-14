package dk.osaa.psaw.job;

import java.io.File;

import lombok.Data;

/**
 * This is the extra data we need to keep for each of the jobs we keep in memory.
 *  
 * @author Flemming Frandsen <dren.dk@gmail.com> <http://dren.dk>
 */
@Data
public class ManagedJob {
	
	/**
	 * The actual job
	 */
	Job job;
	
	/**
	 * The file this job was loaded from
	 */
	File file;
	
	/**
	 * The last time this job was accessed.
	 */
	long lastUse;
	
	/**
	 * The ID of the job.
	 */
	String id;
	
	/**
	 * Is this job being used, so it cannot be tossed out of memory.
	 */
	boolean inUse;
}
