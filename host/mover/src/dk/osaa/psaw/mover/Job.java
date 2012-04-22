package dk.osaa.psaw.mover;

import java.util.ArrayList;

import lombok.Data;

/**
 * A 2D Job, which can be mapped to X-Y or X-A in machine space by the Planner.
 * 
 * @author ff
 */
@Data
public class Job {

	ArrayList<LinePath> paths = new ArrayList<LinePath>();
	// TODO: Add a list of engrave-able images
}
