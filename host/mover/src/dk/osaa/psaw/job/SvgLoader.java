package dk.osaa.psaw.job;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import lombok.val;
import lombok.extern.java.Log;

import com.kitfox.svg.SVGDiagram;
import com.kitfox.svg.SVGException;
import com.kitfox.svg.SVGUniverse;
import com.kitfox.svg.animation.AnimationElement;
import com.kitfox.svg.xml.StyleAttribute;

import dk.osaa.psaw.config.Configuration;

/**
 * Loads an SVG into a job as a JobNodeGroup, each group in the svg becomes another JobNodeGroup  
 * 
 * @author ff
 */
@Log
public class SvgLoader {
	
	public static JobNodeGroup load(Configuration cfg, Job job, String name, InputStream svgStream, double forcedDPI) throws IOException, SVGException {

		SVGUniverse su = new SVGUniverse();
		URI svgURI = su.loadSVG(svgStream, name);
		SVGDiagram diagram = su.getDiagram(svgURI);
		val e = diagram.getRoot();
		
		/* Try to guess the user units to real world conversion factor, because the
		 * fucking assholes at w3c didn't bother to specify a default pixel size or an easy way to specify it.
		 * 
		 * inkscape seems to just work in 90 DPI implicitly, without stating that anywhere, let alone in an
		 * attribute in the xml, so let's hope they never change their minds.
		 * 
		 * Everyone else seems to use either 96 DPI or 72 DPI if they are in a postscript state of mind.
		 * 
		 * Fuck!
		 * 
		 * Why? Why, would you do something like that, were you evil or just stupid?
		 */
		
		double pixelsSizeX = 25.4/96; // Default to 96 DPI, perhaps we should default to 2540 DPI, just to make uncertainty very obvious.
		double pixelsSizeY = pixelsSizeX;
		
		if (forcedDPI > 0) {
			pixelsSizeX = pixelsSizeY = 25.4/forcedDPI;
			log.info("Explicit DPI set by SVG loader: "+forcedDPI);			
			
		} else if (e.hasAttribute("photonsaw-dpi", AnimationElement.AT_XML)) {
			StyleAttribute dpiAttr = new StyleAttribute("photonsaw-dpi");
			e.getPres(dpiAttr);
			pixelsSizeX = pixelsSizeY = 25.4/dpiAttr.getDoubleValue();
			log.info("Explicit DPI set by photonsaw-dpi attribute: "+dpiAttr.getDoubleValue());
			
		} else if (e.hasAttribute("inkscape:version", AnimationElement.AT_XML)) {
			pixelsSizeX = pixelsSizeY = 25.4/90;
			log.info("Inkscape svg detected: "+name+" assuming 90 DPI for conversion to mm");

		} else {
			log.warning("Unable to guess the pixel size used in the svg file "+name+" guessing 96 DPI");
		}
				
		JobNodeGroup res = new JobNodeGroup(job.getNodeId(name));
		Graphics2DJobNodeGroup g2d = new Graphics2DJobNodeGroup(job, res);
		g2d.scale(pixelsSizeX, pixelsSizeY);
		diagram.setIgnoringClipHeuristic(true);
		
		g2d.setMaximumPower(cfg.machineConfig.getMaximumLaserPower());
		g2d.setDefaultPower(cfg.machineConfig.getMaximumLaserPower());
		
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
