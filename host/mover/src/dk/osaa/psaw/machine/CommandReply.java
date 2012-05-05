package dk.osaa.psaw.machine;

import java.util.TreeMap;

public class CommandReply extends TreeMap<String, ReplyValue> {
	private static final long serialVersionUID = 1L;

	public String toString() {
		StringBuilder res = new StringBuilder();
		for (String name : keySet()) {
			res.append(get(name).toString());
			res.append("\n");
		}		
		return res.toString();
	}
	
	public CommandReply clone() {
		return (CommandReply)super.clone();
	}
}
