package dk.osaa.psaw.machine;

import java.util.ArrayList;
import java.util.logging.Level;

import lombok.Data;
import lombok.val;
import lombok.extern.java.Log;

/**
 * A Move ready to be sent to the hardware, see firmware/mover/move.h for a specification of how this works
 * 
 * The encode routine is sensitive to the order it's called, so call it once for every move and in the same
 * order the moves are going to be dispatched to the hardware.
 *  
 * @author ff
 */
@Data
@Log
public class Move {
	public static final int AXES = 4;
	public static final String AXIS_NAMES[] = {"X", "Y", "Z", "A"};
	static long lastEncodedId = 0;
	
	public static class MoveAxis {
		Q30 speed;
		Q30 accel;
		long endPos;
		long startPos;
	};
	
	long id;
	long duration;
	MoveAxis axes[] = new MoveAxis[AXES];
	Integer laserIntensity;	
	Q16 laserAcceleration;

	Q30 pixelSpeed;
	long[] pixelWords;
	
	long switchesOn;
	long switchesOff;
	
	static final long MOVE_SW_ASSIST_AIR = 1<<0;
	
	public void setAssistSwitch(boolean assistAir) {
		if (assistAir) {
			switchesOn |= MOVE_SW_ASSIST_AIR;			
		} else {
			switchesOff |= MOVE_SW_ASSIST_AIR;			
		}
	}

	public void setScanline(boolean[] pixels) {
		long pixelSpeedLong = (Q30.ONE*pixels.length)/duration;
		pixelSpeed = new Q30(pixelSpeedLong);
			
		//pixelSpeed = new Q30(((double)pixels.length)/duration);
		pixelWords = new long[(int)Math.ceil(pixels.length/32.0)];
		
		if (pixels.length == 448 && duration==8382) {
			log.info("Hit: "+axes[0].speed.toDouble());
		}
		
		long actualPixels = getPixelLength();
		if (pixels.length != actualPixels) {
			throw new RuntimeException("Fail! "+pixels.length+" != "+actualPixels);
		}		
		
		log.info("id: "+id+" Speed: "+pixelSpeed.toDouble()+
				" duration:"+duration+" pixels.length:"+pixels.length+
				" pixelWords:"+pixelWords.length+" xspeed:"+axes[0].speed.toDouble());
		for (int pc=0;pc<pixels.length;pc++) {
			if (pixels[pc]) {
				pixelWords[pc >> 5] |= 1L<<(pc & 31);
			}
			/*
			if ((pc & 31) == 31) {
				log.info((pc>>5)+": "+pixelWords[pc >> 5]);
			}
			*/
		}
	}
	
	public long getPixelLength() {
		int v = pixelSpeed.getIntValue();
		
		int ticks = (int)duration;
		int d = 0; // pixels consumed
		int e = 0; // Error
		while (true) {
			e += v; 
			
			if (e >= Q30.ONE) {
				d++;
				e -= Q30.ONE;
			}
			
			if (ticks-- == 0) { // See shaker.c: Bottom of continueCurrentMove
				break;
			}
		}
		return d;
	}
	
	
	MoveAxis getAxis(int axis) {
		if (axes[axis] == null) {
			axes[axis] = new MoveAxis();			
		}
		return axes[axis];
	}
	
	public void setAxisSpeed(int axis, double speed) {
		if (speed != 0) {
			getAxis(axis).speed = new Q30(speed);
		}
	}

	int getAxisSpeed(int axis) {
		if (axes[axis] != null && axes[axis].speed != null) {
			return axes[axis].speed.intValue;
		} else {
			return 0;
		}
	}
	
	public void setAxisAccel(int axis, double accel) {
		if (accel != 0) {
			getAxis(axis).accel = new Q30(accel);
		}
	}

	int getAxisAccel(int axis) {
		if (axes[axis] != null && axes[axis].accel != null) {
			return axes[axis].accel.intValue;
		} else {
			return 0;
		}
	}

	public void setAxisStartPos(int axis, long pos) {
		getAxis(axis).startPos = pos;
	}
	
	public void setAxisEndPos(int axis, long pos) {
		getAxis(axis).endPos = pos;
	}

