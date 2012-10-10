package dk.osaa.psaw.machine;

import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.UnsupportedCommOperationException;

import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;

import lombok.extern.java.Log;

@Log
public class SimulatedCommander implements CommanderInterface {

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
    		log.info(moveQueue.take().toString());
    	}
	}
}
