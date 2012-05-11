package dk.osaa.psaw.job;

import lombok.Getter;
import lombok.Setter;

public abstract class AbstractJobNode implements JobNode {
	@Getter
	String id;
		
	@Getter @Setter
	String name;
		
	@Getter @Setter
	JobNodeGroup parent;

	AbstractJobNode(String id) {
		this.id=id;
		this.name=id;
	}	
	
	@Override
	public JobNodeID getNodeID() {		
		return getParent() != null 
					? new JobNodeID(getParent().getNodeID(), this.getId())
					: new JobNodeID(this.getId());
	}
}
