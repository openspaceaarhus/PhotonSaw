package dk.osaa.psaw.job;

import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashSet;

import lombok.Getter;
import lombok.Setter;
import lombok.val;

/**
 * A group of JobNodes that make up the document tree that make up a Job. 
 * 
 * @author Flemming Frandsen <dren.dk@gmail.com> <http://dren.dk>
 */
public class JobNodeGroup extends AbstractJobNode {
	
	@Getter @Setter
	PointTransformation pointTransformation;
	
	ArrayList<JobNode> children = new ArrayList<JobNode>();
	
	public JobNodeGroup(String id, AffineTransform xform) {
		super(id, xform);
	}

	public void render(JobRenderTarget target,
			PointTransformation transformation) {

		PointTransformation xform = transformation.add(pointTransformation); 
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

	@Override
	public Rectangle2D getBoundingBox() {
		Rectangle2D r = new Rectangle2D.Double();
		for (JobNode ch : children) {
			r.add(ch.getBoundingBox());
		}
		return r;
	}
	
	public ArrayList<RasterNode> getRasters() {
		val res = new ArrayList<RasterNode>(); 

		for (JobNode ch : children) {
			if (ch instanceof JobNodeGroup) {
				res.addAll(((JobNodeGroup)ch).getRasters());
			} else if (ch instanceof RasterNode) {
				res.add((RasterNode)ch);
			}
		}
		return res;
	}
}
