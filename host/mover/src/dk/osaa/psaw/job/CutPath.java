package dk.osaa.psaw.job;

import java.util.ArrayList;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.java.Log;

/**
 * A path that needs to be moved, while the LASER is on as part of a job.
 * This can be used for both cutting as the name implies, but also for doing vector engraving.
 * 
 * @author ff
 */
@Log
public class CutPath implements JobNode {

	@Getter
	String id;
	
	@Getter @Setter
	String name;
	
	@Getter @Setter
	JobNodeGroup parent;
	
	@Getter
	ArrayList<Point2D> path;
	@Getter
	double intensity;
	@Getter
	double maxSpeed;

	CutPath(String id, ArrayList<Point2D> path, double intensity, double maxSpeed) {
		this.name = id;
		this.id = id;
		this.path = path;
		this.intensity = intensity;
		this.maxSpeed = maxSpeed;
	}

	@Override
	public void render(JobRenderTarget target,
			PointTransformation transformation) {
		
		boolean first = true;
		for (Point2D p2d: path) {
			if (first) {
				target.moveTo(transformation.transform(p2d));
				first = false;			
			} else {
				target.cutTo(transformation.transform(p2d), intensity, maxSpeed);
			}
		}		
	}

	@Override
	public JobNode getChildById(String id) {
		log.warning(this.getClass().getName()+ " can't have children");
		return null;
	}

	@Override
	public JobNodeID getNodeID() {		
		return new JobNodeID(getParent().getNodeID(), this.getId());
	}
}
