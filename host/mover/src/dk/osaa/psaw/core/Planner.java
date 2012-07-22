package dk.osaa.psaw.core;

import java.util.logging.Level;

import dk.osaa.psaw.config.MovementConstraints;
import dk.osaa.psaw.job.Job;
import dk.osaa.psaw.job.PointTransformation;
import dk.osaa.psaw.job.JobRenderTarget;
import dk.osaa.psaw.machine.Commander;
import dk.osaa.psaw.machine.Move;
import dk.osaa.psaw.machine.MoveVector;
import dk.osaa.psaw.machine.Point;
import dk.osaa.psaw.machine.Q16;

import lombok.Getter;
import lombok.val;
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
	
	MoveVector jogDirection;
	long jogTimeout;
	long jogId;
	
	@Getter
	double currentJobLength;
	
	@Getter
	int currentJobSize;
	
	@Getter
	int renderedLines;

	public Planner(PhotonSaw photonSaw) {
		this.photonSaw = photonSaw;
		
		setDaemon(true);
		setName("Planner thread");
		
		// TODO: We need to always have a position, so ask the hardware where we are in stead of making shit up. 
		lastBufferedLocation = new Point();
		for (int i=0;i<Move.AXES;i++) {
			lastBufferedLocation.getAxes()[i] = 0;
		}
		
		jogTimeout = 0;
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
	
	Move endcodeJogMove(MoveVector mmMoveVector, double speedMMS) {		
		
		MovementConstraints mc = photonSaw.cfg.movementConstraints;
		
		val stepVector = mmMoveVector.div(mc.mmPerStep()).round(); // move vector in whole steps
		val unitVector = mmMoveVector.unit();
		MoveVector startSpeedVector = unitVector.mul(speedMMS/mc.getTickHZ()).div(mc.mmPerStep()); // convert from scalar mm/s to vector step/tick

		// Find the longest axis, so we can use it for calculating the duration of the move, this way we get better accuracy.
		int longAxis = 0;
		double longAxisLength = -1;
		for (int i=0;i<Move.AXES;i++) {
			if (Math.abs(stepVector.getAxis(i)) > longAxisLength) {
				longAxisLength = Math.abs(stepVector.getAxis(i));
				longAxis = i;
			}
		}
				
		MoveVector accel = null;
		long ticks;
		ticks = (long)Math.ceil(stepVector.getAxis(longAxis) / startSpeedVector.getAxis(longAxis));
			
		if (ticks == 0) {
			return null;			
		}
	
		Move move = new Move(jogId++, ticks);
		
		for (int a=0; a < Move.AXES; a++) {
			move.setAxisSpeed(a, startSpeedVector.getAxis(a));
			
			// Check that we got exactly the movement in steps that we wanted,
			// if not adjust the speed until the error is gone.
			long steps = move.getAxisLength(a);
			long stepsWanted = (long)Math.round(mmMoveVector.getAxis(a)/mc.getAxes()[a].mmPerStep);
			
			long diffSteps = steps - stepsWanted;
			if (diffSteps != 0) {
				log.fine("Did not get correct movement in axis "+a+" wanted:"+stepsWanted+" got:"+steps);				
				move.nudgeSpeed(a, -diffSteps);
					
				// TODO: This is a ghastly hack, I know, but damn it, it works and I don't know what else to do.
				// I'd much rather have a system that's able to calculate the correct speed and acceleration the first time
				// rather than have to rely on nudging the speed parameter up and down after the inaccuracy has been detected.
				while (diffSteps != 0) {
					steps = move.getAxisLength(a);
					diffSteps = steps - stepsWanted;
					if (diffSteps != 0) {
						move.nudgeSpeed(a, -diffSteps/2.0);
						log.warning("Did not get correct movement in axis after correction "+a+" wanted:"+stepsWanted+" got:"+steps+", compensating...");
					}
				}
			}
		}		

		return move;
	}
	
	public void run() {
		try {
			while (true) {
				if (getCurrentJob() != null) {

					// Unlock only the axes that are used by this job.
					Point startPoint = lastBufferedLocation;
					usedAxes = getCurrentJob().getUsedAxes();
					renderedLines = 0;
					
					JobSize js = new JobSize(this);
					getCurrentJob().render(js);	
					currentJobLength = js.lineLength;
					currentJobSize = js.lineCount;
										
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
				
				} else if (jogDirection != null && jogTimeout > System.currentTimeMillis()) {

					for (int i=0;i<Move.AXES;i++) {
						usedAxes[i] = true;
					}
					
					while (jogTimeout > System.currentTimeMillis()) {
						double duration =  (jogTimeout-System.currentTimeMillis())/1000.0;
						if (duration > 0.1) {
							duration = 0.1;
						}
						
						double jogMaxSpeed = 150;
						double speed = jogMaxSpeed*jogDirection.length();						
						
						Point nextLoc = new Point();
						for (int i=0;i<Move.AXES;i++) {
							nextLoc.axes[i] = lastBufferedLocation.axes[i] + jogDirection.getAxis(i) * speed * duration; 
						}
						nextLoc.roundToWholeSteps(photonSaw.cfg.movementConstraints);
						
						MoveVector step = new MoveVector();
						for (int i=0;i<Move.AXES;i++) {
							step.setAxis(i, nextLoc.axes[i]-lastBufferedLocation.axes[i]);
						}
						
						lastBufferedLocation = nextLoc;
												
						Move m = endcodeJogMove(step, speed);
						if (m != null) {
							photonSaw.putMove(m);
						}
						while (!photonSaw.moveQueue.isEmpty()) {
							Thread.sleep(1); // Don't burn all the CPU while waiting						
						}					

						if (duration > 0.015) {
							Thread.sleep(Math.round(duration*1000-10));
						}
					}					
				}
				
				Thread.sleep(100); // Don't burn all the CPU if idle.
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
		Line line = new Line(photonSaw.cfg.movementConstraints, 
							lineBuffer.getList().size()>0 ? lineBuffer.getList().get(lineBuffer.getList().size()-1) : null,
							lastBufferedLocation, p, photonSaw.cfg.movementConstraints.getRapidMoveSpeed());
		if (line.getLength() > photonSaw.cfg.movementConstraints.getShortestMove()) {
			addLine(line);
		}
		renderedLines++;
	}

	@Override
	public void cutTo(Point p, double intensity, double maxSpeed) {
		for (int i=0;i<Move.AXES;i++) {
			if (!usedAxes[i]) {
				p.axes[i] = lastBufferedLocation.axes[i]; 
			}
		}
		Line line = new Line(photonSaw.cfg.movementConstraints, 
				lineBuffer.getList().size()>0 ? lineBuffer.getList().get(lineBuffer.getList().size()-1) : null,
				lastBufferedLocation, p, maxSpeed);
		if (line.getLength() > photonSaw.cfg.movementConstraints.getShortestMove()) {
			line.setLaserIntensity(intensity);
			addLine(line);
		}
		renderedLines++;
	}

	@Override
	public void engraveTo(Point p, double intensity, double maxSpeed,
			boolean[] pixels) {

		for (int i=0;i<Move.AXES;i++) {
			if (!usedAxes[i]) {
				p.axes[i] = lastBufferedLocation.axes[i]; 
			}
		}
		Line line = new Line(photonSaw.cfg.movementConstraints, 
				lineBuffer.getList().size()>0 ? lineBuffer.getList().get(lineBuffer.getList().size()-1) : null,
				lastBufferedLocation, p, maxSpeed);
		if (line.getLength() > photonSaw.cfg.movementConstraints.getShortestMove()) {
			line.setLaserIntensity(intensity);
			line.setPixels(pixels);
			addLine(line);
		}
		renderedLines++;
	}

	@Override
	public double getEngravingXAccelerationDistance(double speed) {
		// This doesn't take minSpeed into account, so there should be some head room.
		return Line.estimateAccelerationDistance(0, speed, photonSaw.cfg.movementConstraints.getAxes()[0].acceleration);
	}

	@Override
	public double getEngravingYStepSize() {
		if (getCurrentJob().getRootTransformation().getAxisMapping() == PointTransformation.AxisMapping.XY) {
			return photonSaw.cfg.movementConstraints.getAxes()[1].mmPerStep;

		} else if (getCurrentJob().getRootTransformation().getAxisMapping() == PointTransformation.AxisMapping.XA) {
			return photonSaw.cfg.movementConstraints.getAxes()[3].mmPerStep;
			
		} else {
			throw new RuntimeException("New AxisMapping not implemented");			
		}
	}

	public double getLineBufferLength() {
		return lineBuffer.getBufferLength();
	}

	public int getLineBufferCount() {
		return lineBuffer.getBufferSize();
	}

	public void setJogSpeed(MoveVector direction) {
		if (getCurrentJob() != null) {
			throw new RuntimeException("Cannot start jogging while a job is running");
		}
		
		// TOOD: Check for outstanding alarms too.
		
		jogDirection = direction;
		jogTimeout = System.currentTimeMillis()+300;		
	}
}
