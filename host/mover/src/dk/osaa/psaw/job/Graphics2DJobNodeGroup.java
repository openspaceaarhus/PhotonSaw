package dk.osaa.psaw.job;

import java.awt.Image;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.CubicCurve2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Rectangle2D;
import java.lang.annotation.ElementType;
import java.util.ArrayList;

import javax.management.RuntimeErrorException;

import com.kitfox.svg.RenderableElement;
import com.kitfox.svg.SVGGraphics2D;
import com.kitfox.svg.xml.StyleAttribute;

import lombok.val;
import lombok.extern.java.Log;

import de.erichseifert.vectorgraphics2d.VectorGraphics2D;

@Log
public class Graphics2DJobNodeGroup extends VectorGraphics2D  implements SVGGraphics2D {
	
	public static final double PIXELS_PER_MM = 90/25.4;
	private Job job;
	private JobNodeGroup jobNodeGroup;
	private double curveFlatness; // The length of lines that make up curves
	private double defaultPower;
	private double defaultSpeed;
	private double maximumPower;

	public Graphics2DJobNodeGroup(Job job, JobNodeGroup jobNodeGroup) {
		super(0, 0, 2000*PIXELS_PER_MM, 1000*PIXELS_PER_MM);
		this.job = job;
		this.jobNodeGroup = jobNodeGroup;
		curveFlatness = 0.1;
		defaultPower = 80;
		maximumPower = 80;
		defaultSpeed = 100;
	}
	
	RenderableElement element;
	@Override
	public void startRendering(RenderableElement element) {
		this.element = element; 
	}

	void addCutPath(ArrayList<Point2D> points) {
		
		double power = defaultPower;
		double speed = defaultSpeed;
		String id = "svg-node";
		if (element != null) {
			StyleAttribute powerAttr = element.getStyleAbsolute("photonsaw-power");
			if (powerAttr != null) {
				power = Math.max(maximumPower, Math.min(0, powerAttr.getDoubleValue()));				
			}
			StyleAttribute speedAttr = element.getStyleAbsolute("photonsaw-speed");
			if (speedAttr != null) {
				speed = Math.max(1000, Math.min(1, speedAttr.getDoubleValue()));
			}
			
			id = element.getId();
		}	
		
		CutPath path = new CutPath(job.getNodeId(id), points, power / maximumPower, speed);
		jobNodeGroup.addChild(path);
	}
	
	@Override
	protected void writeShape(Shape s) {
	
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
			points.add(new Point2D(r.getX()             , r.getY()));
			points.add(new Point2D(r.getX()+r.getWidth(), r.getY()));
			points.add(new Point2D(r.getX()+r.getWidth(), r.getY()+r.getHeight()));
			points.add(new Point2D(r.getX()             , r.getY()+r.getHeight()));
			points.add(new Point2D(r.getX()             , r.getY()));
			addCutPath(points);
			return;
			
		} else if (s instanceof Ellipse2D) {
			/*
			Ellipse2D e = (Ellipse2D) s;
			double x = e.getX() + e.getWidth()/2.0;
			double y = e.getY() + e.getHeight()/2.0;
			double rx = e.getWidth()/2.0;
			double ry = e.getHeight()/2.0;
			*/
			throw new RuntimeException("Ellipse not implemented, try a path in stead, sorry");
			
		} else if (s instanceof Arc2D) {

			throw new RuntimeException("Arc2D not implemented, try a path in stead, sorry");
			
			/*
			Arc2D e = (Arc2D) s;
			double x = (e.getX() + e.getWidth()/2.0);
			double y = (e.getY() + e.getHeight()/2.0);
			double rx = e.getWidth()/2.0;
			double ry = e.getHeight()/2.0;
			double startAngle = -e.getAngleStart();
			double endAngle = -(e.getAngleStart() + e.getAngleExtent());
			write(x, " ", y, " ", rx, " ", ry, " ", startAngle, " ", endAngle,
					" ellipse");
			if (e.getArcType() == Arc2D.CHORD) {
				write(" Z");
			} else if (e.getArcType() == Arc2D.PIE) {
				write(" ", x, " ", y, " L Z");
			}
			return;
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
					
				// } else if (segmentType == PathIterator.SEG_CUBICTO) { // Should be flattened for us
				// } else if (segmentType == PathIterator.SEG_QUADTO) {  // Should be flattened for us
										
				} else if (segmentType == PathIterator.SEG_CLOSE) {
					points.add(points.get(0));
					addCutPath(points);
					points = null;
					
				} else {
					throw new RuntimeException("Unimplemented segment type for path: "+segmentType);
				}

				segments.next(); 
			}
			if (points != null) {
				addCutPath(points);
			}					
		}			
	}
	
	public void fill(Shape s) {
		writeShape(s);
		
		// TODO: Add some sort of handling for filling shapes in stead of ignoring them
		//writeClosingFill(s);
	}

	@Override
	protected void writeClosingDraw(Shape s) {
		writeShape(s);
	}

	
	@Override
	protected void writeString(String str, double x, double y) {
		log.warning("Ignoring text: "+str); // TODO: Create an annotation JobNode for this.
	}

	@Override
	protected void writeImage(Image img, int imgWidth, int imgHeight, double x,
			double y, double width, double height) {
		// TODO Auto-generated method stub
		throw new RuntimeException("Engraving not supported, sorry");
	}
	
	@Override
	protected void writeHeader() {
		// Don't care.
	}

	@Override
	protected String getFooter() {
		return null; // Don't care.
	}

}
