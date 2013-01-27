package dk.osaa.psaw.job;

import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
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
	CutPath(String id, AffineTransform xform, LaserNodeSettings settings, ArrayList<Point2D> path) {
		super(id, xform, settings);
		this.path = path;
	}

	@Override
	public void render(JobRenderTarget target,
			PointTransformation transformation) {
		
		target.startShape(id);

		target.setAssistAir(settings.assistAir);
		
		for (int pass=0;pass<settings.passes;pass++) {
			boolean first = true;
			for (Point2D p2d: path) {
				
				if (first) {
					target.moveTo(transformation.transform(getTransformation().transform(p2d,null)));
					first = false;			
				} else {
					target.cutTo(transformation.transform(getTransformation().transform(p2d,null)), settings.intensity, settings.maxSpeed);
				}
			}
		}
		target.startShape("end-"+id);
	}

	@Override
	public Rectangle2D getBoundingBox() {
		Rectangle2D r = new Rectangle2D.Double();
		for (Point2D p2d: path) {
			r.add(getTransformation().transform(p2d,null));
		}
		return r;
	}
}
