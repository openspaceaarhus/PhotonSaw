package dk.osaa.psaw.job;


/**
 * The JobNode and JobNodeGroups both render onto a target, so they both implement this.
 * 
 * @author ff
 */
public interface JobNode {
	public void render(JobRenderTarget target, PointTransformation transformation);
	public String getId();
	public JobNode getChildById(String id);
	public void setParent(JobNodeGroup parent);
	public JobNodeID getNodeID();
}
