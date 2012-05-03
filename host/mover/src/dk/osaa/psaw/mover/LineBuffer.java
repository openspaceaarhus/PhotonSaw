package dk.osaa.psaw.mover;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Lines that are being used for lookahead acceleration planning
 */
public class LineBuffer {

	private ArrayList<Line> buffer = new ArrayList<Line>();	
	private double bufferLength = 0;
	
	/**
	 * @return a read only reference to the underlying array, do not 
	 */
	public List<Line> getList() {
		return Collections.unmodifiableList(buffer);
	}

	/**
	 * Answers the question: Do we have enough lines buffered?
	 * @return true if no more lines are needed
	 */
	public boolean isFull() {
		return bufferLength > 1000 && buffer.size() > 100;
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
