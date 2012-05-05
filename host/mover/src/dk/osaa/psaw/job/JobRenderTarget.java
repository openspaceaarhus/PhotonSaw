package dk.osaa.psaw.job;

import dk.osaa.psaw.machine.Point;
import dk.osaa.psaw.machine.Scanline;

/**
 * This is the interface that a job renders its output to, this is implemented by the Planner
 * which turns it into Moves to be sent to the hardware and by the preview renderer for the GUI.
 * 
 * @author ff
 */
public interface JobRenderTarget {
	void moveTo(   Point p);
	void cutTo(    Point p, double intensity, double maxSpeed);
	void engraveTo(Point p, double intensity, double maxSpeed, Scanline scanline);
}
