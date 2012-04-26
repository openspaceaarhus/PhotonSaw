package dk.osaa.psaw.mover;

import lombok.Data;
import lombok.ToString;

/**
 * A Point in n-dimensional space.
 * 
 * @author ff
 */
@Data
@ToString
public class Point {
	double axes[] = new double[Move.AXES];
 }
