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
			PhotonSaw ps = new PhotonSaw(commander);
		} catch (Exception e) {
			log.log(Level.SEVERE, "Failed while running command", e);
			System.exit(2);
		}
	
		Move.dumpProfile();
		System.exit(0);
	}

}
