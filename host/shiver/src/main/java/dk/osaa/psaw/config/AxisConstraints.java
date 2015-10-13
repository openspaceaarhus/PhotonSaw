package dk.osaa.psaw.config;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@NoArgsConstructor
public class AxisConstraints {
	@JsonProperty
	@NonNull
	private Double acceleration; // mm/s/s

	@JsonProperty
	@NonNull
	private Double maxSpeed;     // mm/s

	@JsonProperty
	@NonNull
	private Double minSpeed;     // mm/s

	@JsonProperty
	@NonNull
	private Double maxJerk;      // mm/s 

	@JsonProperty
	@NonNull
	private Double mmPerStep;    // mm / step

	@Min(100)
	@Max(10000)
	@NonNull
	@JsonProperty
	private Integer coilCurrent;     // mA

	@Min(0)
	@Max(3)
	@NonNull
	@JsonProperty
	private Integer microSteppingMode; // 0=fullstep, 1=halfstep, 2=1/4 step, 3=1/8 step			
}
