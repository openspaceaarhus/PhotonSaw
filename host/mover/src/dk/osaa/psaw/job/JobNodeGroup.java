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
public class JobNodeGroup extends AbstractJobNode {
	
	@Getter @Setter
	PointTransformation nodeTransformation;
	
	ArrayList<JobNode> children = new ArrayList<JobNode>();
	
	public JobNodeGroup(String id) {
		super(id);
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

	public JobNode getChildById(String id) {
		for (JobNode ch : children) {
			if (ch.getId().equals(id)) {
				return ch;
			}
		}
		return null; // Not found.
	}
	
	public void getStructure(StringBuilder sb, String indent) {		
		sb.append(indent);
		sb.append(id); 
		sb.append(" ("+this.getClass()+")\n"); 
		indent = indent+" ";
		for (JobNode ch : children) {
			if (ch instanceof JobNodeGroup) {
				((JobNodeGroup) ch).getStructure(sb, indent);
			} else {
				sb.append(indent);
				sb.append(ch.getId());
				sb.append(" ("+ch.getClass()+")\n"); 
			}
		}
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
