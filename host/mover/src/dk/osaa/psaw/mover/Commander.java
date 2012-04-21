package dk.osaa.psaw.mover;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.UnsupportedCommOperationException;

import java.io.IOException;
import java.io.InputStream;
import java.util.TreeMap;
import java.util.logging.Level;

import lombok.extern.java.Log;

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
	
	
	@SuppressWarnings("unchecked")
	CommandReply run(String cmd) throws IOException, ReplyTimeout {
		synchronized (reply) {
			reply.clear();
			replyReady = false;
		}
		cmd += "\r";
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
}
