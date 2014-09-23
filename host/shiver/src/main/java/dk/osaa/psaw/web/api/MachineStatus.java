package dk.osaa.psaw.web.api;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.val;

import com.fasterxml.jackson.annotation.JsonProperty;

import dk.osaa.psaw.core.PhotonSawStatus;

@Getter
@AllArgsConstructor
public class MachineStatus {
	private static final String[] AXES_NAMES = new String[] {"X","Y","Z","A"};

	@JsonProperty
	private Long watchdogStateAge;
	
	@JsonProperty
	private String watchdogState;
	
	@JsonProperty
	private Long exhaustAirflow;
	
	@JsonProperty 
	private Long exhaustAirflowAdc;
	
	@JsonProperty
	private Boolean exhaustRunning;
	
	@JsonProperty
	private Double boardTemperature;
	
	@JsonProperty
	private Long boardInputVoltage;
	
	@JsonProperty
	private MachineAlarmMask coolingAlarms;

	@JsonProperty
	private Double coolingFlow;

	@JsonProperty
	private Long coolingFlowRaw;

	@JsonProperty
	private Double coolingPower;

	@JsonProperty
	private Double coolingTempIn;

	@JsonProperty
	private Double coolingTempOut;

	@JsonProperty
	private Long sysIrqInterval;

	@JsonProperty
	private Long sysIrqMax;

	@JsonProperty
	private Double sysIrqAvg;

	@JsonProperty
	private Long sysTime;

	@JsonProperty
	private MachineAlarmMask alarmIgnoreMask; 
	
	@JsonProperty
	private ArrayList<MachineAlarm> alarms;

	@JsonProperty
	private Long bufferSize;

	@JsonProperty
	private Long bufferFree;

	@JsonProperty
	private Long bufferInUse;

	@JsonProperty
	private Boolean bufferEmpty;

	@JsonProperty
	private Boolean bufferFull;

	@JsonProperty
	private Boolean motionActive;
	
	@JsonProperty
	private Long motionDuration;

	@JsonProperty
	private Long motionMoveId;

	@JsonProperty
	private Long motionMoveOffset;  

	@JsonProperty
	private Map<String, MachineAxisStatus> motionAxes; 
	
	public MachineStatus(PhotonSawStatus ps) {
		
		
		// See: state.c # printState
		val hw = ps.getHardwareStatus();
		if (hw == null) {
			return;
		}
		watchdogStateAge = hw.getLong("watchdog.state.age");
		watchdogState = hw.getString("watchdog.state");
		exhaustAirflow = hw.getLong("exhaust.airflow");
		exhaustAirflowAdc = hw.getLong("exhaust.airflow.adc");
		exhaustRunning = hw.getBoolean("exhaust.running");
		boardTemperature = hw.getDouble("board.temperature");
		boardInputVoltage = hw.getLong("board.inputvoltage");
		Long coolingAlarmBits = hw.getLong("cooling.alarm");
		if (coolingAlarmBits != null) {
			coolingAlarms = new MachineAlarmMask(coolingAlarmBits);
		}
		coolingFlow = hw.getDouble("cooling.flow");
		coolingFlowRaw = hw.getLong("cooling.flow.raw");
		coolingPower = hw.getDouble("cooling.power");
		coolingTempIn = hw.getDouble("cooling.temp.in");
		coolingTempOut = hw.getDouble("cooling.temp.out");
		sysIrqInterval = hw.getLong("sys.irq.interval");
		sysIrqMax      = hw.getLong("sys.irq.max");
		sysIrqAvg      = hw.getDouble("sys.irq.avg");
		sysTime = hw.getLong("sys.time");

		// See: state.c # printAlarmState
		Long alarmIgnoreMaskBits = hw.getHex("alarm.ignoremask");
		if (alarmIgnoreMaskBits != null) {
			alarmIgnoreMask = new MachineAlarmMask(alarmIgnoreMaskBits);			
		}
		
		alarms = new ArrayList<>();
		String alarmIds = hw.getString("alarm.ids");
		if (alarmIds != null) {
			for (String id : alarmIds.split(",")) {
				String alarmString = hw.getString("alarm."+id);
				if (alarmString != null) {
					alarms.add(new MachineAlarm(id, alarmString));
				}
			}
		}
		
		// See: state.c # printBufferState		
		bufferSize = hw.getLong("buffer.size");
		bufferFree = hw.getLong("buffer.free");
		bufferInUse = hw.getLong("buffer.inuse");
		bufferEmpty = hw.getBoolean("buffer.empty");
		bufferFull = hw.getBoolean("buffer.full");
		
		// See: state.c # printMotionState
		motionActive = hw.getBoolean("motion.active");
		motionDuration = hw.getLong("motion.duration");
		motionMoveId = hw.getHex("motion.move.id");
		motionMoveOffset = hw.getLong("motion.move.offset");
		motionAxes = new TreeMap<>();
		for (String axis : AXES_NAMES) {
			motionAxes.put(axis, new MachineAxisStatus(
					hw.getLong("motion.axis."+axis+".pos"),
					hw.getDouble("motion.axis."+axis+".speed"),
					hw.getDouble("motion.axis."+axis+".accel")
					));
		}
	}
}
