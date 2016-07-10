package dk.osaa.psaw.job;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.logging.Level;

import lombok.extern.java.Log;

import dk.osaa.psaw.machine.Point;

@Log
public class SVGRenderTarget implements JobRenderTarget {


	private static final double MMTOPX = 90.0/25.4;
	private int idcnt = 0;
	
	ArrayList<Point> path;
	Writer out;
	String pathClass = "first";

	private String id;
	
	public SVGRenderTarget(File outFile) {
		try {
			out = new FileWriter(outFile);
			out.write("<svg>\n" +
					"<defs>\n" +
					"<style type=\"text/css\"><![CDATA[\n" +
					"  .first  { fill:none; stroke:#f00; stroke-width: 0.1; }\n" +
					"  .move   { fill:none; stroke:#0f0; stroke-width: 0.1; }\n" +
					"  .cut    { fill:none; stroke:#000; stroke-width: 1; }\n" +
					"  .eng    { fill:none; stroke:#008; stroke-width: 1; }\n" +
					"  .dead   { fill:none; stroke:#f00; stroke-width: 0.5; }\n" +
					"  .leadin { fill:none; stroke:#aa0; stroke-width: 0.5; }\n" +
					"]]></style>\n" +
					"\t</defs>" +
					"");
		} catch (IOException e) {
			log.log(Level.SEVERE, "Fail", e);
			throw new RuntimeException(e);
		}
		
		path = new ArrayList<Point>();
		Point pp = new Point();
		pp.axes[0] = 0.0;
		pp.axes[1] = 0.0;
		path.add(pp);
	}
	
	void outputPath() {
		if (path.size() == 0) {
			return;
		}

		try {
			out.write("  <path class=\""+pathClass+"\" id=\"path"+idcnt++ +"\" d=\"");
			String l = "M";
			for (Point p : path) {
				out.write(l+p.axes[0]*MMTOPX+","+p.axes[1]*MMTOPX);
				l = " L";
			}
			out.write("\"/>\n");
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		path.clear();
	}
	
	public void done() throws IOException {
		Point pp = new Point();
		pp.axes[0] = 0.0;
		pp.axes[1] = 0.0;
		moveTo(pp, -1);
		outputPath();
		out.write("</svg>\n");
		out.close();
	}
	
	@Override
	public void engraveTo(Point p,  LaserNodeSettings settings, boolean[] pixels) {
		Point lastpoint = path.get(path.size()-1);
		double dx = p.axes[0] - lastpoint.axes[0];
		double dy = p.axes[1] - lastpoint.axes[1];
		int i;
		boolean lastpix = pixels[0];
		int lastpixidx = 0;
		for(i = 1; i < pixels.length; i++) {
			if(pixels[i] == lastpix)
				continue;
			Point pp = new Point();
			pp.axes[0] = lastpoint.axes[0] + (double)i*dx/(double)pixels.length;
			pp.axes[1] = lastpoint.axes[1] + (double)i*dy/(double)pixels.length;
			if (lastpix) {
				encodePathTo(pp, "eng");
			} else {
				encodePathTo(pp, "dead");
			}
			lastpix = pixels[i];
			lastpixidx = i;
		}
		if(lastpixidx != pixels.length) {
			if(lastpix) {
				encodePathTo(p, "eng");
			} else {
				encodePathTo(p, "dead");
			}
		}
	}

	@Override
	public double getEngravingYStepSize() {
		return 0.0375;
	}

	@Override
	public void setAssistAir(boolean assistAirOn) {
		// Meh.
	}
	@Override
	public void startShape(String id) {
		this.id = id;
	}

	@Override
	public void moveToAtSpeed(Point p, double maxSpeed) {
		encodePathTo(p, "leadin");
	}

	@Override
	public void cutTo(Point p, LaserNodeSettings settings) {
		if (settings.getIntensity() == 0.0) {
			encodePathTo(p, "dead");
		} else {
			encodePathTo(p, "cut");
		}
	}

	@Override
	public void moveTo(Point p, double maxSpeed) {
		encodePathTo(p, "move");
	}

	public void moveTo(Point p) {
		encodePathTo(p, "dead");
	}

	void encodePathTo(Point p, String style) {
		if (!pathClass.equals(style)) {
			Point pp = path.get(path.size()-1);
			outputPath();
			path.add(pp);
		}
		pathClass = style;
		path.add(p);
	}
}
