package dk.osaa.psaw.machine;

import java.util.TreeMap;

import lombok.Getter;
import lombok.Setter;
import lombok.val;

public class CommandReply {
	private static final long serialVersionUID = 1L;

	@Getter @Setter
	String sentCommand;
	
	public void startCommand(String sentCommand) {
		this.sentCommand = sentCommand;
		kv.clear();
	}
	
	private TreeMap<String, ReplyValue> kv = new TreeMap<>();
	
	public ReplyValue get(String key) {
		val res = kv.get(key);
		if (res == null) {
			throw new RuntimeException("Failed to get reply value: "+key+" in "+toString());
		}
		return res;
	}
	
	public String toString() {
		StringBuilder res = new StringBuilder();
		for (String name : kv.keySet()) {
			res.append(get(name).toString());
			res.append("\n");
		}		
		return res.toString();
	}
	
	public CommandReply clone() {
		CommandReply c = new CommandReply();
		c.sentCommand = sentCommand;
		c.kv = (TreeMap<String, ReplyValue>)kv.clone();
		return c;
	}

	public void clearReply() {
		kv.clear();
	}

	public void add(ReplyValue value) {
		kv.put(value.getName(), value);
	}

	public void setOkResult() {
		if (!kv.containsKey("result")) {
			add(new ReplyValue("result OK"));
		}
	}

	public Long getLong(String key) {
		if (kv.containsKey(key)) {
			return kv.get(key).getInteger();
		}
		return null;
	}

	public Long getHex(String key) {
		if (kv.containsKey(key)) {
			return kv.get(key).getHex();
		}
		return null;
	}

	public String getString(String key) {
		if (kv.containsKey(key)) {
			return kv.get(key).getString();
		}
		return null;
	}

	public Boolean getBoolean(String key) {
		if (kv.containsKey(key)) {
			return kv.get(key).getBoolean();
		}
		return null;
	}

	public Double getDouble(String key) {
		if (kv.containsKey(key)) {
			return kv.get(key).getDouble();
		}
		return null;
	}

	
	
}
