package dk.osaa.psaw.job;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import com.kitfox.svg.SVGDiagram;
import com.kitfox.svg.SVGException;
import com.kitfox.svg.SVGUniverse;

/**
 * Loads an SVG into a job as a JobNodeGroup, each group in the svg becomes another JobNodeGroup  
 * 
 * @author ff
 */
public class SvgLoader {
	
	public static JobNodeGroup load(Job job, String name, InputStream svgStream) throws IOException, SVGException {

		SVGUniverse su = new SVGUniverse();
		URI svgURI = su.loadSVG(svgStream, name);
		SVGDiagram diagram = su.getDiagram(svgURI);
		
		JobNodeGroup res = new JobNodeGroup(job.getNodeId(name));
		Graphics2DJobNodeGroup g2d = new Graphics2DJobNodeGroup(job, res);
		diagram.setIgnoringClipHeuristic(true);
		
		/* 
		 * Notice: I've replaced com.kitfox.svg.Group with a version that makes the svg element being rendered available
		 * to the Graphics2DJobNodeGroup which the svg is being rendered to, so it's able to pick out the style information
		 * it wants.
		 * 
		 * Special style:
		 *  photonsaw-speed : Speed in mm/s of the laser when cutting this shape, lower speed is possible
		 *  photonsaw-power : Power output at full speed 0-80W  
		 */
		diagram.render(g2d);
				
		return res;
	}
}
