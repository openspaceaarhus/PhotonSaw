package dk.osaa.psaw.mover;

import java.util.ArrayList;

/**
 * A path consisting only of two-dimensional straight lines,
 * this is what a Job consists of, along with EngraveableImages.
 * 
 * Notice that we're working in mm, but not necessarily in machine space,
 * as a transformation (axis-mapping, translation, rotation and scaling)
 * happens when the Planner adds the job to the LineBuffer.   
 * 
 * @author ff
 */
public class LinePath {
	
	String name;
	public LinePath(String name) {
		this.name = name;
	}
	
	ArrayList<Point2D> points = new ArrayList<Point2D>();
	public void addPoint(double x, double y) {		
		points.add(new Point2D(x, y));
	}
}
