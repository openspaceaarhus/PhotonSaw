/**
 * 
 */
package dk.osaa.psaw.job;

import java.awt.geom.AffineTransform;

import lombok.Getter;

/**
 * A node in the job tree which does something with the LASER.
 * 
 * @author Flemming Frandsen <dren.dk@gmail.com> <http://dren.dk>
 */
public abstract class LaserNode extends AbstractJobNode {
	
	@Getter
	LaserNodeSettings settings;
	
	LaserNode(String id, AffineTransform xform, LaserNodeSettings settings) {
		super(id, xform);
		this.settings = settings;
	}	
}
