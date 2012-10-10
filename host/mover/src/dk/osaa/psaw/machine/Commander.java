package dk.osaa.psaw.machine;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.UnsupportedCommOperationException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.logging.Level;


import lombok.extern.java.Log;
import lombok.val;

@Log
public class Commander implements CommanderInterface {
		
	Thread reader;
    SerialPort serialPort;
    CommandReply reply = new CommandReply(); // Result of the last command.
	boolean replyReady = false;
		
	/* (non-Javadoc)
	 * @see dk.osaa.psaw.machine.CommanderInterface#connect(java.lang.String)
	 */
	@Override
	public boolean connect(String portName) throws NoSuchPortException, PortInUseException, UnsupportedCommOperationException, IOException {
		System.setProperty("gnu.io.rxtx.SerialPorts", portName); // God damn it, why isn't the default to try the port the user wants?
		
        CommPortIdentifier portIdentifier = CommPortIdentifier.getPortIdentifier(portName);
        if (portIdentifier.isCurrentlyOwned()) {
            log.warning("Unable to open serial port "+portName+" as it's already opened");
            return false;
        }
        
        CommPort commPort = portIdentifier.open(this.getClass().getName(),2000);            
        if (!(commPort instanceof SerialPort)) {
        	log.severe("Can't open a port that's not a serial port "+portName+" is a "+commPort.getClass().getName());
        	return false;
        }
        
        serialPort = (SerialPort) commPort;
        serialPort.setSerialPortParams(9600, SerialPort.DATABITS_8,SerialPort.STOPBITS_1,SerialPort.PARITY_NONE);
        
        reader = new Thread(new SerialReader());
        reader.start();
        
        return true;
    }
	
	public static final int USB_LINE_BUFFER_SIZE = 1<<12;
		
	/* (non-Javadoc)
	 * @see dk.osaa.psaw.machine.CommanderInterface#run(java.lang.String)
	 */
	@Override
	public synchronized CommandReply run(String cmd) throws IOException, ReplyTimeout {
		synchronized (reply) {
			reply.clear();
			replyReady = false;
		}
		cmd += "\r";
		
		if (cmd.length() >= USB_LINE_BUFFER_SIZE) {
			throw new RuntimeException("The command line is too long: "+cmd);			
		}
		
		log.fine("Running: "+cmd);
		serialPort.getOutputStream().write(cmd.getBytes());
		serialPort.getOutputStream().flush();
		
		int patience = 10000 / 10; // Timeout 10 sec, sleep 10 ms per loop
		while (patience-- > 0) {
			synchronized (reply) {
				if (replyReady) {
					return reply.clone();
				}
			}
			try { Thread.sleep(10); } catch (InterruptedException e) { }
		}
		
		throw new ReplyTimeout();
    }

	CommandReply lastReplyValues = new CommandReply();
	/* (non-Javadoc)
	 * @see dk.osaa.psaw.machine.CommanderInterface#getLastReplyValue(java.lang.String)
	 */
	public ReplyValue getLastReplyValue(String name) {
		synchronized (lastReplyValues) {
			return lastReplyValues.get(name);
		}
	}
	
	/* (non-Javadoc)
	 * @see dk.osaa.psaw.machine.CommanderInterface#getLastReplyValues()
	 */
	@Override
	public CommandReply getLastReplyValues() {
		synchronized (lastReplyValues) {
			return lastReplyValues.clone();
		}
	}
			
	long lastBufferFree;
	
	static final String READY = "\r\nReady\r\n";
	void parseReply(StringBuilder replyString) {
		int readyIndex = replyString.lastIndexOf(READY);
		if (readyIndex < 0) {
			return; // Moar!
		}
				
		synchronized (reply) {
			reply.clear();
			String rs = replyString.substring(0, readyIndex+2);
			replyString.delete(0, readyIndex+READY.length());
			replyReady = true;
			
			for (String line : rs.split("[\r\n]+")) {
				ReplyValue value = new ReplyValue(line);
				reply.put(value.getName(), value);
				synchronized (lastReplyValues) {
					lastReplyValues.put(value.getName(), value);
				}
				if (value.getName().equals("buffer.free")) {
					lastBufferFree = value.getInteger();
				}
			}
			if (!reply.containsKey("result")) {
				reply.put("result", new ReplyValue("result OK"));
			}
		}		
	}

