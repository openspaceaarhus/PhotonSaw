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
	
	@Getter
	double intensity;

	@Getter
	double maxSpeed;
	
	LaserNode(String id, double intensity, double maxSpeed) {
		super(id);
		this.intensity=intensity;
		this.maxSpeed=maxSpeed;
	}	
}
