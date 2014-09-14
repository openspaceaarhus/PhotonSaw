package dk.osaa.psaw.job;

import java.awt.geom.AffineTransform;

import lombok.Getter;
import lombok.Setter;
import lombok.val;
import lombok.extern.java.Log;

@Log
public abstract class AbstractJobNode implements JobNode {
	@Getter
	String id;
	
	@Getter @Setter
	AffineTransform nodeTransformation;
	
	@Getter @Setter
	String name;
		
	@Getter @Setter
	JobNodeGroup parent;

	AbstractJobNode(String id, AffineTransform xform) {
		this.id=id;
		this.name=id;
		this.nodeTransformation = xform;
	}	
	
	@Override
	public JobNodeID getNodeID() {		
		return getParent() != null 
					? new JobNodeID(getParent().getNodeID(), this.getId())
					: new JobNodeID(this.getId());
	}
	
	/**
	 * @return The AffineTransformation that must be applied to get to machine X/Y coordinates for this node.
	 */
	public AffineTransform getTransformation() {
		AffineTransform xf = nodeTransformation != null ? (AffineTransform)nodeTransformation.clone() : new AffineTransform();
		if (getParent() != null) {
//			xf.concatenate(getParent().getTransformation());
			xf.preConcatenate(getParent().getTransformation());
			
		}
		//log.info("Transform @ "+name+" "+xf);
		return xf;
	}
}
