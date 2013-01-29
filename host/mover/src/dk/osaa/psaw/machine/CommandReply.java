package dk.osaa.psaw.machine;

import java.util.TreeMap;

import lombok.Getter;
import lombok.Setter;
import lombok.val;

public class CommandReply extends TreeMap<String, ReplyValue> {
	private static final long serialVersionUID = 1L;

	@Getter @Setter
	String sentCommand;
	
	public ReplyValue get(String key) {
		val res = super.get(key);
		if (res == null) {
			throw new RuntimeException("Failed to get reply value: "+key+" in "+toString());
		}
		return res;
	}
	
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
