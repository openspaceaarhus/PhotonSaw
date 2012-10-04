/**
 * 
 */
package dk.osaa.psaw.job;

import lombok.Getter;

/**
 * A node in the job tree which does something with the LASER.
 * 
 * @author Flemming Frandsen <dren.dk@gmail.com> <http://dren.dk>
 */
public abstract class LaserNode extends AbstractJobNode {
	
	LaserNodeSettings settings;
	
	LaserNode(String id, LaserNodeSettings settings) {
		super(id);
		this.settings = settings;
	}	
}
