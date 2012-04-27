package dk.osaa.psaw.mover;

import java.io.IOException;
import java.util.logging.Level;

import lombok.val;
import lombok.extern.java.Log;

@Log
public class PhotonSaw {

	MovementConstraints mc;
	Commander commander;
	Planner planner;
	
	public PhotonSaw(Commander commander) throws IOException, ReplyTimeout  {
		this.commander = commander;
		mc = new MovementConstraints();
		planner = new Planner(this);
		configureMotors();
		planner.runTest();
	}
	
	private CommandReply runOrDie(String cmd) {		
		try {
			log.info("Running: "+cmd);
			val r = commander.run(cmd);
			if (r.get("result").isOk()) {
				log.info("command '"+cmd+"' worked: "+r);
			} else {
				log.severe("command '"+cmd+"' gave error: "+r);
			}
			return r;
		} catch (Exception e) {
			log.log(Level.SEVERE, "Caught exception while running command: "+cmd, e);
			throw new RuntimeException(e);
		}
	}
		
	private void configureMotors() {
		runOrDie("ai 10c"); // TODO: Ignore alarms while testing
		runOrDie("ai 10c"); // TODO: Ignore alarms while testing

		for (int i=0;i<Move.AXES;i++) {
			runOrDie("me "+i+" "+mc.axes[i].coilCurrent+" "+mc.axes[i].microSteppingMode);
		}
	}

	public Commander getCommander() {
		return commander;
	}
}
