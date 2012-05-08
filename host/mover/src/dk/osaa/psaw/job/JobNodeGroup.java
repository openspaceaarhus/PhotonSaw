package dk.osaa.psaw.job;

import java.util.ArrayList;
import java.util.HashSet;


import lombok.Getter;
import lombok.Setter;

/**
 * A group of JobNodes that make up the document tree that make up a Job. 
 * 
 * @author Flemming Frandsen <dren.dk@gmail.com> <http://dren.dk>
 */
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
	
	@SuppressWarnings("unused")
	private JobNodeGroup() {}
	
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

	/**
	 * @return all the ids of all children and this group.
	 */
	public HashSet<String> rebuildAndGetIds() {
		HashSet<String> res = new HashSet<String>();
		res.add(id);
		for (JobNode ch : children) {
			ch.setParent(this);
			if (ch instanceof JobNodeGroup) {
				res.addAll(((JobNodeGroup)ch).rebuildAndGetIds());
			} else {
				res.add(ch.getId());
			}
		}
		return res;
	}
}
