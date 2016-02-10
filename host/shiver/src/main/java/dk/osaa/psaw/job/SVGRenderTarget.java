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

	private static final double LWMOVE = 0.1;
	private static final double LWCUT = 1.0;
	private static final double LWENG = 0.2;
	private static final double MMTOPX = 90.0/25.4;
	private int idcnt = 0;
	
	ArrayList<Point> path;
	double linewidth;
	boolean firstmove;
	
	Writer out;

	private String id;
	
	public SVGRenderTarget(File outFile) {
		try {
			out = new FileWriter(outFile);
			out.write("<svg>\n");
		} catch (IOException e) {
			log.log(Level.SEVERE, "Fail", e);
			throw new RuntimeException(e);
		}
		
		path = new ArrayList<Point>();
		Point pp = new Point();
		pp.axes[0] = 0.0;
		pp.axes[1] = 0.0;
		path.add(pp);
		linewidth = LWMOVE;
		firstmove = true;
	}
	
	void outputPath() {
		if (path.size() == 0) {
			return;
		}
		
		try {
			if(firstmove) {
				out.write("  <path style=\"stroke:#ff0000;stroke-width:"+linewidth+";fill:none\" id=\"path"+idcnt++ +"\" d=\"");
				firstmove = false;
			} else
				out.write("  <path style=\"stroke:#000000;stroke-width:"+linewidth+";fill:none\" id=\"path"+idcnt++ +"\" d=\"");
			String l = "M";
			for (Point p : path) {
//				out.write(l+p.axes[0]+","+p.axes[1]);
				out.write(l+p.axes[0]*MMTOPX+","+p.axes[1]*MMTOPX);
				l = " L";
			}
			out.write("\"/>\n");
			
		} catch (IOException e) {
			log.log(Level.SEVERE, "Fail", e);
			throw new RuntimeException(e);
		}
		
		path.clear();
	}
	
	public void done() {
		Point pp = new Point();
		pp.axes[0] = 0.0;
		pp.axes[1] = 0.0;
		moveTo(pp);
		outputPath();
		try {
			out.write("</svg>\n");
			out.close();
		} catch (IOException e) {
			log.log(Level.SEVERE, "Fail", e);
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public void moveTo(Point p) {
		if(linewidth != LWMOVE) {
			Point pp = path.get(path.size()-1);
			outputPath();
			path.add(pp);		
		}
		linewidth = LWMOVE;
		path.add(p);		
	}

	@Override
	public void cutTo(Point p, LaserNodeSettings settings) {
		if (settings.getIntensity() == 0.0) {
			moveTo(p);
		} else {
			if(linewidth != LWCUT) {
				Point pp = path.get(path.size()-1);
				outputPath();
				path.add(pp);		
			}
			linewidth = LWCUT;
			path.add(p);
		}
	}

	@Override
	public void moveToAtSpeed(Point p, double maxSpeed) {
		moveTo(p);
	}

	private void doEngraveTo(Point p) {
		if(linewidth != LWENG) {
			Point pp = path.get(path.size()-1);
			outputPath();
			path.add(pp);		
		}
		linewidth = LWENG;
		path.add(p);				
	}
	
	@Override
	public void engraveTo(Point p, double intensity, double maxSpeed, boolean[] pixels) {
		// TODO Auto-generated method stub
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
			if(lastpix) {
				doEngraveTo(pp);
			} else {
				moveTo(pp);
			}
			lastpix = pixels[i];
			lastpixidx = i;
		}
		if(lastpixidx != pixels.length) {
			if(lastpix)
				doEngraveTo(p);
			else
				moveTo(p);
		}
	}

	@Override
	public double getEngravingXAccelerationDistance(double speed) {
		return 10;
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
	public void moveTo(Point p, double maxSpeed) {
		moveTo(p);
	}
}
