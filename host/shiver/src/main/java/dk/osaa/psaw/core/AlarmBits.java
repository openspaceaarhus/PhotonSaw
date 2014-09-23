package dk.osaa.psaw.core;

/**
 * Constants that match those defined in alarm.h
 */
public class AlarmBits {
	
	// Limit switches
	public static final int ALARM_SW_X_MIN = 0;
	public static final int ALARM_SW_Y_MIN = 1;
	public static final int ALARM_SW_Z_MIN = 2;
	public static final int ALARM_SW_A_MIN = 3;

	public static final int ALARM_SW_X_MAX = 4;
	public static final int ALARM_SW_Y_MAX = 5;
	public static final int ALARM_SW_Z_MAX = 6;
	public static final int ALARM_SW_A_MAX = 7;

	// Emergency stop was triggered
	public static final int ALARM_SW_ESTOP = 8;

	// Watchdog is unhappy
	public static final int ALARM_WD    = 9;

	// The code was invalid
	public static final int ALARM_CODE    = 10;

	// Coolant flow was too low and the laser was commanded on
	public static final int ALARM_COOLANT_FLOW = 11;

	// Coolant temperature too high and the laser was commanded on
	public static final int ALARM_COOLANT_TEMP_HIGH = 12;

	// The buffer is about to be reset
	public static final int ALARM_RESET = 13;

	// LASER is not ready
	public static final int ALARM_LASER = 14;

	// Motor drivers too hot.
	public static final int ALARM_MOTOR_DRIVER_OVERTEMP = 15;

	// Cooling sensors are giving invalid readings.
	public static final int ALARM_COOLING_SENSORS = 16;

	// Coolant temperature too low and the laser was commanded on
	public static final int ALARM_COOLANT_TEMP_LOW = 17;

}
