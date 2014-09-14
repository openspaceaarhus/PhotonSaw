package dk.osaa.psaw.job;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * The JobNode and JobNodeGroups both render onto a target, so they both implement this.
 * 
 * @author ff
 */
public interface JobNode {
	public void render(JobRenderTarget target, PointTransformation transformation);
	
	/*
	 * Note these are all taken care of by the AbstractJobNode, so just extend that.
	 */	
	public String getId();
	public void setParent(JobNodeGroup parent);
	public JobNodeID getNodeID();
	public Rectangle2D getBoundingBox();
	public Point2D getStartPoint();
	public Point2D getEndPoint();
}
