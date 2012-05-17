package dk.osaa.psaw.core;

import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.UnsupportedCommOperationException;

import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.logging.Level;

import dk.osaa.psaw.machine.CommandReply;
import dk.osaa.psaw.machine.Commander;
import dk.osaa.psaw.machine.Move;
import dk.osaa.psaw.config.Configuration;
import dk.osaa.psaw.machine.PhotonSawCommandFailed;
import dk.osaa.psaw.machine.ReplyTimeout;

import lombok.Getter;
import lombok.val;
import lombok.extern.java.Log;

/**
 * The main machine interface class that all the other bits hang off of. 
 * 
 * @author Flemming Frandsen <dren.dk@gmail.com> <http://dren.dk>
 */
@Log
public class PhotonSaw extends Thread {
	Configuration cfg;
	Commander commander;
	@Getter
	Planner planner;
	
	@Getter
	ArrayBlockingQueue<Move> moveQueue = new ArrayBlockingQueue<Move>(100); // TODO: Read from config
	
	public PhotonSaw(Configuration cfg) throws IOException, ReplyTimeout, NoSuchPortException, PortInUseException, UnsupportedCommOperationException, PhotonSawCommandFailed  {
		this.cfg = cfg;
		planner = new Planner(this);
		commander = new Commander();
		commander.connect(cfg.hostConfig.getSerialPort());
		
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
			run("me "+i+" "+
					cfg.movementConstraints.getAxes()[i].coilCurrent+" "+
					cfg.movementConstraints.getAxes()[i].microSteppingMode);
		}
	}

	public Commander getCommander() {
		return commander;
	}

	public boolean active() throws IOException, ReplyTimeout, PhotonSawCommandFailed {		
		CommandReply r = run("st");
		return r.get("motion.active").getBoolean();
	}
}
