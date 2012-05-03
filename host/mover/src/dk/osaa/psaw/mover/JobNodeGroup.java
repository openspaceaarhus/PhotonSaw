package dk.osaa.psaw.mover;

import java.util.ArrayList;

import lombok.Getter;
import lombok.Setter;

public class JobNodeGroup implements LineProducerInterface {
	
	@Getter	@Setter String name;
	ArrayList<JobNodeGroup> children = new ArrayList<JobNodeGroup>();

	public Line getLine(PointTransformation transformation) {
		// TODO Auto-generated method stub
		return null;
	}	
	
}
