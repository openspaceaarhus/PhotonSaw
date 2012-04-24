package dk.osaa.psaw.mover;

import lombok.Data;

/**
 * A Point in n-dimensional space.
 * 
 * @author ff
 */
@Data
public class Point {
	double axes[] = new double[Move.AXES];
 }
