package dk.osaa.psaw.mover;

import java.io.IOException;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.logging.Level;

import lombok.extern.java.Log;

/**
 * This is the code that keeps state about the machine and maintains LineBuffer by weeding
 * out already processed Moves and adding new Jobs to the buffer  
 * 
 * @author ff
 */
@Log
public class Planner extends Thread {
	
	Job currentJob;
		
	LineBuffer lineBuffer = new LineBuffer();
	Point lastBufferedLocation;
	boolean homed[] = new boolean[Move.AXES];
	Commander commander;	
	private PhotonSaw photonSaw;

	public Planner(PhotonSaw photonSaw) {
		this.photonSaw = photonSaw;
		
		setDaemon(true);
		setName("Planner thread");				
	}
	
	/**
	 * Starts a new job, will fail if 
	 * @param newJob The job to start.
	 */
	public void startJob(Job newJob) {
		if (currentJob != null) {
			throw new RuntimeException("Cannot start new job while another one is running");
		}
		currentJob = newJob;
	}
	
	public Job getCurrentJob() {
		return currentJob;
	}
	
	void addLine(Point endPoint, double maxSpeed) {
		endPoint.axes[2] = endPoint.axes[1]/200 + endPoint.axes[2]/200;  
		double l = lastBufferedLocation != null ? Math.sqrt(Math.pow(endPoint.axes[0]-lastBufferedLocation.axes[0], 2) + Math.pow(endPoint.axes[1]-lastBufferedLocation.axes[1], 2)) : 1000;
		if (l > 0.025) { // Discard all lines that are too small to actually cause a move.	
			Line line = new Line(photonSaw.mc, 
								lineBuffer.getList().size()>0 ? lineBuffer.getList().get(lineBuffer.getList().size()-1) : null,
								endPoint, maxSpeed);
			lineBuffer.push(line);
			lastBufferedLocation = endPoint;
		}
	}
		
	void recalculate() {
		
		// Reverse pass, with a reference to the next move:
		{
			Line next = null;
			for (int i=lineBuffer.getList().size()-1;i>=0;i--) {
				Line line = lineBuffer.getList().get(i);
				line.reversePass(next);
				next = line; 
			}
		}
		
		// And a forward pass as well...
		Line prev = null;
		for (Line line : lineBuffer.getList()) {
			line.forwardPass(prev);
			prev = line;
		}
		
		// Recalculates the trapezoid speed profiles for flagged blocks in the plan according to the
		// entry_speed for each junction and the entry_speed of the next junction. Must be called by
		// planner_recalculate() after updating the blocks. Any recalculate flagged junction will
		// compute the two adjacent trapezoids to the junction, since the junction speed corresponds
		// to exit speed and entry speed of one another.
		Line current = null;
		for (Line next: lineBuffer.getList()) {
			if (current != null) {
				if (current.recalculate || next.recalculate) {
					current.calculateTrapezoid(next);
				}
			}			
			current = next;
		}
		current.calculateTrapezoid(null); // Stop when this is done.
	}

	public void runTest() throws IOException, ReplyTimeout, InterruptedException {
	
		Point p1 = new Point();
		p1.axes[0] = 0;
		p1.axes[1] = 0;
		addLine(p1, 1000);

		Point p3 = new Point();
		p3.axes[0] = 30;
		p3.axes[1] = 60;
		addLine(p3, 1000);
		
		for (int i=1;i<60+1;i++) {
			Point p2 = new Point();
			p2.axes[0] = 0.2*i;
			p2.axes[1] = 1.4*i;
			addLine(p2, 1000);
		}
				
		final int N = 50;
		for (int i=0;i<N*10;i++) {
			Point p = new Point();
			p.axes[0] = ((i)/N)*30*Math.sin((i*Math.PI*2)/N);
			p.axes[1] = ((i)/N)*60*Math.cos((i*Math.PI*2)/N);
			addLine(p, 1000);
		}
		
		addLine(p1, 1000);		

		recalculate();
		
		for (Line line : lineBuffer.getList()) {
			line.toMoves(photonSaw);	
		}
		Move.dumpProfile();	
	}
	
	public void run() {
		try {
			runTest();
		} catch (Exception e) {
			log.log(Level.SEVERE, "Failed while planning moves", e);
		}
	}
}
