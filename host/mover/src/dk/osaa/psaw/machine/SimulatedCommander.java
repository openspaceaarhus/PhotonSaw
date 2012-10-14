package dk.osaa.psaw.machine;

import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.UnsupportedCommOperationException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.logging.Level;

import dk.osaa.psaw.machine.Move.MoveAxis;

import lombok.extern.java.Log;

@Log
public class SimulatedCommander implements CommanderInterface {

	static FileWriter writer;

	public static void setLog(File lf) {
		try {
			if (writer != null) {
				writer.close();
				writer = null;
			}
			if (lf != null) {
				writer = new FileWriter(lf);
			}
		} catch (IOException e) {
			log.log(Level.SEVERE, "Fail", e);
		}
	}

	
	@Override
	public boolean connect(String portName) throws NoSuchPortException,
			PortInUseException, UnsupportedCommOperationException, IOException {
		log.info("Simulating connection to "+portName);		
		return false;
	}
	

	@Override
	public CommandReply run(String cmd) throws IOException, ReplyTimeout {
		log.info("Command: "+cmd);
			
		CommandReply r = new CommandReply();
		r.put("result", new ReplyValue("result ok"));
		
		if (cmd.equals("st")) {
			r.put("motion.active", new ReplyValue("motion.active No"));			
		}
		
		return r;
	}

	@Override
	public CommandReply getLastReplyValues() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void bufferMoves(ArrayBlockingQueue<Move> moveQueue)
			throws InterruptedException, IOException, ReplyTimeout,
			PhotonSawCommandFailed {
		
    	while (!moveQueue.isEmpty()) {
    		Move m = moveQueue.take();
    		
    		if (writer == null) {
    			continue;    			
    		}
    		
    		writer.append(Long.toString(m.id)); writer.append("\t");
    		writer.append(Long.toString(m.duration)); writer.append("\t");
    		writer.append(Long.toString(m.laserIntensity)); writer.append("\t");

    		for (MoveAxis ma : m.axes) {
    			if (ma.speed != null) {
    	    		writer.append(ma.speed.toString()); writer.append("\t");    				
    			} else {
    	    		writer.append("0"); writer.append("\t");    				    				
    			}
    			if (ma.accel != null) {
    	    		writer.append(ma.accel.toString()); writer.append("\t");    				
    			} else {
    	    		writer.append("0"); writer.append("\t");    				    				
    			}
    		}
    		
    		writer.append("\n");
    	}
		if (writer != null) {
	    	writer.flush();
		}
	}
}
