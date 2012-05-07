package dk.osaa.psaw.job;

import java.util.ArrayList;

/**
 * Loads a test path into the Job
 * 
 * @author Flemming Frandsen <dren.dk@gmail.com> <http://dren.dk>
 */
public class TestLoader {

	ArrayList<Point2D> path = new ArrayList<Point2D>();
	void addPoint(double x, double y) {
		path.add(new Point2D(x,y));
	}

	public static JobNode load(Job job) {
				
		TestLoader tl = new TestLoader();

		tl.addPoint(0,0);
		tl.addPoint(30, 60);
		
		for (int i=1;i<60+1;i++) {
			tl.addPoint(0.2*i, 1.4*i);
		}
				
		final int N = 50;
		for (int i=0;i<N*10;i++) {
			tl.addPoint(((i)/N)*30*Math.sin((i*Math.PI*2)/N),
					((i)/N)*60*Math.cos((i*Math.PI*2)/N));			
		}
		
		tl.addPoint(0,0);
		
		JobNodeGroup test = new JobNodeGroup(job.getNodeId("test"));
		test.addChild(new CutPath(job.getNodeId("testcut"), tl.path, 0.5, 1000));
		return test;
	}

}
