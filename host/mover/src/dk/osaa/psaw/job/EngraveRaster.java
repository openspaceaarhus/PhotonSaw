/**
 * 
 */
package dk.osaa.psaw.job;

import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.awt.image.DataBufferByte;

import lombok.val;

/**
 * Engraves (or cuts depending on speed and power setting, but that would be silly) a raster image
 * 
 * @author Flemming Frandsen <dren.dk@gmail.com> <http://dren.dk>
 */
public class EngraveRaster extends LaserNode {

	/*
	 * Where to put the raster in the real world and what size to give it
	 */
	double xOffset;
	double yOffset;
	double width;
	double height;

	/*
	 * The pixels themselves
	 */
	byte[] raster;   
	int rasterHeight;
	int rasterWidth;
	int bytesPerLine;
	
	void setPixel(int x, int y, boolean set) {
		if (set) {
			raster[y*bytesPerLine + (x >> 3)] |= 1<<(x & 7);
		} else {
			raster[y*bytesPerLine + (x >> 3)] &=~ (1<<(x & 7));
		}
	}

	boolean getPixel(int x, int y) {
		if (x < 0 || x >= rasterWidth) {
			throw new RuntimeException("x out of bounds: 0<="+x+"<"+rasterWidth);
		}
		if (y < 0 || y >= rasterHeight) {
			throw new RuntimeException("y out of bounds: 0<="+y+"<"+rasterHeight);
		}
		
 		return 0 != (raster[y*bytesPerLine + (x >> 3)] & (1<<(x & 7)));
	}
	
	void setRaster(BufferedImage image) {
		BufferedImage  img = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
        ColorConvertOp xformOp = new ColorConvertOp(null);
        xformOp.filter(image, img);

        byte[] gray = ( (DataBufferByte) img.getRaster().getDataBuffer() ).getData();

        rasterHeight = img.getHeight();
        rasterWidth = img.getWidth();
        bytesPerLine = (int)Math.ceil(rasterWidth/8.0);
        raster = new byte[bytesPerLine*rasterHeight];
        
        int srcIndex = 0;
        for (int y=0;y<img.getHeight();y++) {
        	int bitValue = 1;
        	int tgtIndex = y*bytesPerLine;
            for (int x=0;x<img.getWidth();x++) {
            	if (gray[srcIndex] >= 0) {
            		raster[tgtIndex] |= bitValue;
            	}

            	bitValue <<= 1;
            	if (bitValue >= 256) {
            		bitValue = 1;
            		tgtIndex++;
            	}
            	srcIndex++;
            }
        }
	}
	
	/*
	@SuppressWarnings("unused")
	private EngraveRaster() {};
	*/	
	
	public EngraveRaster(String id, LaserNodeSettings settings,
						BufferedImage img, double x, double y, double width, double height) {
		super(id, settings);

		setRaster(img);
        
        xOffset = x;
        yOffset = y;
        this.width = width;
        this.height = height;
	}	
	
	/**
	 * 
	 * @param x0 The start of the engraving line
	 * @param x1 The end of the engraving line
	 * @param y The line to engrave on
	 * @param leadin The length to leave on either side of the engraving line for acceleration
	 * @param yStep The height of the line, the deceleration line will move down this amount  
	 * @param pixels The pixels to smear across the x0 to x1 stretch, in the order they are needed
	 */
	void renderScanline(JobRenderTarget target, PointTransformation transformation,
			boolean reverse, double y, double leadin, double yStep, boolean[] pixels) {
		
		double x0 = xOffset;
		double x1 = xOffset+width;
		
		if (reverse) {
			double x = x1; x1 = x0; x0 = x;
			leadin *= -1;
			
			for(int i = 0; i < pixels.length/2; i++) {
			    val temp = pixels[i];
			    pixels[i] = pixels[pixels.length - i - 1];
			    pixels[pixels.length - i - 1] = temp;
			}
		}
		
		/* Note: We use cutTo for the lead-in/out lines, this is because we need to hit the desired maxSpeed,
		 * not the maximum speed of the machine as moveTo would do. 
		 */
		
		target.moveTo(transformation.transform(new Point2D(x0-leadin, y))); // Will be optimized out for every line except the first.
		target.moveToAtSpeed(transformation.transform(new Point2D(x0, y)), settings.maxSpeed);
//		target.cutTo(transformation.transform(new Point2D(x0, y)), 0, settings.maxSpeed);

		target.engraveTo(transformation.transform(new Point2D(x1, y)), settings.intensity, settings.maxSpeed, pixels);

		//		target.moveTo(transformation.transform(new Point2D(x1+leadin, y+yStep))); // stopping move, so we don't care about what speed it's at.
		target.cutTo(transformation.transform(new Point2D(x1+leadin, y+yStep)), 0, settings.maxSpeed);
	}
	

	@Override
	public void render(JobRenderTarget target, PointTransformation transformation) {

		target.setAssistAir(settings.assistAir);
		
		double yStep = target.getEngravingYStepSize();
		double leadin = target.getEngravingXAccelerationDistance(settings.maxSpeed);
		
		/*
		 * Note: We always scan in the X direction at the speed wanted, acceleration moves
		 * are appended to each end of the scan, so enough room must be available.
		 */

		int scanlines = (int)Math.floor(height / yStep);

		if (transformation.rotation == PointTransformation.Rotation.NORMAL || 
			transformation.rotation == PointTransformation.Rotation.DOWN) {

			double rasterLinesPerScanLine = ((double)rasterHeight-1) / scanlines;
			double rasterLine;
			if (transformation.rotation == PointTransformation.Rotation.DOWN) {
				rasterLine = rasterHeight-1;
				rasterLinesPerScanLine *= -1;
			} else {
				rasterLine = 0;
			}			
			
			double y = yOffset;
			int scanNumber = 0;
			for (int yc=0;yc<scanlines;yc++) {
				
				val pixels = new boolean[rasterWidth];				
				for (int xc=0;xc<rasterWidth;xc++) {
					pixels[xc] = getPixel(xc, (int)Math.round(rasterLine));  
				}				
				
				if (settings.passes > 1) {
					for (int pass=1;pass<settings.passes;pass++) {
						renderScanline(target, transformation, (scanNumber++ & 1) == 1, y, leadin, 0, pixels);
					}					
				}
				
				renderScanline(target, transformation, (scanNumber++ & 1) == 1, y, leadin, yStep, pixels);
				
				y += yStep;
				rasterLine += rasterLinesPerScanLine; 
			}

		} else /* if (transformation.rotation == PointTransformation.Rotation.LEFT 
		           || transformation.rotation == PointTransformation.Rotation.RIGHT) */ {
			
			double rasterPixelsPerScanLine = ((double)rasterWidth-1) / scanlines;
			double rasterPixel;
			if (transformation.rotation == PointTransformation.Rotation.LEFT) {
				rasterPixel = rasterWidth-1;
				rasterPixelsPerScanLine *= -1;
			} else {
				rasterPixel = 0;
			}			
			
			double y = yOffset;
			for (int xc=0;xc<scanlines;xc++) {
				
				val pixels = new boolean[rasterHeight];				
				for (int yc=0;yc<rasterHeight;yc++) {
					pixels[xc] = getPixel((int)Math.round(rasterPixel), yc);  
				}				
				
				renderScanline(target, transformation, (xc & 1) == 1, y, leadin, yStep, pixels);
				
				y += yStep;
				rasterPixel += rasterPixelsPerScanLine; 
			}
		}		
	}
}
