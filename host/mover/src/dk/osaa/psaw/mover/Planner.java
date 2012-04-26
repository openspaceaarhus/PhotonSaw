package dk.osaa.psaw.mover;

import java.io.IOException;
import java.util.ArrayList;
import java.util.TreeMap;

import lombok.extern.java.Log;

/**
 * This is the code that keeps state about the machine and maintains LineBuffer by weeding
 * out already processed Moves and adding new Jobs to the buffer  
 * 
 * @author ff
 */
@Log
public class Planner {
	
	MovementConstraints mc = new MovementConstraints(); // TODO: Load from a config file in stead.
	ArrayList<Line> lineBuffer = new ArrayList<Line>();
	ArrayList<Move> moveBuffer = new ArrayList<Move>();
	
	Point lastBufferedLocation = new Point();
	boolean homed[] = new boolean[Move.AXES];
	Commander commander;
	private PhotonSaw photonSaw;

	void addLine(Point endPoint, double maxSpeed) {
		Line line = new Line(mc, 
							lineBuffer.size()>0 ? lineBuffer.get(lineBuffer.size()-1) : null,
							endPoint, maxSpeed);
		lineBuffer.add(line);
		lastBufferedLocation = endPoint;
	}
	
	
	public Planner(PhotonSaw photonSaw) {
		this.photonSaw = photonSaw;
	}
	
	void recalculate() {
		
		// Reverse pass, with a reference to the next move:
		{
			Line next = null;
			for (int i=lineBuffer.size()-1;i>=0;i--) {
				Line line = lineBuffer.get(i);
				line.reversePass(next);
				next = line; 
			}
		}
		
		// And a forward pass as well...
		Line prev = null;
		for (Line line : lineBuffer) {
			line.forwardPass(prev);
			prev = line;
		}
		
		// Recalculates the trapezoid speed profiles for flagged blocks in the plan according to the
		// entry_speed for each junction and the entry_speed of the next junction. Must be called by
		// planner_recalculate() after updating the blocks. Any recalculate flagged junction will
		// compute the two adjacent trapezoids to the junction, since the junction speed corresponds
		// to exit speed and entry speed of one another.
		Line current = null;
		for (Line next: lineBuffer) {
			if (current != null) {
				if (current.recalculate || next.recalculate) {
					current.calculateTrapezoid(next);
				}
			}			
			current = next;
		}
		current.calculateTrapezoid(null); // Stop when this is done.
	}


	public void runTest() throws IOException, ReplyTimeout {
		// TODO: Implement homing.		
		homed[0] = true;
		homed[1] = true;
	
		Point p1 = new Point();
		p1.axes[0] = 0;
		p1.axes[1] = 0;

		Point p2 = new Point();
		p2.axes[0] = 0;
		p2.axes[1] = 60;
		for (int i=0;i<10;i++) {
			addLine(p1, 1000);
			addLine(p2, 1000);
		}
/*
		
		final int N = 20;
		for (int i=0;i<N;i++) {
			Point p = new Point();
			p.axes[0] = 30*Math.sin((i*Math.PI*2)/N);
			p.axes[1] = 30*Math.cos((i*Math.PI*2)/N);
			addLine(p, 1000);
		}
	*/	
		recalculate();
		
		for (Line line : lineBuffer) {
			line.toMoves(moveBuffer);
		}		
		photonSaw.getCommander().bufferMoves(moveBuffer);		
	}
}
