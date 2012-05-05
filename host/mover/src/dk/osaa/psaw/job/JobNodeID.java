package dk.osaa.psaw.job;

import lombok.Getter;

/**
 * An ID of a job node, which can be used to find the way back to the job node as well as find the ancestor nodes
 * 
 * @author ff
 */
public class JobNodeID {
	@Getter
	String id;
	
	public JobNodeID(String id) {
		this.id = id;
	}
	
	public JobNodeID(JobNodeID parent, String child) {
		id = parent.getId()+"."+child;
	}
	
	public JobNodeID getParent() {
		return new JobNodeID(id.replaceAll("\\.[^\\.]+$", "")); 
	}
	
	public String[] getPath() {
		return id.split("\\.");
	}
}
