package dk.osaa.psaw.mover;

public class MoveVector {
	double axes[] = new double[Move.AXES];

	public double getAxis(int ax) {
		return axes[ax];
	}	

	public void setAxis(int ax, double v) {
		axes[ax] = v;
	}
	
	public MoveVector mul(double factor) {
		MoveVector r = new MoveVector();
		for (int i=0;i<Move.AXES;i++) {
			r.setAxis(i, axes[i]*factor);			
		}		
		return r;
	}
	
	public double length() {
		double length = 0;		
		for (int a=0;a<Move.AXES;a++) {
			length += Math.pow(axes[a], 2);
		}
		return Math.sqrt(length);		
	}
	
	public MoveVector unit() {
		return mul(1/length());
	}
}
