package dk.osaa.psaw.job;

import java.util.ArrayList;


import lombok.Getter;
import lombok.Setter;

public class JobNodeGroup implements JobNode {
	
	@Getter	@Setter
	String name;
	
	@Getter
	String id;

	@Getter @Setter
	PointTransformation nodeTransformation;

	@Getter @Setter
	JobNodeGroup parent;
	
	ArrayList<JobNode> children = new ArrayList<JobNode>();
	
	public JobNodeGroup(String id) {
		this.name = id;
		this.id = id;
	}

	public void render(JobRenderTarget target,
			PointTransformation transformation) {

		PointTransformation xform = transformation.add(nodeTransformation); 
		for (JobNode child : children) {
			child.render(target, xform);
		}		
	}

	public void addChild(JobNode newChild) {
		newChild.setParent(this);
		children.add(newChild);
	}

	@Override
	public JobNode getChildById(String id) {
		for (JobNode ch : children) {
			if (ch.getId().equals(id)) {
				return ch;
			}
		}
		return null; // Not found.
	}
	
	@Override
	public JobNodeID getNodeID() {		
		return getParent() != null 
					? new JobNodeID(getParent().getNodeID(), this.getId())
					: new JobNodeID(this.getId());
	}
}
