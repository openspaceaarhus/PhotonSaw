package dk.osaa.psaw.config;

import java.io.File;

import javax.validation.constraints.NotNull;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonProperty;

import dk.osaa.psaw.machine.Move;
import dk.osaa.psaw.machine.MoveVector;

@Data
@NoArgsConstructor
public class PhotonSawMachineConfig {
	@JsonProperty
	private	String serialPort = "/dev/ttyACM0";
	
	@JsonProperty
	private boolean simulating = false;

	@JsonProperty
	private boolean recording = false;

	@JsonProperty
	private File recordDir;

	@JsonProperty
	@NotEmpty
	private File jobsDir;

	@JsonProperty	
	private int jobsInMemory = 10;

	@JsonProperty	
	private long assistAirDelay=500;

	@JsonProperty	
	private double maximumLaserPower=80;

	@JsonProperty		
	public double junctionDeviation=0.1;

	@JsonProperty	
	private double rapidMoveSpeed=5000;

	@JsonProperty	
	private int tickHZ = 50000;

	@JsonProperty	
	private double shortestMove=0.1;

	@JsonProperty
	@NotNull
	AxesConstraints axes;	
	
	
	private MoveVector mmPerStep;
	public MoveVector getMmPerStep() {
		if (mmPerStep == null) {
			mmPerStep = new MoveVector();
			for (int ax=0;ax<Move.AXES;ax++) {
				mmPerStep.setAxis(ax, axes.getArray()[ax].getMmPerStep());
			}
		}
		return mmPerStep;
	}
}
