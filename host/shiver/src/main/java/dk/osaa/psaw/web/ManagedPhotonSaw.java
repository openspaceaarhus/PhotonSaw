package dk.osaa.psaw.web;

import dk.osaa.psaw.core.PhotonSaw;
import dk.osaa.psaw.machine.Move;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.java.Log;
import io.dropwizard.lifecycle.Managed;

@Log
@Getter
public class ManagedPhotonSaw implements Managed {
	public ManagedPhotonSaw(PhotonSaw photonSaw) {
		this.photonSaw = photonSaw;
	}

	private final PhotonSaw photonSaw;

	@Override
	public void start() throws Exception {
		log.info("Start");
	}

	@Override
	public void stop() throws Exception {
		log.info("Stop");
		
		// Turn off the motors
		for (int i=0;i<Move.AXES;i++) {					
			try {
				photonSaw.run("me "+i+" 0 0");
			} catch (Exception e) {
			}
		}
		Move.dumpProfile();
	}

}
