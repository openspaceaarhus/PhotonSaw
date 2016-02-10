package dk.osaa.psaw.job;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.TreeMap;

import lombok.val;
import dk.osaa.psaw.config.PhotonSawMachineConfig;

/**
 * Manages a the jobs the system knows about and keeps a cache of jobs in memory.
 *   
 * @author Flemming Frandsen <dren.dk@gmail.com> <http://dren.dk>
 */
public class JobManager {

	/**
	 * Job cache, which contains the jobs that have been loaded from disk or 
	 */
	TreeMap<String, ManagedJob> jobs;	
	PhotonSawMachineConfig cfg;
	public JobManager(PhotonSawMachineConfig cfg) {
		this.cfg = cfg;
		jobs = new TreeMap<String, ManagedJob>();
		
		discoverJobs();
	}
	
	/**
	 * Scans the disk for jobs so we don't need to do that every time someone wants a list of jobs.
	 */
	protected void discoverJobs() {
		File jobStore = cfg.getJobsDir();
		if (jobStore == null) {
			throw new RuntimeException("Missing jobStore option");
		} else if (!jobStore.isDirectory()) {
			throw new RuntimeException("The directory for storing jobs doesn't exist: "+jobStore);
		}
		
		for (String fn : jobStore.list()) {
			File jf = new File(jobStore, fn);
			if (jf.isFile() && fn.matches("\\.psjob$")) {
				String id = fn.replaceAll("\\.psjob$", "");
							
				if (!jobs.containsKey(id)) {
					val mj = new ManagedJob();
					mj.setFile(jf);
					mj.setId(id);
					jobs.put(id, mj);
				}
			}
		}
	}
	
	/**
	 * All the jobs available either on disk or already loaded are returned, only the id and the file fields can be used. 
	 * @return All the known jobs in the system
	 */
	public Collection<ManagedJob> getKnownJobs() {
		return jobs.values();
	}

	protected String makeJobId(String name) {
		String id = name.replaceAll("[^a-zA-Z0-9\\._-]+", "");
	
		// Ensure that the ID of the job is unique, if the user is full of fail
		if (jobs.containsKey(id)) {			
			id += "-"+String.format("%1$tY.%1$tm.%1$td.%1$tH.%1$tM", GregorianCalendar.getInstance());
			String pfx = id;
			int uniq = 0;
			while (jobs.containsKey(id)) {
				uniq++;
				id = pfx + "-" + uniq;
			}
		}
		
		return id;
	}
	
	
	/**
	 * Creates a new job, with a unique ID and returns the ID, use getJobById to get access to the actual Job
	 * 
	 * @param name The name of the new job to create, this is used to create a unique ID for the job.
	 * @return The ID of the newly created job
	 */
	public String createJob(String name) {
		String id = makeJobId(name);
				
		File jf = new File(cfg.getJobsDir(), id+".psjob");		
		val mj = new ManagedJob();
		mj.setFile(jf);
		mj.setId(id);
		mj.setJob(new Job());
		mj.getJob().setName(name);
		
		jobs.put(id, mj);
		return id;
	}
	
	/**
	 * Creates a new job, with a unique ID and returns the ID, use getJobById to get access to the actual Job
	 * 
	 * @param name The name of the new job to create, this is used to create a unique ID for the job.
	 * @return The ID of the newly created job
	 * @throws IOException 
	 * @throws ClassNotFoundException 
	 */
	public String importJob(String name, InputStream jobStream) throws ClassNotFoundException, IOException {
		val job = Job.loadJob(jobStream);
		if (name == null) {
			name = job.getName();
		}
		String id = makeJobId(name);
				
		File jf = new File(cfg.getJobsDir(), id+".psjob");		
		val mj = new ManagedJob();
		mj.setFile(jf);
		mj.setId(id);
		mj.setJob(job);
		mj.getJob().setName(name);
		
		jobs.put(id, mj);
		return id;
	}	
	
	/**
	 * Returns a job, when given the id or null if it doesn't exist.
	 * 
	 * @param key the id of the job to load, currently the job file name ending in .psjob
	 * @return the Job or null if it doesn't exist
	 * @throws IOException If unable to load the job file from disk
	 * @throws FileNotFoundException If unable to load the job file from disk
	 * @throws ClassNotFoundException If unable to load the job file from disk
	 */
	public Job getJobById(String key) throws ClassNotFoundException, FileNotFoundException, IOException {
		
		// If a job is requested we don't know about, scan the disk, it could have appeared by magic.
		if (!jobs.containsKey(key)) {
			discoverJobs();

			if (!jobs.containsKey(key)) {
				return null; // ... but sadly it didn't
			}
		}
		
		val mj = jobs.get(key);
		mj.setLastUse(System.currentTimeMillis());

		// It's already loaded, so just use it.
		if (mj.getJob() != null) {
			return mj.getJob(); 
		}
		
		// The file disappeared, so remove it from our list.
		if (!mj.getFile().isFile()) {
			jobs.remove(key); 
			return null;
		}
		
		// Make room for the job by getting rid of the oldest one we have loaded, if we'd get over the limit
		while (jobs.size() >= cfg.getJobsInMemory()) {
			ManagedJob oldest = null;
			for (ManagedJob j : jobs.values()) {
				// Never nuke jobs that are explicitly marked as in use or jobs that have been used recently
				if (j.isInUse() || System.currentTimeMillis()-j.getLastUse() < 120*1000) {
					continue;
				}
								
				if (oldest == null || j.getLastUse() < oldest.getLastUse()) {
					oldest = j;
				}
			}
			
			if (oldest == null) {
				continue;
			}
			
			jobs.remove(oldest.getId());
		}

		// Finally load the the actual job and return it
		mj.setJob(Job.loadJob(new FileInputStream(mj.getFile())));		
		return mj.getJob();
	}
}
