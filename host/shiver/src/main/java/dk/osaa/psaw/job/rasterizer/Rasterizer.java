package dk.osaa.psaw.job.rasterizer;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

import dk.osaa.psaw.job.*;
import lombok.val;
import lombok.extern.java.Log;

/**
 * This is the great big lump of code that takes care of collecting all the RasterNodes in the job and merging the transformed rasters
 * into fewer, ideally one, large raster, then outputs the merged raster lines to the render target.
 * 
 * @author Flemming Frandsen <dren.dk@gmail.com> <http://dren.dk>
 */
@Log
public class Rasterizer {

	/**
	 * 
	 * @param x0 The start of the engraving line
	 * @param x1 The end of the engraving line
	 * @param y The line to engrave on
	 * @param leadin The length to leave on either side of the engraving line for acceleration
	 * @param yStep The height of the line, the deceleration line will move down this amount  
	 * @param pixels The pixels to smear across the x0 to x1 stretch, in the order they are needed
	 */
	static void renderScanline(double x0, double x1, LaserNodeSettings settings, JobRenderTarget target, PointTransformation transformation,
                               boolean reverse, double y, double leadin, double yStep, boolean[] pixels) {
				
		if (reverse) {
			double x = x1; x1 = x0; x0 = x;
			leadin *= -1;
			
			for(int i = 0; i < pixels.length/2; i++) {
			    val temp = pixels[i];
			    pixels[i] = pixels[pixels.length - i - 1];
			    pixels[pixels.length - i - 1] = temp;
			}
		}
		
		/* Note: We use cutTo for the lead-in/out lines, this is because we need to hit the desired getRasterSpeed(),
		 * not the maximum speed of the machine as moveTo would do. 
		 */
		
		target.moveTo(       transformation.transform(new Point2D.Double(x0-leadin, y)),-1); // Will be optimized out for every line except the first.
		target.moveToAtSpeed(transformation.transform(new Point2D.Double(x0,        y)), settings.getRasterSpeed());
		target.engraveTo(    transformation.transform(new Point2D.Double(x1, y)), settings, pixels);
		target.moveTo(       transformation.transform(new Point2D.Double(x1+leadin, y+yStep)), settings.getRasterSpeed());
	}
	
	static void rasterize(JobNodeGroup root, PointTransformation pointTransformation, JobRenderTarget target) {

		for (val eg : RasterGroup.getRasterGroups(root, target)) {
			

            // Make sure the line pitch is a whole number of y-steps or we'll end up with a shitty looking raster
            double linePitch = Math.max(target.getEngravingYStepSize(),
                    target.getEngravingYStepSize() * Math.round(eg.settings.getRasterLinePitch()/target.getEngravingYStepSize()));

            double pixelPitch = linePitch; // TODO: This means that we have square pixels, do we want that or should we also have a pixelPitch setting?

            BufferedImage onebit = combinedOneBitRaster(eg, linePitch, pixelPitch);


            /**
		     * For each line of the raster we need to figure out the first and last black pixel, as those positions are needed to calculate 
		     * how wide this particular scanline is, if it doesn't have any black pixels at all, then we need to skip the line entirely.
		     */
			target.setAssistAir(eg.settings.isAssistAir());
			
			double leadin = target.getEngravingXAccelerationDistance(eg.settings.getRasterSpeed());

            target.startShape("rastergroup-"+eg.bb.toString());

			double y = eg.bb.getY();
			int scanNumber = 0;
			for (int yc=0;yc<onebit.getHeight();yc++) {
				
				
				int firstSet = onebit.getWidth();
				int lastSet = -1;
				for (int xc=0; xc<onebit.getWidth(); xc++) {
					int pixel = onebit.getRGB(xc, yc) & 0xffffff;
					
					
					if (pixel == 0) {
						lastSet = xc;
						if (firstSet > lastSet) {
							firstSet = lastSet;
						}
					}
				}
				
				if (lastSet < 0) {
					y += linePitch;
					continue; // Skip empty lines entirely
				}
				
				double x0 = firstSet*pixelPitch + eg.bb.getX();
				double x1 = lastSet *pixelPitch + eg.bb.getX();

				lastSet++; 

				val pixels = new boolean[lastSet-firstSet];
				for (int xc=firstSet; xc<lastSet; xc++) {
					if ((onebit.getRGB(xc, yc) & 0xffffff) == 0) {
						pixels[xc-firstSet] = true;					
					}
				}
				
				if (eg.settings.getPasses() > 1) {
					for (int pass=1;pass<eg.settings.getPasses();pass++) {
						renderScanline(x0, x1, eg.settings, target, pointTransformation, (scanNumber++ & 1) == 1, y, leadin, 0, pixels);
					}					
				}
				
				renderScanline(x0, x1, eg.settings,target, pointTransformation, (scanNumber++ & 1) == 1, y, leadin, linePitch, pixels);
				
				y += linePitch;
			}    
			target.startShape("end-rastergroup-"+eg.bb.toString());
		}	
	}

    private static BufferedImage combinedOneBitRaster(RasterGroup eg, double linePitch, double pixelPitch) {
        // The bb is now in mm, so we need to figure out what resolution we want the raster to be in.
        int pixelWidth  = (int)Math.round(eg.bb.getWidth()/pixelPitch);
        int pixelHeight = (int)Math.round(eg.bb.getHeight()/linePitch);

        BufferedImage ri = new BufferedImage(pixelWidth, pixelHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = ri.createGraphics();

        g.setColor(Color.WHITE);
        g.fillRect(0, 0, ri.getWidth(), ri.getHeight());
        g.setClip( 0, 0, ri.getWidth(), ri.getHeight());
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        for (val rn : eg.nodes) {
            AffineTransform toImage = AffineTransform.getScaleInstance(1/pixelPitch, 1/linePitch);
            toImage.concatenate(AffineTransform.getTranslateInstance(-eg.bb.getX(), -eg.bb.getY()));
            toImage.concatenate(rn.getTransformation());

            g.drawImage(rn.getImage(), toImage, null);
        }

	        /*
	         * We now have all the images in the group rendered into the same huge raster, which has a simple bounding box, but it's in color
	         * and we need 1-bit per pixel for the laser, so we need to run it though some dithering.
	         */
			/*
			try {
			    File outputfile = new File("/tmp/saved.png");
				log.info("Writing image that is "+ri.getWidth()+" x "+ri.getHeight()+" pixels");
			    ImageIO.write(ri, "png", outputfile);
			} catch (IOException e) {
			    log.log(Level.SEVERE, "Fail!", e);
			}
			*/

        log.info("Starting dither of image "+ri.getWidth()+" x "+ri.getHeight()+" pixels at "+eg.bb.getMinX()+","+eg.bb.getMinY()+" w/h: "+eg.bb.getWidth()+"/"+eg.bb.getHeight());
        BufferedImage onebit = DitherFloydSteinberg.dither(ri);
        log.info("Done with dither");

			/*
			try {
			    File outputfile = new File("/tmp/onebit.png");
			    ImageIO.write(onebit, "png", outputfile);
			} catch (IOException e) {
			    log.log(Level.SEVERE, "Fail!", e);
			}
			*/
        return onebit;
    }
}