	public long getAxisStartPos(int axis) {
		return getAxis(axis).startPos;
	}
	
	public long getAxisEndPos(int axis) {
		return getAxis(axis).endPos;
	}	

	public String toString() {
		val sb = new StringBuilder();
		sb.append("Move(");
		sb.append("t:"+duration);
		for (int i=0;i<AXES;i++) {
			double v = getAxis(i).speed != null ? getAxis(i).speed.toDouble() : 0;
			double a = getAxis(i).accel != null ? getAxis(i).accel.toDouble() : 0;
			long l = getAxisLength(i);
			if (v != 0) {
				sb.append(", "+i+"s:"+String.format("%05f", v));
			}
			if (a != 0) {
				sb.append(", "+i+"a:"+a);
			}
			if (l != 0) {
				sb.append(", "+i+"l:"+l);
			}
		}
		if (laserIntensity != null && laserIntensity != 0) {
			sb.append(", Li:"+laserIntensity);				
		}
		if (laserAcceleration != null && laserAcceleration.value != 0) {
			sb.append(", La:"+laserAcceleration);				
		}
		if (pixelSpeed != null) {
			sb.append(", p:"+pixelWords.length);
		}
		sb.append(")");
		return sb.toString();
	}

	/**
	 * Uses simple Newtonian physics to calculate the length of the move, this doesn't
	 * yield the correct result as the quantification that comes from the distinct ticks
	 * and the algorithm used isn't ideal.
	 * 
	 * @param a the axis
	 * @return the number of steps taken in this move
	 */
	public long getAxisLengthNewtonian(int axis) {
		double v = getAxis(axis).speed != null ? getAxis(axis).speed.toDouble() : 0;
		double a = getAxis(axis).accel != null ? getAxis(axis).accel.toDouble() : 0;
		double d = duration*v + duration*duration*(a/2);
		if (d > duration) {
			throw new RuntimeException("distance traveled in a move can never be more than one step per tick v:"+v+" a:"+a+" d:"+d+" ticks:"+duration);
		}
		return (long)Math.floor(d);
	}

	/**
	 * This is an exact replication of the algorithm used by the shaker.c and axis.h to move
	 * the motor, it's slow as it iterates exactly like the hardware does to ensure absolute fidelity.
	 * 
	 * @param axis The axis to calculate
	 * @return
	 */	
	public long getAxisLength(int axis) {
		int v = getAxisSpeed(axis);
		int a = getAxisAccel(axis);
		
		// This should save about half the time spent in this routine as Z and A are still most of the time.
		if (v == 0 && a == 0) { 
			return 0;
		}
		
		long t0 = System.nanoTime();
		int ticks = (int)duration;
		int dir = 1;
		if (v < 0 || (v==0 && a < 0)) { // See axis.h: axisPrepareMove
			v = -v;
			a = -a;
			dir = -1;
		}
		int d = 0; // steps traveled
		int e = 0; // Error
		while (true) {
			e += v; 
			v += a; // See axis.h: axisTick
			
			if (e >= Q30.ONE) {
				d += dir;
				e -= Q30.ONE;
			}
			
			if (ticks-- == 0) { // See shaker.c: Bottom of continueCurrentMove
				break;
			}
		}
		long t1 = System.nanoTime();
		lengthTime += t1-t0;
		lengthCount++;
		return d;
	}
	static long lengthCount = 0;
	static long lengthTime = 0;
	public static void dumpProfile() {
		if (lengthCount != 0) {
			log.info("getAxisLength calls: "+lengthCount+" total time: "+lengthTime+" ns, avg: "+lengthTime/lengthCount+" ns");
		}
		log.info("nudgeSpeed calls: "+nudgeAxisStepsCalls);
	}


	/**
	 * Start creating a move with the only two mandatory parameters 
	 * @param id The ID of the move, to make debugging possible
	 * @param duration Number of timer ticks this move lasts
	 */
	public Move(long id, long duration) {
		this.id = id;
		this.duration = duration;
		
		switchesOn = switchesOff = 0;			
	}
	
	static final long MOVE_MAGIC = 0x05aa0000L;
	static final int ID_FLAG = 1<<0;
	
	int axisSpeedFlag(int axis) {
		return (1<<(((axis)<<1)+1));
	}

