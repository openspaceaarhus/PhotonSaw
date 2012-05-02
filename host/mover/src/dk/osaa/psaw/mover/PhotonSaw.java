package dk.osaa.psaw.mover;

import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.UnsupportedCommOperationException;

import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.logging.Level;

import lombok.val;
import lombok.extern.java.Log;

@Log
public class PhotonSaw extends Thread {
	MovementConstraints mc;
	Commander commander;
	Planner planner;
	
	ArrayBlockingQueue<Move> moveQueue = new ArrayBlockingQueue<Move>(2000); // TODO: Read from config
	
	public PhotonSaw() throws IOException, ReplyTimeout, NoSuchPortException, PortInUseException, UnsupportedCommOperationException, PhotonSawCommandFailed  {
		mc = new MovementConstraints();
		planner = new Planner(this);
		commander = new Commander();
		commander.connect("/dev/ttyACM0"); // TODO: Read from config
		
		setDaemon(true);
		setName("PhotonSaw thread, keeps the hardware fed");				
		
		configureMotors();

		planner.start();
		this.start();	
	}

	public void putMove(Move move) throws InterruptedException {
		moveQueue.put(move);
	}	
	
	public void run() {
		while (true) {
			try {
				commander.bufferMoves(moveQueue);
				Thread.sleep(250); // Wait and see if another move becomes available
			} catch (Exception e) {
				log.log(Level.SEVERE, "Caught exception while buffering moves", e);
			}
		}
	}
		
	public CommandReply run(String cmd) throws IOException, ReplyTimeout, PhotonSawCommandFailed {		
		log.fine("Running: "+cmd);
		val r = commander.run(cmd);
		if (r.get("result").isOk()) {
			log.fine("command '"+cmd+"' worked: "+r);
		} else {
			log.severe("command '"+cmd+"' gave error: "+r);
			throw new PhotonSawCommandFailed("command '"+cmd+"' gave error: "+r);
		}
		return r;
	}
		
	private void configureMotors() throws IOException, ReplyTimeout, PhotonSawCommandFailed {
		run("ai 10c"); // TODO: Ignore alarms while testing
		run("ai 10c"); // TODO: Ignore alarms while testing

		for (int i=0;i<Move.AXES;i++) {
			run("me "+i+" "+mc.axes[i].coilCurrent+" "+mc.axes[i].microSteppingMode);
		}
	}

	public Commander getCommander() {
		return commander;
	}
}
