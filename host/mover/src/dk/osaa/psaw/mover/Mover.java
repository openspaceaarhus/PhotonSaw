package dk.osaa.psaw.mover;

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
		try {
			PhotonSaw ps = new PhotonSaw();
		} catch (Exception e) {
			log.log(Level.SEVERE, "Failed while running command", e);
			System.exit(2);
		}
	
		Move.dumpProfile();
		System.exit(0);
	}

}
