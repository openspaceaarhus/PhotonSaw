package dk.osaa.psaw.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Lines that are being used for lookahead acceleration planning
 */
public class LineBuffer {

	public static final int BUFFER_SIZE = 100;
	public static final int BUFFER_LENGTH = 200;
	
	private ArrayList<Line> buffer = new ArrayList<Line>();	
	private double bufferLength = 0;
	
	/**
	 * @return a read only reference to the underlying array, do not 
	 */
	public List<Line> getList() {
		return Collections.unmodifiableList(buffer);
	}

	/**
	 * Answers the question: Do we have enough lines buffered to do effective planning?
	 * It's possible to add more lines even after this returns true, but it only impacts performance negatively.
	 * @return true if no more lines are needed
	 */
	public boolean isFull() {
		return bufferLength > BUFFER_LENGTH && buffer.size() > BUFFER_SIZE;
	}
	
	/**
	 * @return The length in mm of the buffered lines
	 */
	public double getBufferLength() {
		return bufferLength;
	}

	/**
	 * @return The length in mm of the buffered lines
	 */
	public int getBufferSize() {
		return buffer.size();
	}
	

	/**
	 * @return True if no lines are buffered
	 */
	public boolean isEmpty() {
		return buffer.isEmpty();
	}
	
	/**
	 * @return The oldest line from the buffer
	 */
	public Line shift() {
		Line l = buffer.remove(0);
		bufferLength -= l.getLength();		
		return l;
	}
	
	/**
	 * Adds a new line to the lookahead buffer
	 * @param newLine The line to add
	 */
	public void push(Line newLine) {
		bufferLength += newLine.getLength();
		buffer.add(newLine);
	}
}
