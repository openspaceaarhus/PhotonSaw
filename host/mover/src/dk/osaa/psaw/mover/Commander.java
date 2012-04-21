package dk.osaa.psaw.mover;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.UnsupportedCommOperationException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.logging.Level;

import lombok.extern.java.Log;
import lombok.val;

@Log
public class Commander {
		
	Thread reader;
    SerialPort serialPort;
    CommandReply reply = new CommandReply(); // Result of the last command.
	boolean replyReady = false;
		
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
		
	CommandReply run(String cmd) throws IOException, ReplyTimeout {
		synchronized (reply) {
			reply.clear();
			replyReady = false;
		}
		cmd += "\r";
		
		if (cmd.length() >= USB_LINE_BUFFER_SIZE) {
			throw new RuntimeException("The command line is too long: "+cmd);			
		}
		
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

    /**
     * Buffers the moves as soon as possible with as few commands as possible. 
     */
    public CommandReply bufferMoves(ArrayList<Move> moves) throws IOException, ReplyTimeout {
    	
    	CommandReply result = null;
    	val cc = new StringBuilder();    	
    	int wordsInCommand = 0;
    	for (Move m : moves) {

        	val ms = new StringBuilder();    	
        	for (long w : m.encode()) {
        		ms.append(" ");
        		ms.append(Long.toHexString(w).toLowerCase());
        	}
        	int wordsInMove = m.encode().size();

        	// Fire off the previously accumulated command if adding this move would overflow the line buffer size or the move buffer. 
    		if (cc.length()+ms.length() > USB_LINE_BUFFER_SIZE-10 || wordsInCommand+wordsInMove > lastBufferFree) {
            	while (lastBufferFree < wordsInCommand) {    		
            		result = run("st");
            		if (!result.get("result").isOk()) {
            			log.severe("Failed to get buffer status while waiting for room for move words: "+wordsInCommand+" "+result);
            			return result;    			
            		}
            		if (lastBufferFree < wordsInCommand) {
            			//log.info("Waiting for room in the buffer for "+wordsInCommand+" words, current free: "+lastBufferFree);
            			try { Thread.sleep(FULL_BUFFER_POLL_INTERVAL); } catch (InterruptedException e) { }    			
            		}
            	}

            	StringBuffer cb = new StringBuffer();
            	cb.append("bm ");
            	cb.append(Long.toHexString(wordsInCommand));
            	cb.append(cc);
        		result = run(cb.toString());    		
        		    	
            	cc.setLength(0);
            	wordsInCommand = 0;
    		}
    		
			cc.append(ms);
			wordsInCommand += wordsInMove;
    	}
    	
    	if (wordsInCommand > 0) {
        	while (lastBufferFree < wordsInCommand) {    		
        		result = run("st");
        		if (!result.get("result").isOk()) {
        			log.severe("Failed to get buffer status while waiting for room for move words: "+wordsInCommand+" "+result);
        			return result;    			
        		}
        		if (lastBufferFree < wordsInCommand) {
        			//log.info("Waiting for room in the buffer for "+wordsInCommand+" words, current free: "+lastBufferFree);
        			try { Thread.sleep(FULL_BUFFER_POLL_INTERVAL); } catch (InterruptedException e) { }    			
        		}
        	}

        	StringBuffer cb = new StringBuffer();
        	cb.append("bm ");
        	cb.append(Long.toHexString(wordsInCommand));
        	cb.append(cc);
        	result = run(cb.toString());    		
    	}
    	
    	return result;    	
    }
    
    
}
 