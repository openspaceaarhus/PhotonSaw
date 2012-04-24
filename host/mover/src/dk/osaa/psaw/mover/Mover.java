package dk.osaa.psaw.mover;

import java.util.ArrayList;
import java.util.logging.Level;

import lombok.val;
import lombok.extern.java.Log;

/**
 * Test class to get the API to do something.
 * @author ff
 */
@Log
public class Mover {
	public static void main(String[] args) {
		Commander commander = new Commander();
		try {
			commander.connect("/dev/ttyACM0");
		} catch (Exception e) {
			log.log(Level.SEVERE, "Failed to connect to serial port", e);
			System.exit(1);
		}
		
		try {
			val r = commander.run("ai 10c");
			if (r.get("result").isOk()) {
				log.info("command worked: "+r);
			} else {
				log.info("command gave error: "+r);
			}
			
			Planner p = new Planner(commander);
			
		} catch (Exception e) {
			log.log(Level.SEVERE, "Failed while running command", e);
			System.exit(2);
		}
		
		System.exit(0);
	}

}
