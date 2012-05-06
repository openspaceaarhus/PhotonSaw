package com.kitfox.svg;

/**
 * Interface that can be implemented by the Graphics2D object
 * passed to SVGDiagram.render() 
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
}
