package dk.osaa.psaw.job;

import java.awt.geom.Point2D;

import lombok.Getter;

public class JobNodeEndPoint {
	public JobNodeEndPoint(JobNode node, boolean reversed) {
		this.node = node;
		this.reversed = reversed;
		this.point = reversed ? node.getEndPoint() : node.getStartPoint();
	}

	@Getter
	JobNode node;
	@Getter
	Point2D point;
	@Getter
	boolean reversed;
}