    public class SerialReader implements Runnable {

    	InputStream in;
    	public SerialReader() throws IOException {
    		in = serialPort.getInputStream();
    	}
    	
        public void run() {
            try {
                byte[] buffer = new byte[1024];
                int len = -1;
                StringBuilder replyString = new StringBuilder();
                while ( (len = this.in.read(buffer)) > -1) {
                	replyString.append(new String(buffer,0,len));
                	parseReply(replyString);
                }
            } catch (IOException e) {
                log.log(Level.SEVERE, "Got exception while reading from serial port", e);
            }            
        }
    }
    
    static final int FULL_BUFFER_POLL_INTERVAL = 250;
        
    /**
     * Sends the moves to the buffer and ensures the first one isn't started before the last one is done buffering
     */
	public CommandReply bufferMovesAtomic(ArrayList<Move> moves) throws IOException, ReplyTimeout {
    	int wordsNeeded = 0;
    	for (Move m : moves) {
    		wordsNeeded += m.encode().size();
    	}
    	
    	if (wordsNeeded > 4095) {
    		throw new RuntimeException("More words are needed to buffer the moves, than are available in hardware");
    	}

    	CommandReply result = null;
    	while (lastBufferFree < wordsNeeded) {    		
    		result = run("st");
    		if (!result.get("result").isOk()) {
    			log.severe("Failed to get buffer status while waiting for room for move words: "+wordsNeeded+" "+result);
    			return result;    			
    		}
    		if (lastBufferFree < wordsNeeded) {
    			//log.info("Waiting for room in the buffer for "+wordsNeeded+" words, current free: "+lastBufferFree);
    			try { Thread.sleep(FULL_BUFFER_POLL_INTERVAL); } catch (InterruptedException e) { }    			
    		}
    	}
  
    	val mw = new ArrayList<String>();    	
    	for (Move m : moves) {
    		for (long w : m.encode()) {
    			mw.add(Long.toHexString(w).toLowerCase());
    		}
    	}

    	while (mw.size() > 0) {    		
    		val s = new StringBuilder();
			int wordsInCommand = 0;
    		while (s.length() < USB_LINE_BUFFER_SIZE-20 && mw.size() > 0) {
    			s.append(" ");
    			s.append(mw.remove(0));
    			wordsInCommand++;
    		}
    		
    		val cmd = new StringBuilder();
    		if (mw.size() == 0) {
    			cmd.append("bm ");
            	cmd.append(Long.toHexString(wordsInCommand));
    			cmd.append(" ");
    			
    		} else {
    			cmd.append("bm -nc"); // don't allow the move bytes to be consumed as we aren't done buffering yet.    			
            	cmd.append(Long.toHexString(wordsInCommand));
    			cmd.append(" ");
    		}
    		cmd.append(s);
    		
    		result = run(cmd.toString());    		
    	}
    	
    	return result;    	
    }

    
    CommandReply lastBufferMoveReply;
    StringBuilder encodedMoves = new StringBuilder();
    int wordsInCommand = 0;

    public CommandReply bufferMoveResult() {
    	return lastBufferMoveReply;
    }
    
    /* (non-Javadoc)
	 * @see dk.osaa.psaw.machine.CommanderInterface#flushMoves()
	 */
	public void flushMoves() throws IOException, ReplyTimeout, PhotonSawCommandFailed {
    	if (wordsInCommand <= 0) {
    		return;
    	}

    	if (log.isLoggable(Level.FINE)) {
    		log.fine("Flushing the last "+wordsInCommand+" words");
        }
    		
    	while (lastBufferFree < wordsInCommand) {    		
    		lastBufferMoveReply = run("st");
    		if (!lastBufferMoveReply.get("result").isOk()) {
    			log.severe("Failed to get buffer status while waiting for room for move words: "+wordsInCommand+" "+lastBufferMoveReply);
    			throw new PhotonSawCommandFailed("Failed to get buffer status while waiting for room for move words: "+wordsInCommand+" "+lastBufferMoveReply);    			
    		}
    		if (lastBufferFree < wordsInCommand) {
    			//log.info("Waiting for room in the buffer for "+wordsInCommand+" words, current free: "+lastBufferFree);
    			try { Thread.sleep(FULL_BUFFER_POLL_INTERVAL); } catch (InterruptedException e) { }    			
    		}
    	}

    	StringBuffer cb = new StringBuffer();
    	cb.append("bm ");
    	cb.append(Long.toHexString(wordsInCommand));
    	cb.append(encodedMoves);
    	lastBufferMoveReply = run(cb.toString());    		
    	encodedMoves.setLength(0);
    	wordsInCommand = 0;
		if (!lastBufferMoveReply.get("result").isOk()) {
			log.severe("Failed to buffer move words: "+wordsInCommand+" "+lastBufferMoveReply);
			throw new PhotonSawCommandFailed("Failed to buffer move words: "+wordsInCommand+" "+lastBufferMoveReply);    			
		}
    }
    
