package dk.osaa.psaw.machine;

import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.UnsupportedCommOperationException;

import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * This is the interface used by the rest of the system to communicate with the hardware
 *  
 * @author Flemming Frandsen <dren.dk@gmail.com> <http://dren.dk>
 */
public interface CommanderInterface {

	public boolean connect(String portName) throws NoSuchPortException, PortInUseException, UnsupportedCommOperationException, IOException;
	public CommandReply run(String cmd) throws IOException, ReplyTimeout;
	public CommandReply getLastReplyValues();
	public void bufferMoves(ArrayBlockingQueue<Move> moveQueue) throws InterruptedException, IOException, ReplyTimeout, PhotonSawCommandFailed;
}