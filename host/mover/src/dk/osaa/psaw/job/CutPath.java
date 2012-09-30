package dk.osaa.psaw.job;

import java.util.ArrayList;

import lombok.Getter;

/**
 * A path that needs to be moved, while the LASER is on as part of a job.
 * This can be used for both cutting as the name implies, but also for doing vector engraving.
 * 
 * @author ff
 */
public class CutPath extends LaserNode {

	@Getter
	ArrayList<Point2D> path;

/*
	@SuppressWarnings("unused")
	private CutPath() { super(); }
	*/
	CutPath(String id, double intensity, double maxSpeed, int passes, boolean assistAir, ArrayList<Point2D> path) {
		super(id,intensity,maxSpeed,passes,assistAir);
		this.path = path;
	}

	@Override
	public void render(JobRenderTarget target,
			PointTransformation transformation) {

		for (int pass=0;pass<passes;pass++) {
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
	}
}
