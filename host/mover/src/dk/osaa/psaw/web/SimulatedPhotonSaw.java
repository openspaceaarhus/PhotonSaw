package dk.osaa.psaw.web;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;

import dk.osaa.psaw.config.Configuration;
import dk.osaa.psaw.core.PhotonSaw;
import dk.osaa.psaw.core.PhotonSawAPI;
import dk.osaa.psaw.core.PhotonSawStatus;
import dk.osaa.psaw.job.JobManager;
import dk.osaa.psaw.machine.Move;
import dk.osaa.psaw.machine.MoveVector;

public class SimulatedPhotonSaw implements PhotonSawAPI {
	Configuration cfg;
	
	ArrayList<PhotonSawStatus> status = new ArrayList<PhotonSawStatus>();
	int currentStatus;
	long offsetTime;
	
	JobManager jobManager;
	
	@SuppressWarnings("unchecked")
	public SimulatedPhotonSaw(Configuration cfg) throws FileNotFoundException {
		this.cfg = cfg;
		currentStatus = 0;
		jobManager = new JobManager(cfg);
		
		if (!cfg.hostConfig.isSimulating()) {
			throw new RuntimeException("This class can only be used when simulating");
		}
		
		File d = cfg.hostConfig.getRecordDir();
		if (d == null || !d.isDirectory()) {
			throw new RuntimeException("Missing recordDir parameter to start simulation: "+d);
		}
		
		for (String f : d.list()) {
			if (!f.matches("\\.psawstate$")) {
				continue;
			}
			
			File fn = new File(d, f);
			PhotonSawStatus pss = (PhotonSawStatus)PhotonSaw.getStatusXStream().fromXML(
						new BufferedInputStream(
						new FileInputStream(fn)));
			status.add(pss);
		}
		
		Collections.sort(status);
		
		resetSimulation();
	}
		
	private void resetSimulation() {		
		if (status.size() > 0) {
			offsetTime = status.get(0).getTimestamp()-System.currentTimeMillis();
			currentStatus = 0;
		}
	}

	@Override
	public PhotonSawStatus getStatus() {
		if (status.size() == 0) {
			return null;
		}
		
		if (currentStatus+1 < status.size()-1) {
			while ((currentStatus+1 < status.size()-1) && status.get(currentStatus+1).getTimestamp()+offsetTime > System.currentTimeMillis()) {
				currentStatus++;
			}
		} else {
			resetSimulation();
		}
			
		PhotonSawStatus s = status.get(currentStatus);
		s.setTimestamp(s.getTimestamp()+offsetTime);
		return s;
	}

	@Override
	public JobManager getJobManager() {
		return jobManager;
	}

	@Override
	public void setJogSpeed(MoveVector direction) {
		// TODO: Simulate jogging...		
	}

	@Override
	public void putMove(Move move) throws InterruptedException {
		// TODO Auto-generated method stub		
	}

	@Override
	public void putAssistAir(boolean assistAir) throws InterruptedException {
		// TODO Auto-generated method stub		
	}
}
