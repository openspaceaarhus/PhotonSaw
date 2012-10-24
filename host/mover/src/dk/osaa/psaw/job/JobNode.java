package dk.osaa.psaw.job;

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
}
