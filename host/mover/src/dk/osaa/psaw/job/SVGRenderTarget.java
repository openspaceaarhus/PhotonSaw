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

	
	ArrayList<Point> path;
	
	Writer out;
	
	public SVGRenderTarget(File outFile) {
		try {
			out = new FileWriter(outFile);
			out.write("<svg>\n");
		} catch (IOException e) {
			log.log(Level.SEVERE, "Fail", e);
			throw new RuntimeException(e);
		}
		
		path = new ArrayList<Point>();		

	}
	
	void outputPath() {
		if (path.size() == 0) {
			return;
		}
		
		try {
			out.write("  <path style=\"stroke:#000000;stroke-width:1;fill:none\" d=\"");
			String l = "M";
			for (Point p : path) {
//				out.write(l+p.axes[0]+","+p.axes[1]);
				out.write(l+p.axes[0]*3.5+","+p.axes[1]*3.5);
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
		outputPath();
		path.add(p);		
	}

	@Override
	public void cutTo(Point p, double intensity, double maxSpeed) {
		path.add(p);		
	}

	@Override
	public void moveToAtSpeed(Point p, double maxSpeed) {
		path.add(p);		
	}

	@Override
	public void engraveTo(Point p, double intensity, double maxSpeed, boolean[] pixels) {
		// TODO Auto-generated method stub
	}

	@Override
	public double getEngravingXAccelerationDistance(double speed) {
		return 10;
	}

	@Override
	public double getEngravingYStepSize() {
		return 1;
	}

	@Override
	public void setAssistAir(boolean assistAirOn) {
		// Meh.
	}
}