	int axisAccelFlag(int axis) {
		return (1<<(((axis)<<1)+2));
	}
	
	static final long LASER_MAGIC = 0x1A5E0000; 
	static final int LASER_FLAG = 1<<9;
	static final int LASER_ACCEL_FLAG = 1<<10;
	static final int PIXEL_FLAG = 1<<11;
	static final int SWITCHES_FLAG = 1<<13;
	
	/**
	 * Encode this move as a number of move codes so it's ready to be output. 
	 */
	ArrayList<Long> encoded;
	public synchronized ArrayList<Long> encode() {
		if (encoded != null) {
			return encoded;
		}
		
		encoded = new ArrayList<Long>();

		int headerIndex = encoded.size(); // Remember were to put the header
		encoded.add(0xaabbaabbaaL); //dummy value as a place holder, it's easy to recognize if it ever shows up in the output.  
		long header = MOVE_MAGIC;
		
		encoded.add(duration); // Duration: Number of ticks this move lasts

		// 0: ID
		if (id != lastEncodedId+1) {
			encoded.add(id); 
			header |= ID_FLAG;
		}
		lastEncodedId = id;
		
		// Speed and acceleration for all axes:
		for (int i=0;i<AXES;i++) {
			if (axes[i] == null) {
				continue;
			}
			if (axes[i].speed != null && Math.abs(axes[i].speed.getValue()*duration) > 10) {
				encoded.add((long)axes[i].speed.getValue()); 
				header |= axisSpeedFlag(i);
			}
			if (axes[i].accel != null && Math.abs(axes[i].accel.getValue()*duration) > 10) {
				encoded.add((long)axes[i].accel.getValue()); 
				header |= axisAccelFlag(i);
			}
		}
		
		// 13: Switches
		if (switchesOn != 0 || switchesOff != 0) {
			encoded.add(switchesOn);
			encoded.add(switchesOff);
			header |= SWITCHES_FLAG;
		}

		// 9: LASER (start) intensity
		if ((laserIntensity    != null && laserIntensity != 0) || 
			(laserAcceleration != null && laserAcceleration.getValue() != 0)) {
			if (laserIntensity == null) {
				laserIntensity = 0; // This is needed when there is an acceleration from 0.
			}
			encoded.add(LASER_MAGIC | laserIntensity);
			header |= LASER_FLAG;
		}
		
		// 10: LASER intensity acceleration (Q16)
		if (laserAcceleration != null && laserAcceleration.getValue() != 0) {
			encoded.add(laserAcceleration.getValue());
			header |= LASER_ACCEL_FLAG;
		}
		
		if (pixelWords != null) {
			encoded.add((long)pixelSpeed.getValue()); // 11: Pixel speed
			encoded.add((long)pixelWords.length); // 11: Pixel word count
			for (long pw : pixelWords) {
				encoded.add(pw);
			}
			header |= PIXEL_FLAG;
		}		
		
		encoded.set(headerIndex, header);
		
		if (log.isLoggable(Level.FINE)) {
			// Log the encoded words for debugging.
			val sb = new StringBuilder();
			sb.append("Encoding move id: ");
			sb.append(id);
			for (val w : encoded) { 
				sb.append(" ");
				sb.append(Long.toHexString(w).toLowerCase());	
			}
			log.fine(sb.toString());
		}		
		
		return encoded;
	}

	static long nudgeAxisStepsCalls = 0;
	public void nudgeAxisSteps(int axis, double diffSteps) {
		nudgeAxisStepsCalls++;
		
		if (getAxis(axis).accel != null) {
			double oldAccel = getAxis(axis).accel.toDouble();
			double addAccel = diffSteps/(duration*duration);
			if (Math.signum(oldAccel+addAccel) != Math.signum(oldAccel)) {
				//throw new RuntimeException("Nudging acceleration would change sign of acceleration "+oldAccel+" "+addAccel);
			}
			getAxis(axis).accel.addDouble(addAccel);

		} else if (getAxis(axis).speed != null) {
			getAxis(axis).speed.addDouble(diffSteps/duration);

		} else {
			throw new RuntimeException("This move has no acceleration and no speed to modify on axis: "+axis+" steperror: "+diffSteps);
		}
	}
}
