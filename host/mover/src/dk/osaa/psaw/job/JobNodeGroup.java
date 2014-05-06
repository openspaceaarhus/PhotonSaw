package dk.osaa.psaw.job;

import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashSet;

import javax.management.RuntimeErrorException;

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
	boolean layer;
	
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
	
	public void optimizeCuts() {
		
		// First have every child group optimize itself
		for (JobNode ch : children) {
			if (ch instanceof JobNodeGroup) {
				((JobNodeGroup) ch).optimizeCuts();
			}
		}
		
		
		// The children that have not been added to the new children list yet
		ArrayList<JobNodeEndPoint> toGo = new ArrayList<JobNodeEndPoint>();
		
		// The new children list.
		ArrayList<JobNode> nc = new ArrayList<JobNode>();
		
		// Find the first child node with a known end point and add all the children without an end position.
		Point2D current = null;
		for (JobNode ch : children) {
			if (ch instanceof JobNodeGroup) {
				if (((JobNodeGroup) ch).isLayer()) {
					nc.add(ch); // Bail out and add layers without reordering them
					continue;
				}				
			}
			
			Point2D chs = ch.getEndPoint();
			if (chs == null) {
				nc.add(ch);	
				
			} else if (current == null) {
				current = chs;
				nc.add(ch);
				
			} else {
				
				if (ch instanceof CutPath) {
					toGo.add(new JobNodeEndPoint(ch, false));
					toGo.add(new JobNodeEndPoint(ch, true));	
				} else {
					toGo.add(new JobNodeEndPoint(ch, false));
				}
			}
		}
		
		// For the rest of the children we'll need to find the one that has an end which is the closest possible to the current point
		JobNode lastBest = null;
		while (!toGo.isEmpty()) {
			JobNodeEndPoint best = null;
			double bestDist = -1;
			
			ArrayList<JobNodeEndPoint> ntoGo = new ArrayList<JobNodeEndPoint>();
			for (JobNodeEndPoint ep : toGo) {
				if (lastBest != null) {
					if (lastBest.equals(ep.getNode())) {
						continue;
					}
				}
				ntoGo.add(ep);
				
				double dist = current.distance(ep.getPoint());
				if (best == null || bestDist > dist) {
					best = ep;
					bestDist = dist;					
				}
			}
			toGo = ntoGo;
			if (best == null) {
				break;
			}
			lastBest = best.getNode(); // Get rid of all nodes of this type during the next search
			
			if (best.isReversed()) {
				if (best.getNode() instanceof CutPath) {
					((CutPath) best.getNode()).reverse();
				} else {
					throw new RuntimeException("Fail: The node is not reversible");
				}
			} 
			current = best.getNode().getEndPoint();
			nc.add(best.getNode());			
		}
		
		if (nc.size() != children.size()) {
			throw new RuntimeException("Fail: The new children list is different from the old one: "+nc.size()+" != "+children.size());			
		}
		
		children = nc;		
	}

	@Override
	public Point2D getStartPoint() {
		for (JobNode ch : children) {
			Point2D res = ch.getStartPoint();
			if (res != null) {
				return res;
			}
		}
		return null;
	}

	@Override
	public Point2D getEndPoint() {
		for (int i=children.size()-1;i>=0;i--) {
			Point2D res = children.get(i).getEndPoint();
			if (res != null) {
				return res;
			}
		}
		return null;
	}
}
