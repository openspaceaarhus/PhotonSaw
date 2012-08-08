package dk.osaa.psaw.job;

import java.awt.Image;
import java.awt.Shape;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.logging.Level;

import com.kitfox.svg.RenderableElement;
import com.kitfox.svg.SVGException;
import com.kitfox.svg.SVGGraphics2D;
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

	public Graphics2DJobNodeGroup(Job job, JobNodeGroup jobNodeGroup) {
		super(0, 0, 2000, 1000);
		this.job = job;
		this.jobNodeGroup = jobNodeGroup;
		curveFlatness = 0.1;
		defaultPower = 80;
		maximumPower = 80;
		defaultSpeed = 300;
	}

	@Override
	public void startRendering(RenderableElement element) {
		this.element = element;
		log.info("Rendering SVG element: "+element.getId());
	}
	
	void addCutPath(ArrayList<Point2D> points) {
		ArrayList<Point2D> newPoints = new ArrayList<Point2D>();
		for (Point2D p : points) {
			Point2D np = new Point2D(p.getX(), p.getY());
			np.transform(getTransform());
			newPoints.add(np);
		}
		
		CutPath path = new CutPath(job.getNodeId(getId()), getPower()/maximumPower, getSpeed(), newPoints);
		log.info("Appending CutPath: "+path.getId());
		jobNodeGroup.addChild(path);
	}

	@Override
	public FontRendering getFontRendering() {
		return FontRendering.VECTORS; // We don't want to handle text so get it as vectors
	}
			
	public void fill(Shape s) {
		// TODO: Add some sort of handling for filling shapes in stead of ignoring them
		log.warning("Going to cut outline of shape in stead of filling it");
		writeClosingDraw(s);
		// writeClosingFill(s);
	}

	@Override
	protected void writeClosingDraw(Shape s) {
		if (s instanceof Line2D) {
			Line2D l = (Line2D) s;
			val points = new ArrayList<Point2D>();
			points.add(new Point2D(l.getX1(), l.getY1()));
			points.add(new Point2D(l.getX2(), l.getY2()));
			addCutPath(points);
			return;

		} else if (s instanceof Rectangle2D) {
			Rectangle2D r = (Rectangle2D) s;
			val points = new ArrayList<Point2D>();
			points.add(new Point2D(r.getX(), r.getY()));
			points.add(new Point2D(r.getX() + r.getWidth(), r.getY()));
			points.add(new Point2D(r.getX() + r.getWidth(), r.getY() + r.getHeight()));
			points.add(new Point2D(r.getX(), r.getY() + r.getHeight()));
			points.add(new Point2D(r.getX(), r.getY()));
			addCutPath(points);
			return;

		} else if (s instanceof Ellipse2D) {
			/*
			 * Ellipse2D e = (Ellipse2D) s; double x = e.getX() +
			 * e.getWidth()/2.0; double y = e.getY() + e.getHeight()/2.0; double
			 * rx = e.getWidth()/2.0; double ry = e.getHeight()/2.0;
			 */
			throw new RuntimeException("Ellipse not implemented, try a path in stead, sorry");

		} else if (s instanceof Arc2D) {

			throw new RuntimeException("Arc2D not implemented, try a path in stead, sorry");

			/*
			 * Arc2D e = (Arc2D) s; double x = (e.getX() + e.getWidth()/2.0);
			 * double y = (e.getY() + e.getHeight()/2.0); double rx =
			 * e.getWidth()/2.0; double ry = e.getHeight()/2.0; double
			 * startAngle = -e.getAngleStart(); double endAngle =
			 * -(e.getAngleStart() + e.getAngleExtent()); write(x, " ", y, " ",
			 * rx, " ", ry, " ", startAngle, " ", endAngle, " ellipse"); if
			 * (e.getArcType() == Arc2D.CHORD) { write(" Z"); } else if
			 * (e.getArcType() == Arc2D.PIE) { write(" ", x, " ", y, " L Z"); }
			 * return;
			 */
			
		} else {
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
					points.add(new Point2D(coordsCur[0], coordsCur[1]));

				} else if (segmentType == PathIterator.SEG_LINETO) {
					points.add(new Point2D(coordsCur[0], coordsCur[1]));
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
	}
	
	double getPower() {
		double power = defaultPower;
		if (element != null) {
			StyleAttribute powerAttr = new StyleAttribute("photonsaw-power");
			try {
				element.getStyle(powerAttr, true);
			} catch (SVGException e) {
				log.log(Level.WARNING, "Failed to get power attribute from svg element", e);
				powerAttr = null;
			}
			if (powerAttr != null && !powerAttr.getStringValue().equals("")) {
				power = Math.min(maximumPower,Math.max(0, powerAttr.getDoubleValue()));
			}
		}
		return power;
	}
	
	double getSpeed() {
		double speed = defaultSpeed;
		if (element != null) {
			StyleAttribute speedAttr = new StyleAttribute("photonsaw-speed");
			try {
				element.getStyle(speedAttr, true);
			} catch (SVGException e) {
				log.log(Level.WARNING, "Failed to get speed attribute from svg element", e);
				speedAttr = null;
			}			
			if (speedAttr != null && !speedAttr.getStringValue().equals("")) {
				speed = Math.min(1000, Math.max(1, speedAttr.getDoubleValue()));
			}
		}
		return speed;
	}
	
	String getId() {
		String id = "svg-node";
		if (element != null) {
			id = element.getId();
		}
		return id;		
	}
	

	@Override
	protected void writeImage(Image img, int imgWidth, int imgHeight, double x, double y, double width, double height) {

		// Note: We do not support rotation of rasters at this point.
		Point2D pos = new Point2D(x, y);
		pos.transform(getTransform());
		width  = width *getTransform().getScaleX(); 
		height = height*getTransform().getScaleY(); 
		
		if (!(img instanceof BufferedImage)) {
			throw new RuntimeException("The Image passed to writeImage was not a BufferedImage: "+img.getClass().getName());
		}
		
		EngraveRaster engraving = new EngraveRaster(job.getNodeId(getId()), getPower()/maximumPower, getSpeed(), 
													(BufferedImage) img, pos.x, pos.y, width, height);
		log.info("Appending EngraveRaster: "+engraving.getId());
		jobNodeGroup.addChild(engraving);		
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