    /**
     * Buffers the move, possibly sending it and the previous commands to the hardware, if command line length limit is reached
     * Remember to call flushMoves() when all moves have been buffered 
     * @throws PhotonSawCommandFailed 
     */
    public void bufferMove(Move move) throws IOException, ReplyTimeout, PhotonSawCommandFailed {
    	
    	if (log.isLoggable(Level.FINE)) {
    		log.fine("Buffering move: "+move.id+" length: "+move.getAxisLength(0)+" x "+move.getAxisLength(1));
    	}
    	
    	val ms = new StringBuilder();
    	int wordsInMove = 0;
		for (long w : move.encode()) {
			ms.append(" ");
			ms.append(Long.toHexString(w).toLowerCase());
		}
		wordsInMove = move.encode().size();

    	// Fire off the previously accumulated command if adding this move would overflow the line buffer size or the move buffer. 
		if (encodedMoves.length()+ms.length() > USB_LINE_BUFFER_SIZE-10 || wordsInCommand+wordsInMove > lastBufferFree) {

        	while (lastBufferFree < wordsInCommand) {    		
        		lastBufferMoveReply = run("st");
        		if (!lastBufferMoveReply.get("result").isOk()) {
        			log.severe("Failed to get buffer status while waiting for room for move words: "+wordsInCommand+" "+lastBufferMoveReply);
        			throw new PhotonSawCommandFailed("Failed to get buffer status while waiting for room for move words: "+wordsInCommand+" "+lastBufferMoveReply);    			
        		}
        		if (lastBufferFree < wordsInCommand) {
        			//log.info("Waiting for room in the buffer for "+wordsInCommand+" words, current free: "+lastBufferFree);
        			try { Thread.sleep(FULL_BUFFER_POLL_INTERVAL); } catch (InterruptedException e) { }    			
        		}
        	}

        	StringBuffer cb = new StringBuffer();
        	cb.append("bm ");
        	cb.append(Long.toHexString(wordsInCommand));
        	cb.append(encodedMoves);
    		lastBufferMoveReply = run(cb.toString());    		
    		if (!lastBufferMoveReply.get("result").isOk()) {
    			log.severe("Failed to buffer move words: "+wordsInCommand+" "+lastBufferMoveReply);
    			throw new PhotonSawCommandFailed("Failed to buffer move words: "+wordsInCommand+" "+lastBufferMoveReply);    			
    		}
    		    	
        	encodedMoves.setLength(0);
        	wordsInCommand = 0;
		}

		encodedMoves.append(ms);
		wordsInCommand += wordsInMove;    	
    }
   
   
    /**
     * Buffers the moves as soon as possible with as few commands as possible. 
     * @throws PhotonSawCommandFailed 
     */
	public void bufferMoves(ArrayList<Move> moves) throws IOException, ReplyTimeout, PhotonSawCommandFailed {
    	for (Move m : moves) {
    		bufferMove(m);
    	}
    	flushMoves();
    }

    /**
     * Buffers all the moves in the queue, returns when the queue is empty.
     *  
     * @param moveQueue
     * @throws InterruptedException
     * @throws IOException
     * @throws ReplyTimeout
     * @throws PhotonSawCommandFailed
     */
	public void bufferMoves(ArrayBlockingQueue<Move> moveQueue) throws InterruptedException, IOException, ReplyTimeout, PhotonSawCommandFailed {
    	while (!moveQueue.isEmpty()) {
    		bufferMove(moveQueue.take());
    	}
    	flushMoves();
	}
}
 