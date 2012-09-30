package dk.osaa.psaw.job;

import dk.osaa.psaw.machine.Point;

/**
 * This is the interface that a job renders its output to, this is implemented by the Planner
 * which turns it into Moves to be sent to the hardware and by the preview renderer for the GUI.
 * 
 * @author ff
 */
public interface JobRenderTarget {
	/**
	 * Move as quickly as possible to this point
	 * @param p The point to move to
	 */
	void moveTo(Point p);
	
	/**
	 * Turn on the laser and move to this point at the desired speed
	 * @param p the point to move to
	 * @param intensity The intensity (0..1) of the LASER during the move
	 * @param maxSpeed The desired speed
	 */
	void cutTo(Point p, double intensity, double maxSpeed);
	
	/**
	 * Turn on the laser and move to this point at the desired speed while engraving a scanline of pixels.
	 * @param p the point to move to
	 * @param intensity The intensity (0..1) of the LASER during the move
	 * @param maxSpeed The desired speed
	 * @param pixels The pixels to engrave over this line.
	 */
	void engraveTo(Point p, double intensity, double maxSpeed, boolean[] pixels);
	
	/**
	 * Calculates the distance in mm needed to accelerate the X axis from 0 to the desired speed.
	 * 
	 * This determines the lead-in and lead-out of the engraving moves.
	 * 
	 * @param speed in mm/s
	 * @return distance in mm
	 */
	double getEngravingXAccelerationDistance(double speed);
	
	/**
	 * @return The size, in mm of one step in the Y axis as used when engraving;
	 */
	double getEngravingYStepSize();
	
	/**
	 * Turn assist air on or off
	 * @param on status of assist air
	 */
	void setAssistAir(boolean on);
}
