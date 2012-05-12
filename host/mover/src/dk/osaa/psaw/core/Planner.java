package dk.osaa.psaw.core;

import java.util.logging.Level;

import dk.osaa.psaw.job.Job;
import dk.osaa.psaw.job.PointTransformation;
import dk.osaa.psaw.job.JobRenderTarget;
import dk.osaa.psaw.machine.Commander;
import dk.osaa.psaw.machine.Move;
import dk.osaa.psaw.machine.Point;

import lombok.extern.java.Log;

/**
 * This is the code that keeps state about the machine and maintains LineBuffer by weeding
 * out already processed Moves and adding new Jobs to the buffer  
 * 
 * @author ff
 */
@Log
public class Planner extends Thread implements JobRenderTarget {
	
	Job currentJob;
		
	LineBuffer lineBuffer = new LineBuffer();
	Point lastBufferedLocation;
	boolean homed[] = new boolean[Move.AXES];
	boolean usedAxes[] = new boolean[Move.AXES];
	Commander commander;	
	private PhotonSaw photonSaw;

	public Planner(PhotonSaw photonSaw) {
		this.photonSaw = photonSaw;
		
		setDaemon(true);
		setName("Planner thread");
		
		// TODO: We need to always have a position, so ask the hardware where we are in stead of making shit up. 
		lastBufferedLocation = new Point();
		for (int i=0;i<Move.AXES;i++) {
			lastBufferedLocation.getAxes()[i] = 0;
		}	
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
	
	public void run() {
		try {
			while (true) {
				if (getCurrentJob() != null) {

					// Unlock only the axes that are used by this job.
					Point startPoint = lastBufferedLocation;
					usedAxes = getCurrentJob().getUsedAxes();
					getCurrentJob().render(this);
					moveTo(startPoint); // Go back to where we were before the job.
					
					log.info("Job has finished rendering, waiting for buffer to empty");
					// Let the line buffer empty out before we continue
					while (!lineBuffer.isEmpty()) {
						Line ready = lineBuffer.shift();
						ready.toMoves(photonSaw); // This will block if the move buffer is full.
					}
					
					log.info("LineBuffer empty, waiting for MoveQueue to empty");
					// Wait until all moves have been sent to the hardware
					while (!photonSaw.moveQueue.isEmpty()) {
						Thread.sleep(100); // Don't burn all the CPU while waiting						
					}
					
					log.info("MoveQueue to empty, waiting for hardware to go idle");
					// Wait for the hardware to process all the buffered moves
					while (photonSaw.active()) {
						Thread.sleep(250); // Don't burn all the CPU while waiting						
					}
					
					log.info("Hardware idle, Job done");
					currentJob = null;
				}
				Thread.sleep(1000); // Don't burn all the CPU if idle.
			}					
		} catch (Exception e) {
			log.log(Level.SEVERE, "Failed while planning moves", e);
		}
	}
	
	void addLine(Line line) {
		lineBuffer.push(line);
		lastBufferedLocation = line.getEndPoint(); // because the Line constructor rounds the point to the actual position.
		recalculate();

		while (lineBuffer.isFull()) {
			Line ready = lineBuffer.shift();
			try {
				ready.toMoves(photonSaw); // This will block if the move buffer is full.
			} catch (InterruptedException e) {
				log.log(Level.WARNING, "Ignoring exeception from buffering moves", e);
			}
		}
	}

	@Override
	public void moveTo(Point p) {
		for (int i=0;i<Move.AXES;i++) {
			if (!usedAxes[i]) {
				p.axes[i] = lastBufferedLocation.axes[i]; 
			}
		}
		Line line = new Line(photonSaw.mc, 
							lineBuffer.getList().size()>0 ? lineBuffer.getList().get(lineBuffer.getList().size()-1) : null,
							lastBufferedLocation, p, photonSaw.mc.getRapidMoveSpeed());
		if (line.getLength() > photonSaw.mc.getShortestMove()) {
			addLine(line);
		}
	}

	@Override
	public void cutTo(Point p, double intensity, double maxSpeed) {
		for (int i=0;i<Move.AXES;i++) {
			if (!usedAxes[i]) {
				p.axes[i] = lastBufferedLocation.axes[i]; 
			}
		}
		Line line = new Line(photonSaw.mc, 
				lineBuffer.getList().size()>0 ? lineBuffer.getList().get(lineBuffer.getList().size()-1) : null,
				lastBufferedLocation, p, maxSpeed);
		if (line.getLength() > photonSaw.mc.getShortestMove()) {
			line.setLaserIntensity(intensity);
			addLine(line);
		}
	}

	@Override
	public void engraveTo(Point p, double intensity, double maxSpeed,
			boolean[] pixels) {

		for (int i=0;i<Move.AXES;i++) {
			if (!usedAxes[i]) {
				p.axes[i] = lastBufferedLocation.axes[i]; 
			}
		}
		Line line = new Line(photonSaw.mc, 
				lineBuffer.getList().size()>0 ? lineBuffer.getList().get(lineBuffer.getList().size()-1) : null,
				lastBufferedLocation, p, maxSpeed);
		if (line.getLength() > photonSaw.mc.getShortestMove()) {
			line.setLaserIntensity(intensity);
			//line.setPixels(pixels); TODO: Do something different for engraving.
			addLine(line);
		}
	}

	@Override
	public double getEngravingXAccelerationDistance(double speed) {
		// This doesn't take minSpeed into account, so there should be some head room.
		return Line.estimateAccelerationDistance(0, speed, photonSaw.mc.getAxes()[0].acceleration);
	}

	@Override
	public double getEngravingYStepSize() {
		if (getCurrentJob().getRootTransformation().getAxisMapping() == PointTransformation.AxisMapping.XY) {
			return photonSaw.mc.getAxes()[1].mmPerStep;

		} else if (getCurrentJob().getRootTransformation().getAxisMapping() == PointTransformation.AxisMapping.XA) {
			return photonSaw.mc.getAxes()[3].mmPerStep;
			
		} else {
			throw new RuntimeException("New AxisMapping not implemented");			
		}
	}

}
