package dk.osaa.psaw.web.api;

import java.util.regex.Pattern;

import lombok.Getter;
import lombok.val;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MachineAlarm {
	
	static final Pattern LINE = Pattern.compile("ts:(\\d+) mid:(\\d+) mo:(\\d+) sw:([\\da-f]+) msg:(.+)"); 
	
	public MachineAlarm(String id, String line) {
		val m = LINE.matcher(line);
		if (m.matches()) {
			this.id        = Integer.parseInt(id);
			timestamp = Integer.parseInt(m.group(1));
			moveId = Integer.parseInt(m.group(2));
			moveCodeOffset = Integer.parseInt(m.group(3));
			alarmMask = new MachineAlarmMask(Integer.parseInt(m.group(4), 16));
			message = m.group(5);
		}
	} 

	@Getter
	@JsonProperty
	int id;
	
	@Getter
	@JsonProperty
	long timestamp;
	
	@Getter
	@JsonProperty
	long moveId;
	
	@Getter
	@JsonProperty
	long moveCodeOffset;
	
	@Getter
	@JsonProperty
	MachineAlarmMask alarmMask;
	
	@Getter
	@JsonProperty
	String message;
}
