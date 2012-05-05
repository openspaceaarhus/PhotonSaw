package dk.osaa.psaw.job;

import java.io.InputStream;

/**
 * Loads an SVG into a job as a JobNodeGroup, each group in the svg becomes another JobNodeGroup  
 * 
 * @author ff
 */
public class SvgLoader {

	public static JobNodeGroup load(Job job, String name, InputStream svgStream) {

		throw new RuntimeException("Loading svg files not implemented yet, sorry");
	}
}
