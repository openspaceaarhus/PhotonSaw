package dk.osaa.psaw.job;

import lombok.Data;
import dk.osaa.psaw.machine.Point;

@Data
public class PointTransformation {
	enum Rotation {
		NORMAL, 
		LEFT,  
		DOWN,  
		RIGHT  
	};

	static int rotationToInt(Rotation r) {
		if (r == Rotation.NORMAL) {
			return 0;
		} else if (r == Rotation.LEFT) {
			return 1;
		} else if (r == Rotation.DOWN) {
			return 2;
		} else {
			return 3;
		}
	}
	
	static Rotation intToRotation(int r) {
		if ((r & 3) == 0) {
			return Rotation.NORMAL;

		} else if ((r & 3) == 1) {
			return Rotation.LEFT;

		} else if ((r & 3) == 2) {
			return Rotation.DOWN;
		} else {
			return Rotation.RIGHT;
		}
	}
	
	enum AxisMapping {
		XY,
		XA
	};
	
	AxisMapping axisMapping = AxisMapping.XY;
	Rotation rotation = Rotation.NORMAL;
	Point2D offset = new Point2D(0,0);
			
	public Point transform(Point2D p2d) {
		Point res = new Point();
		
		double x;
		double y;
		
		// First handle rotation
		if (rotation == Rotation.NORMAL) {
			x = p2d.x;
			y = p2d.y;

		} else if (rotation == Rotation.LEFT) {
			x = -p2d.y;
			y = p2d.x;

		} else if (rotation == Rotation.DOWN) {
			x = -p2d.x;
			y = -p2d.y;

		} else if (rotation == Rotation.RIGHT) {
			x = p2d.y;
			y = -p2d.x;

		} else {
			throw new RuntimeException("Rotation not implemented");
		}
		
		// Then add the offset
		x += offset.x;
		y += offset.y;
			
		// Then map the 2d point into machine space 
			
		if (axisMapping == AxisMapping.XY) {
			res.axes[0] = x;
			res.axes[1] = y;

		} else if (axisMapping == AxisMapping.XA) {
			res.axes[0] = x;
			res.axes[3] = y;

		} else {
			throw new RuntimeException("AxisMapping not implemented");
		}
		
		return res;
	}
	
	public PointTransformation add(PointTransformation a) {
		if (a == null) {
			return this;
		}
		
		PointTransformation res = new PointTransformation();
		res.axisMapping = this.axisMapping; // Can't remap the axis mapping.
		res.rotation = intToRotation(rotationToInt(this.rotation) + rotationToInt(a.rotation));
		res.offset.x = this.offset.x + a.offset.x; 
		res.offset.y = this.offset.y + a.offset.y; 
				
		return a;
	}
}
