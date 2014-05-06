package dk.osaa.psaw.job;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.logging.Level;

import javax.imageio.ImageIO;

import com.kitfox.svg.RenderableElement;
import com.kitfox.svg.SVGElement;
import com.kitfox.svg.SVGException;
import com.kitfox.svg.SVGGraphics2D;
import com.kitfox.svg.SVGRoot;
import com.kitfox.svg.xml.StyleAttribute;

import lombok.Getter;
import lombok.Setter;
import lombok.val;
import lombok.extern.java.Log;
import de.erichseifert.vectorgraphics2d.VectorGraphics2D;

/**
 * A graphics context that can be passed to SVGSalamander to have it render the svg primitives into a JobNodeGroup
 * 
 * @author Flemming Frandsen <dren.dk@gmail.com> <http://dren.dk>
 */
@Log
public class Graphics2DJobNodeGroup extends VectorGraphics2D implements
		SVGGraphics2D {

	private Job job;
	private JobNodeGroup jobNodeGroup;
	private RenderableElement element;

	/**
	 * The length of lines that make up curves
	 */
	@Getter
	@Setter
	private double curveFlatness;

	/**
	 * The default power to use when cutting or engraving, when nothing is
	 * specified by the svg
	 */
	@Getter
	@Setter
	private double defaultPower;

	/**
	 * The default speed to use when cutting or engraving, when nothing is
	 * specified by the svg
	 */
	@Getter
	@Setter
	private double defaultSpeed;

	/**
	 * The maximum power available for cutting and engraving
	 */
	@Getter
	@Setter
	private double maximumPower;
	
	/**
	 * The duration of each pulse, if no photonsaw-pulse-duration is used 
	 */
	@Getter
	@Setter
	private double defaultPulseDuration;
	
	/**
	 * The number of pulses per mm, if no photonsaw-ppmm style is used 
	 */
	@Getter
	@Setter
	private int defaultPpmm;
	
	@Getter
	@Setter
	private double defaultRasterSpeed;

	public Graphics2DJobNodeGroup(Job job, JobNodeGroup jobNodeGroup) {
		super(0, 0, 2000, 1000);
		this.job = job;
		this.jobNodeGroup = jobNodeGroup;
		curveFlatness = 0.1;
		defaultPower = 80;
		maximumPower = 80;
		defaultSpeed = 6;
		defaultPulseDuration = 3; // 3 ms
		defaultPpmm = 100;
		defaultRasterSpeed = 500;
		setRasteredImageSizeMaximum(256);
	}

	@Override
	public void startRendering(RenderableElement element) {
		this.element = element;
		log.info("Rendering SVG element: "+element.getId());
	}
	
	LaserNodeSettings getLaserNodeSettings() {
		return new LaserNodeSettings(
				getPower()/maximumPower,
				getSpeed(),
				getPasses(),
				getAssistAir(),
				getPulsesPermm(),
				getPulseDuration(),
				getRasterLinePitch(),
				getRasterSpeed());
	}

	void addChild(JobNode newChild) {
		val stack = new ArrayList<SVGElement>();  
		
		
		SVGElement e = element;
		while ((e = e.getParent()) != null && !(e instanceof SVGRoot)) {
			if (e.getId() != null) {
				stack.add(e);
			}
		}
		
		Collections.reverse(stack);
		
		JobNodeGroup parent = jobNodeGroup;
		for (val elem : stack) {
			
			String id=elem.getId();
			String groupMode = getStyleAttr("inkscape:groupmode");			
			String label = getStyleAttr("inkscape:label");
			if (label == null) {
				label = id;
			}
			
			JobNode jn = parent.getChildById(id);

			if (jn == null) {
				val np = new JobNodeGroup(id, null);
				np.setName(label);
				
				if (groupMode != null && groupMode.equals("layer")) {
					np.setLayer(true);
				}
				
				parent.addChild(np);
				parent = np;

			} else if (jn instanceof JobNodeGroup) {
				parent = (JobNodeGroup)jn; 

			} else {
				// Hmm, this is not good.
				log.severe("Found a parent item that corrosponds to a job node which is not a group: "+jn.toString());				
			}
		}
		
		parent.addChild(newChild);		
	}
	
	
	void addCutPath(ArrayList<Point2D> points) {
		/*
		ArrayList<Point2D> newPoints = new ArrayList<Point2D>();
		for (Point2D p : points) {
			newPoints.add(getTransform().transform(p, null));
		}
		*/
		
		CutPath path = new CutPath(job.getNodeId(getId()), getTransform(), getLaserNodeSettings(), points);
		//log.info("Appending CutPath: "+path.getId());
		addChild(path);
	}

	@Override
	public FontRendering getFontRendering() {
		return FontRendering.VECTORS; // We don't want to handle text so get it as vectors
	}
			
	public void fill(Shape s) {
		// TODO: Add some sort of handling for filling shapes in stead of ignoring them
		writeClosingFill(s);
	}

	@Override
	protected void writeClosingDraw(Shape s) {
		PathIterator segments = s.getPathIterator(null, curveFlatness);
		double[] coordsCur = new double[6];
		double[] pointPrev = new double[2];
		ArrayList<Point2D> points = null;

		while (!segments.isDone()) {
			int segmentType = segments.currentSegment(coordsCur);

			if (segmentType == PathIterator.SEG_MOVETO) {
				pointPrev[0] = coordsCur[0];
				pointPrev[1] = coordsCur[1];

				if (points != null) {
					addCutPath(points);
				}
				points = new ArrayList<Point2D>();
				points.add(new Point2D.Double(coordsCur[0], coordsCur[1]));

			} else if (segmentType == PathIterator.SEG_LINETO) {
				points.add(new Point2D.Double(coordsCur[0], coordsCur[1]));
				pointPrev[0] = coordsCur[0];
				pointPrev[1] = coordsCur[1];

				// } else if (segmentType == PathIterator.SEG_CUBICTO) { //
				// Should be flattened for us
				// } else if (segmentType == PathIterator.SEG_QUADTO) { //
				// Should be flattened for us

			} else if (segmentType == PathIterator.SEG_CLOSE) {
				points.add(points.get(0));
				addCutPath(points);
				points = null;

			} else {
				throw new RuntimeException("Unimplemented segment type for path: "+ segmentType);
			}

			segments.next();
		}
		if (points != null) {
			addCutPath(points);
		}
	}
	
	static final private int RASTERED_IMAGE_SIZE = 1000;

	protected void writeClosingFill(Shape s) {
		
		// First transform the shape to the job coordinate system:
		AffineTransform xform = getTransform();
		Shape xShape = xform.createTransformedShape(s);
		
		
		Rectangle2D shapeBounds = xShape.getBounds2D();

		// Calculate dimensions of shape with current transformations
		double pixelSize = getRasterLinePitch();
		int wImage = (int) Math.ceil(shapeBounds.getWidth()/pixelSize);
		int hImage = (int) Math.ceil(shapeBounds.getHeight()/pixelSize);
		// Limit the size of images
		wImage = Math.min(wImage, RASTERED_IMAGE_SIZE);
		hImage = Math.min(hImage, RASTERED_IMAGE_SIZE);

		// Create image to paint draw gradient with current transformations
		BufferedImage paintImage = new BufferedImage(wImage, hImage, BufferedImage.TYPE_INT_ARGB);

		// Paint shape
		Graphics2D g = (Graphics2D) paintImage.getGraphics();
		g.scale(wImage/shapeBounds.getWidth(), hImage/shapeBounds.getHeight());
		g.translate(-shapeBounds.getX(), -shapeBounds.getY());
		g.setPaint(getPaint());
		g.fill(xShape);
		// Free resources
		g.dispose();
		
		AffineTransform scaleAndTranslate = new AffineTransform();
		scaleAndTranslate.translate(shapeBounds.getX(), shapeBounds.getY());
		scaleAndTranslate.scale(shapeBounds.getWidth()/wImage, shapeBounds.getHeight()/hImage);

		/* 
		 	// It's handy to be able to dump the images out to some pngs for debugging.
		File outputfile = new File("/tmp/"+getId()+".png");
	    try {
			ImageIO.write(paintImage, "png", outputfile);
			log.info("Stored: "+outputfile);
		} catch (IOException e) {
			log.log(Level.SEVERE, "Failed to store "+outputfile+" ", e);
		}*/
		
		// Output image of gradient
		addChild(new RasterNode(job.getNodeId(getId()), scaleAndTranslate, getLaserNodeSettings(), paintImage));
	}
	
	
	String getStyleAttr(String name) {
		if (element != null) {
			StyleAttribute attr = new StyleAttribute(name);
			try {
				element.getStyle(attr, true);
			} catch (SVGException e) {
				log.log(Level.WARNING, "Failed to get "+name+" attribute from svg element", e);
				attr = null;
			}			
			if (attr != null && !attr.getStringValue().equals("")) {
				return attr.getStringValue();
			}
		}
		
		return null;
	}

	double getPower() {
		String value = getStyleAttr("photonsaw-power");
		if (value != null) {
			return Math.min(maximumPower, Math.max(0, Double.parseDouble(value)));			
		} else {
			return defaultPower;
		}
	}
	
	private double getRasterLinePitch() {
		String value = getStyleAttr("photonsaw-raster-pitch");
		if (value == null) {
			value = getStyleAttr("photonsaw-linepitch");
		}
		if (value != null) {
			return Math.min(10, Math.max(0.0375, Double.parseDouble(value)));	
		} else {
			return 0.0375;
		}
	}
	
	private double getRasterSpeed() {
		String value = getStyleAttr("photonsaw-raster-speed");
		if (value != null) {
			return Math.min(10000, Math.max(1, Double.parseDouble(value)));
		} else {
			return defaultRasterSpeed;
		}
	}
	
	double getSpeed() {
		String value = getStyleAttr("photonsaw-speed");
		if (value != null) {
			return Math.min(10000, Math.max(1, Double.parseDouble(value)));
		} else {
			return defaultSpeed;
		}
	}
	
	int getPasses() {
		String value = getStyleAttr("photonsaw-passes");
		if (value != null) {
			return Math.min(10, Math.max(1, Integer.parseInt(value)));
		} else {
			return 1;
		}
	}
	
	int getPulsesPermm() {
		String value = getStyleAttr("photonsaw-ppmm");
		if (value != null) {
			return Math.min(10000, Math.max(0, Integer.parseInt(value)));			
		} else {
			return defaultPpmm;			
		}
	}
	
	double getPulseDuration() {
		String value = getStyleAttr("photonsaw-pulse-length");
		if (value != null) {
			return Math.min(100000, Math.max(1, Integer.parseInt(value)));			
		} else {
			return defaultPulseDuration;	
		}
	}
	
	boolean getAssistAir() {
		String value = getStyleAttr("photonsaw-assistair");
		if (value != null) {
			return isTrue(value);	
		} else {
			return true;
		}
	}
	
	static private boolean isTrue(String stringValue) {
		return stringValue.equalsIgnoreCase("true") || stringValue.equals("1") || stringValue.equalsIgnoreCase("yes")|| stringValue.equalsIgnoreCase("on");
	}

	String getId() {
		String id = "svg-node";
		if (element != null) {
			id = element.getId();
		}
		return id;		
	}
	

	@Override
	protected void writeImage(Image img_, int imgWidth, int imgHeight, double x, double y, double width, double height) {
		BufferedImage img = (BufferedImage)img_;
		RasterNode rn = new RasterNode(job.getNodeId(getId()), getTransform(), getLaserNodeSettings(), img);
		addChild(rn);
	}

	@Override
	protected void writeShape(Shape s) {
		// Do nothing.
	}

	@Override
	protected void writeString(String str, double x, double y) {
		// This never gets called.
	}

	@Override
	protected void writeHeader() {
		// Don't care.
	}

	@Override
	protected String getFooter() {
		return null; // Don't care.
	}

	@Override
	public boolean useDrawInSteadOfFillForStroke() {
		return true;
	}
}
