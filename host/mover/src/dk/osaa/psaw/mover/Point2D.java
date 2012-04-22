package dk.osaa.psaw.mover;

import lombok.Data;

/**
 * A point in Job space.
 * 
 * Notice that we're working in mm, but not necessarily in machine space,
 * as a transformation (axis-mapping, translation, rotation and scaling)
 * happens when the Planner adds the job to the LineBuffer.   
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
}
