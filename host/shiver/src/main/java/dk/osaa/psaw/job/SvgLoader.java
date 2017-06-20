package dk.osaa.psaw.job;

import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.val;
import lombok.extern.java.Log;

import com.kitfox.svg.SVGDiagram;
import com.kitfox.svg.SVGException;
import com.kitfox.svg.SVGUniverse;
import com.kitfox.svg.animation.AnimationElement;
import com.kitfox.svg.xml.NumberWithUnits;
import com.kitfox.svg.xml.StyleAttribute;

import dk.osaa.psaw.config.PhotonSawMachineConfig;



/**
 * Loads an SVG into a job as a JobNodeGroup, each group in the svg becomes another JobNodeGroup  
 * 
 * @author ff
 */
@Log
public class SvgLoader {

	private static final Pattern MAJOR_MINOR_VERSION = Pattern.compile("^(\\d+)\\.(\\d+)");
	
	public static JobNodeGroup load(PhotonSawMachineConfig cfg, Job job, String name, InputStream svgStream, double forcedDPI) throws IOException, SVGException {

		SVGUniverse su = new SVGUniverse();
		URI svgURI = su.loadSVG(svgStream, name);
		SVGDiagram diagram = su.getDiagram(svgURI);
		val e = diagram.getRoot();

		// SVG DPI, default to 96 DPI as recommended by the SVG Working Group (and used in e.g. Inkscape 0.92 and later)
		double dpiX = 96;
		double dpiY = 96;

		// Page size and viewbox attributes
		StyleAttribute widthAttr = new StyleAttribute("width");
		StyleAttribute heightAttr = new StyleAttribute("height");
		StyleAttribute viewBoxAttr = new StyleAttribute("viewBox");

		if (forcedDPI > 0) {
			dpiX = dpiY = forcedDPI;
			log.warning("Explicit default DPI set by SVG loader: "+dpiX+"x"+dpiY);
		} else if (e.hasAttribute("photonsaw-dpi", AnimationElement.AT_XML)) {
			StyleAttribute dpiAttr = new StyleAttribute("photonsaw-dpi");
			e.getPres(dpiAttr);
			dpiX = dpiY = dpiAttr.getDoubleValue();
			log.warning("Explicit default DPI set by photonsaw-dpi attribute: "+dpiX+"x"+dpiY);
		} else if (e.hasAttribute("inkscape:version", AnimationElement.AT_XML)) {
			StyleAttribute versionAttr = new StyleAttribute("inkscape:version");
			e.getPres(versionAttr);
			String inkscapeVersion = versionAttr.getStringValue();

			Matcher majorMinor = MAJOR_MINOR_VERSION.matcher(inkscapeVersion);
			if (majorMinor.find()) {
				long major = Long.parseLong(majorMinor.group(1));
				long minor = Long.parseLong(majorMinor.group(2));
				log.fine("Parsed inkscape version: "+major+"."+minor);
				if (major == 0 && minor <= 91) {
					dpiX = dpiY = 90;
				}
			}
			log.info("Inkscape version "+inkscapeVersion+" svg detected: "+name+" assuming "+dpiX+"x"+dpiY+" default DPI");
		}

		if (e.getPres(widthAttr) && e.getPres(heightAttr) && e.getPres(viewBoxAttr)) {
			// Calculate DPI from page size and view box
			NumberWithUnits width = widthAttr.getNumberWithUnits();
			NumberWithUnits height = heightAttr.getNumberWithUnits();
			float[] coords = viewBoxAttr.getFloatList();
			Rectangle2D.Float viewBox = new Rectangle2D.Float(coords[0], coords[1], coords[2], coords[3]);

			// Calculate DPI from page size and viewbox
			if (width.getUnits() == NumberWithUnits.UT_IN) {
				dpiX = viewBox.getWidth()/width.getValue();
			} else if (width.getUnits() == NumberWithUnits.UT_MM) {
				dpiX = viewBox.getWidth()/width.getValue()*25.4;
			} else if (width.getUnits() == NumberWithUnits.UT_CM) {
				dpiX = viewBox.getWidth()/width.getValue()*2.54;
			} else if (width.getUnits() == NumberWithUnits.UT_PX || width.getUnits() == NumberWithUnits.UT_UNITLESS) {
				dpiX = viewBox.getWidth()/(width.getValue()/dpiX);
			} else {
				log.warning("Unsupported unit "+NumberWithUnits.unitsAsString(width.getUnits())+"on SVG width attribute, ignoring SVG width scale factor");
			}

			if (height.getUnits() == NumberWithUnits.UT_IN) {
				dpiY = viewBox.getHeight()/height.getValue();
			} else if (height.getUnits() == NumberWithUnits.UT_MM) {
				dpiY = viewBox.getHeight()/height.getValue()*25.4;
			} else if (height.getUnits() == NumberWithUnits.UT_CM) {
				dpiY = viewBox.getHeight()/height.getValue()*2.54;
			} else if (height.getUnits() == NumberWithUnits.UT_PX || height.getUnits() == NumberWithUnits.UT_UNITLESS) {
				dpiY = viewBox.getHeight()/(width.getValue()/dpiY);
			} else {
				log.warning("Unsupported unit "+NumberWithUnits.unitsAsString(height.getUnits())+"on SVG height attribute, ignoring SVG height scale factor");
			}
		} else {
			log.warning("No width/height/viewBox attributes in SVG file "+name+", assuming "+dpiX+"x"+dpiY+" DPI");
		}

		// Remove width, height and viewBox attributes - if present - as these cause svgSalamander to do its own scaling of the renderered SVG
		if (e.getPres(widthAttr)) {
		    e.removeAttribute(widthAttr.getName(), AnimationElement.AT_XML);
		}
		if (e.getPres(heightAttr)) {
		    e.removeAttribute(heightAttr.getName(), AnimationElement.AT_XML);
		}
		if (e.getPres(viewBoxAttr)) {
		    e.removeAttribute(viewBoxAttr.getName(), AnimationElement.AT_XML);
		}
		su.updateTime();

		log.info("Scaling SVG file "+name+" from "+dpiX+"x"+dpiY+" DPI to 96x96 DPI");

		// Create the affine transform that is used to convert from SVG DPI to 96 DPI
		AffineTransform scaleTo96dpi = AffineTransform.getScaleInstance(25.4/dpiX, 25.4/dpiY);

		JobNodeGroup res = new JobNodeGroup(job.getNodeId(name), scaleTo96dpi);
		Graphics2DJobNodeGroup g2d = new Graphics2DJobNodeGroup(job, res);
		// TODO: different resolution in x/y?
		g2d.setResolution(25.4/cfg.getMmPerStep().getAxis(0));
		diagram.setIgnoringClipHeuristic(true);
		
		g2d.setMaximumPower(cfg.getMaximumLaserPower());
		g2d.setDefaultPower(cfg.getMaximumLaserPower());
		
		/*
		 * 
		 * Special style:
		 *  photonsaw-speed : Speed in mm/s of the laser when cutting this shape, lower speed is possible
		 *  photonsaw-power : Power output at full speed 0-80W  
		 */
		diagram.render(g2d);
				
		return res;
	}
}
