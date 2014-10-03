package dk.osaa.psaw.web.resources;

import javax.validation.Valid;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import lombok.AllArgsConstructor;

import com.codahale.metrics.annotation.Timed;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;

import dk.osaa.psaw.core.PhotonSaw;
import dk.osaa.psaw.web.api.JogSpeed;
import dk.osaa.psaw.web.api.JogStatus;

@Path("/jogger")
@Api(value = "/jogger", description = "Jogging the 4 axis machine")
@Produces(MediaType.APPLICATION_JSON)
@AllArgsConstructor
public class Jogger {
	private final PhotonSaw psaw;
	
	@ApiOperation(value = "Starts or maintains the specified speed vector, the machine will time out after 100 ms so the jog command must be repeated more often than that.")
	@POST
	@Timed
	public JogStatus set(@ApiParam(value = "The speed vector of the machine from -1 to 1 in each axis", required = true) @Valid JogSpeed speed) {
		JogStatus result = new JogStatus("Test");
		psaw.setJogSpeed(speed.toMoveVector());
		return result;
	}	
	
}
