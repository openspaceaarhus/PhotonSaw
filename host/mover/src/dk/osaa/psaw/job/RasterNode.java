package dk.osaa.psaw.job;

import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import lombok.Getter;
import lombok.extern.java.Log;

@Log
public class RasterNode extends LaserNode {

	@Getter
	Image image;
	
	RasterNode(String id, AffineTransform xform, LaserNodeSettings settings, Image image) {
		super(id, xform, settings);
		this.image = image;
	}

	@Override
	public void render(JobRenderTarget target, PointTransformation transformation) {
		// We don't do anything here, because only the vector bits get to render in order.
	}

	@Override
	public Rectangle2D getBoundingBox() {
		
		Point2D bb[] = new Point2D[4];
		bb[0] = new Point2D.Double(0, 0);
		bb[1] = new Point2D.Double(image.getWidth(null)-1, 0);
		bb[2] = new Point2D.Double(image.getWidth(null)-1, image.getHeight(null)-1);
		bb[3] = new Point2D.Double(0, image.getHeight(null)-1);
		
		Double xmin = Double.MAX_VALUE;
		Double ymin = Double.MAX_VALUE;
		Double xmax = Double.MIN_VALUE;
		Double ymax = Double.MIN_VALUE;
		
		AffineTransform xform = getTransformation(); 
		for (Point2D p : bb) {
			Point2D tp = xform.transform(p, null);
			//log.info("Transformed image bb point "+p+" to "+tp);
			xmin = Math.min(xmin, tp.getX());
			xmax = Math.max(xmax, tp.getX());
			ymin = Math.min(ymin, tp.getY());
			ymax = Math.max(ymax, tp.getY());
		}
		Rectangle2D r = new Rectangle2D.Double();
		r.setRect(xmin, ymin, xmax-xmin, ymax-ymin);
		//log.info("Image bounding box: "+r);
		return r;
	}

}
