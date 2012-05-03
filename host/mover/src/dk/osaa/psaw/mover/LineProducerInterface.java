package dk.osaa.psaw.mover;

/**
 * Something that can produce lines
 * 
 * @author ff
 */
public interface LineProducerInterface {
	public Line getLine(PointTransformation transformation);	
}
