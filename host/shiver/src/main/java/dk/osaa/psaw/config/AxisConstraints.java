package dk.osaa.psaw.config;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AxisConstraints {
	@JsonProperty
	@NotNull
	@Min(0)
	private Double acceleration; // mm/s/s

	@JsonProperty
	@Min(10)
	@Max(100000)
	private Double maxSpeed;     // mm/s

	@JsonProperty
	@Min(0)
	private Double minSpeed;     // mm/s

	@JsonProperty
	@Min(0)
	private Double maxJerk;      // mm/s 

	@JsonProperty
	private Double mmPerStep;    // mm / step

	@Min(100)
	@Max(2500)
	@JsonProperty
	private Integer coilCurrent;     // mA

	@Min(0)
	@Max(3)
	@JsonProperty
	private Integer microSteppingMode; // 0=fullstep, 1=halfstep, 2=1/4 step, 3=1/8 step			
}
