/**
 * 
 */
package dk.osaa.psaw.job;

import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.awt.image.DataBufferByte;

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
		return 0 != (raster[y*bytesPerLine + (x >> 3)] & 1<<(x & 7));
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
	
	public EngraveRaster(String id, double intensity, double maxSpeed,
						BufferedImage img, double x, double y, double width, double height) {
		super(id,intensity,maxSpeed);

		setRaster(img);
        
        xOffset = x;
        yOffset = y;
        this.width = width;
        this.height = height;
	}	

	@Override
	public void render(JobRenderTarget target, PointTransformation transformation) {
		// TODO: Something, anything!
	}
}
