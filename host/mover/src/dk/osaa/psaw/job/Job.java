package dk.osaa.psaw.job;

import java.io.InputStream;
import java.util.HashSet;
import java.util.TreeMap;

import dk.osaa.psaw.machine.Move;

import lombok.Getter;
import lombok.Setter;

/**
 * A 2D Job, consisting of a JobNodeGroup.
 * 
 * @author ff
 */
public class Job {
	
	/**
	 * The name of the Job, for user interface use.
	 */
	@Getter @Setter
	String name;
	
	/**
	 * The root of the document tree
	 */
	JobNodeGroup rootNode;
	
	/**
	 * The transformation which sets up axis mapping,
	 * this should probably be the only place to do such a transformation and it should probably be the only
	 * transformation that happens at this level.
	 */
	@Getter
	PointTransformation rootTransformation = new PointTransformation();

	HashSet<String> ids = new HashSet<String>();
	
	/**
	 * Use this method to ensure that all IDs used in the document tree are unique, 
	 * @param hint null if you don't have a good idea for an id or the id you'd like
	 * @return an id that's unique within in this Job
	 */
	public String getNodeId(String hint) {
		String newId = hint==null ? "node-0" : hint;
		int count = 1;
		while (ids.contains(newId)) {
			newId = hint+"-"+count++;			
		}
		ids.add(newId);
		return newId;
	}

	JobNodeGroup getRootNode() {
		if (rootNode == null) {
			rootNode = new JobNodeGroup(getNodeId("root"));
		}
		return rootNode;
	}
	
	/**
	 * Returns the node in the job for a given id.
	 * 
	 * @param id The id to look up the node for
	 * @return null if the node isn't found
	 */
	JobNode getNodeById(JobNodeID id) {
		String path[] = id.getPath();
		JobNode node = getRootNode();
		for (String i : path) {
			node = node.getChildById(i);
			if (node == null) {
				return null;
			}
		}
		return node;
	}
	
	/**
	 * Loads an SVG into the Job, the SVG itself is placed as a node group under the root nodegroup
	 * 
	 * @param name The name of the SVG, used to generate an ID and an initial name for it.
	 * @param svgStream The actual data
	 * @return The id of the created node
	 */
	public JobNodeID loadSVG(String name, InputStream svgStream) {
		JobNode svg = SvgLoader.load(this, name, svgStream);
		getRootNode().addChild(svg);
		return svg.getNodeID();
	}
	
	/**
	 * Loads a test job.
	 * @return The test job node.
	 */
	public JobNodeID loadTest() {
		JobNode test = TestLoader.load(this);
		getRootNode().addChild(test);
		return test.getNodeID();
	}	

	/**
	 * Renders the root node onto the target  
	 * @param target Where the job output must go.
	 */
	public void render(JobRenderTarget target) {
		getRootNode().render(target, rootTransformation);
	}
	
	/**
	 * @return An array of booleans, one for each axis, that is true if this Job has a meaningful opinion about the axis. 
	 */
	public boolean[] getUsedAxes() {
		boolean res[] = new boolean[Move.AXES];
		if (rootTransformation.getAxisMapping() == PointTransformation.AxisMapping.XY) {
			res[0] = res[1] = true;
			res[2] = res[3] = false;

		} else if (rootTransformation.getAxisMapping() == PointTransformation.AxisMapping.XA) {
			res[0] = res[3] = true;
			res[1] = res[2] = false;
		
		} else {
			throw new RuntimeException("Axis mapping not known");			
		}		
		return res;
	}
}
