package dk.osaa.psaw.core;

import dk.osaa.psaw.job.TraverseSettings;
import lombok.Getter;
import dk.osaa.psaw.job.JobRenderTarget;
import dk.osaa.psaw.job.LaserNodeSettings;
import dk.osaa.psaw.machine.Move;
import dk.osaa.psaw.machine.Point;

public class JobSize implements JobRenderTarget {

	@Getter
	private int lineCount = 0;

	@Getter
	private double lineLength = 0;
	private Point pos;

	private Planner p;
	public JobSize(Planner p) {
		this.p = p;
		pos = p.lastBufferedLocation;
	}
	
	@Override
	public void cutTo(Point p, LaserNodeSettings settings) {
		traverseTo(p, TraverseSettings.FAST);
	}

	@Override
	public void engraveTo(Point p, LaserNodeSettings settings, boolean[] pixels) {
		traverseTo(p, TraverseSettings.FAST);
	}

	@Override
	public double getEngravingYStepSize() {
		return p.getEngravingYStepSize();
	}

	@Override
	public void setAssistAir(boolean on) {
		// Ignore
	}

	@Override
	public void startShape(String id) {
		// Ignore
	}

	@Override
	public void traverseTo(Point p, TraverseSettings settings) {
		double s = 0;
		for (int i=0;i<Move.AXES;i++) {
			s += Math.pow(p.axes[i]-pos.axes[i], 2);
		}
		lineLength += Math.sqrt(s);
		lineCount++;
	}
}
