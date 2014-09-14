package dk.osaa.psaw.web.resources;

import javax.validation.Valid;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import lombok.AllArgsConstructor;

import com.codahale.metrics.annotation.Timed;
import com.wordnik.swagger.annotations.Api;

import dk.osaa.psaw.core.PhotonSaw;
import dk.osaa.psaw.web.api.JogSpeed;
import dk.osaa.psaw.web.api.JogStatus;

@Api(value = "/jogger", description = "Sets the jogging speed in all axes")
@AllArgsConstructor
@Path("/jogger")
@Produces(MediaType.APPLICATION_JSON)
public class Jogger {
	private final PhotonSaw psaw;
	
	@POST
	@Timed
	public JogStatus set(@Valid JogSpeed speed) {
		JogStatus result = new JogStatus("Test");
		psaw.setJogSpeed(speed.toMoveVector());
		return result;
	}
}
