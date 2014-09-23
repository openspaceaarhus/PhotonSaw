package dk.osaa.psaw.web.api;

import com.fasterxml.jackson.annotation.JsonProperty;

import dk.osaa.psaw.core.AlarmBits;
import lombok.Getter;

@Getter
public class MachineAlarmMask {
	@JsonProperty private boolean limitSwitchXMin;
	@JsonProperty private boolean limitSwitchYMin;
	@JsonProperty private boolean limitSwitchZMin;
	@JsonProperty private boolean limitSwitchAMin;
	@JsonProperty private boolean limitSwitchXMax;
	@JsonProperty private boolean limitSwitchYMax;
	@JsonProperty private boolean limitSwitchZMax;
	@JsonProperty private boolean limitSwitchAMax;
	@JsonProperty private boolean emergencyStop;
	@JsonProperty private boolean watchdogTriggered;
	@JsonProperty private boolean badInputCode;
	@JsonProperty private boolean resetting;
	@JsonProperty private boolean laserNotReady;	
	@JsonProperty private boolean motorDriverOverTemperature;
	@JsonProperty private boolean coolingSensors;
	@JsonProperty private boolean coolantFlowLow;
	@JsonProperty private boolean coolantTemperatureLow;
	@JsonProperty private boolean coolantTemperatureHigh;
	
	public MachineAlarmMask(long bitfield) {
		if ((bitfield & (AlarmBits.ALARM_SW_X_MIN<<1)) != 0) {
			limitSwitchXMin = true;
		}
		
		if ((bitfield & (AlarmBits.ALARM_SW_Y_MIN<<1)) != 0) {
			limitSwitchYMin = true;
		}
		
		if ((bitfield & (AlarmBits.ALARM_SW_Z_MIN<<1)) != 0) {
			limitSwitchZMin = true;
		}
		
		if ((bitfield & (AlarmBits.ALARM_SW_A_MIN<<1)) != 0) {
			limitSwitchAMin = true;
		}
		
		if ((bitfield & (AlarmBits.ALARM_SW_X_MAX<<1)) != 0) {
			limitSwitchXMax = true;
		}
		
		if ((bitfield & (AlarmBits.ALARM_SW_Y_MAX<<1)) != 0) {
			limitSwitchYMax = true;
		}
		
		if ((bitfield & (AlarmBits.ALARM_SW_Z_MAX<<1)) != 0) {
			limitSwitchZMax = true;
		}
		
		if ((bitfield & (AlarmBits.ALARM_SW_A_MAX<<1)) != 0) {
			limitSwitchAMax = true;
		}

		if ((bitfield & (AlarmBits.ALARM_SW_ESTOP<<1)) != 0) {
			emergencyStop = true;
		}

		if ((bitfield & (AlarmBits.ALARM_WD<<1)) != 0) {
			watchdogTriggered = true;
		}

		if ((bitfield & (AlarmBits.ALARM_CODE<<1)) != 0) {
			badInputCode = true;
		}

		if ((bitfield & (AlarmBits.ALARM_COOLANT_FLOW<<1)) != 0) {
			coolantFlowLow = true;
		}

		if ((bitfield & (AlarmBits.ALARM_COOLANT_TEMP_HIGH<<1)) != 0) {
			coolantTemperatureHigh = true;
		}

		if ((bitfield & (AlarmBits.ALARM_COOLANT_TEMP_LOW<<1)) != 0) {
			coolantTemperatureLow = true;
		}

		if ((bitfield & (AlarmBits.ALARM_RESET<<1)) != 0) {
			resetting = true;
		}

		if ((bitfield & (AlarmBits.ALARM_LASER<<1)) != 0) {
			laserNotReady = true;
		}

		if ((bitfield & (AlarmBits.ALARM_MOTOR_DRIVER_OVERTEMP<<1)) != 0) {
			motorDriverOverTemperature = true;
		}

		if ((bitfield & (AlarmBits.ALARM_COOLING_SENSORS<<1)) != 0) {
			coolingSensors = true;
		}		
	}
	
}
