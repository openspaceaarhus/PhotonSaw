package com.kitfox.svg;

/**
 * Interface that can be implemented by the Graphics2D object
 * passed to SVGDiagram.render() to enable SVGSalamander specific
 * functionality during rendering.
 * 
 * @author Flemming Frandsen <dren.dk@gmail.com> <http://dren.dk>
 */
public interface SVGGraphics2D {
	/**
	 * Notifies the Graphics2D target about where the graphics calls come from
	 * in the SVG document.
	 * 
	 * This feature can be used to extract more information from the source svg
	 * element than what is available via the normal Graphics2D interface.
	 *  
	 * @param element the next element that's going to be rendered
	 */
	void startRendering(RenderableElement element);
	
	/**
	 * SVGSalamander usually constructs a Shape which is an outline of the stroke, by returning
	 * true here it will in stead call setStroke and draw that stroke using draw.
	 * 
	 * Using draw is less accurate if you care about having the output look correct on a raster display,
	 * but it is also more accurate if you care about the exact path and it's potentially much faster.    
	 * 
	 * @return true if you prefer to get a call to draw in stead of fill for outlines
	 */
	boolean useDrawInSteadOfFillForStroke();
	
}
