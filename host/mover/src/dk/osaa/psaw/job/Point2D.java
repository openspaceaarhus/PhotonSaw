package dk.osaa.psaw.job;

import java.awt.geom.AffineTransform;

import lombok.Data;

/**
 * A point in Job space.
 * 
 * Notice that we're working in mm, but not necessarily in machine space,
 * as a transformation (axis-mapping, translation, rotation and possibly scaling)
 * happens when the job is rendered into the Planners LineBuffer.
 * 
 * Lower left corner is 0,0
 * Right is +X
 * Up is +Y
 *    
 * @author ff
 */
@Data
public class Point2D {
	double x;
	double y;
	public Point2D(double x, double y) {
		this.x = x;
		this.y = y;
	}

	public void transform(AffineTransform transform) {
		double[] old = {x,y};
		double[] nc = new double[2];
		transform.transform(old, 0, nc, 0, 1);
		x = nc[0];
		y = nc[1];		
	}
}
