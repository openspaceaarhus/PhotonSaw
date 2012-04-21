package dk.osaa.psaw.mover;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.PortInUseException;

import java.util.Enumeration;
import java.util.HashSet;
import java.util.TreeMap;
import java.util.logging.Level;

import lombok.extern.java.Log;

/**
 * Test class to get the API to do something.
 * @author ff
 */
@Log
public class Mover {
	public static HashSet<CommPortIdentifier> getAvailableSerialPorts() {
        HashSet<CommPortIdentifier> h = new HashSet<CommPortIdentifier>();
        Enumeration thePorts = CommPortIdentifier.getPortIdentifiers();
        while (thePorts.hasMoreElements()) {
            CommPortIdentifier com = (CommPortIdentifier) thePorts.nextElement();
            switch (com.getPortType()) {
            case CommPortIdentifier.PORT_SERIAL:
                try {
                    CommPort thePort = com.open("CommUtil", 50);
                    thePort.close();
                    h.add(com);
                } catch (PortInUseException e) {
                    System.out.println("Port, "  + com.getName() + ", is in use.");
                } catch (Exception e) {
                    System.err.println("Failed to open port " +  com.getName());
                    e.printStackTrace();
                }
            }
        }
        return h;
    }

	public static void main(String[] args) {
		Commander commander = new Commander();
		try {
			commander.connect("/dev/ttyACM0");
		} catch (Exception e) {
			log.log(Level.SEVERE, "Failed to connect to serial port", e);
			System.exit(1);
		}
		
		try {
			TreeMap<String, ReplyValue> r = commander.run("st");
			if (r.get("result").isOk()) {
				log.info("command worked: "+r);
			} else {
				log.info("command gave error: "+r);
			}
		} catch (Exception e) {
			log.log(Level.SEVERE, "Failed while running command", e);
			System.exit(2);
		}
		System.exit(0);
	}

}
