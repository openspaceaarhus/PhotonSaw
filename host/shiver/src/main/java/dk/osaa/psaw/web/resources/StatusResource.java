package dk.osaa.psaw.web.resources;

import java.util.logging.Level;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import lombok.AllArgsConstructor;
import lombok.extern.java.Log;

import com.codahale.metrics.annotation.Timed;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

import dk.osaa.psaw.core.PhotonSaw;
import dk.osaa.psaw.web.api.MachineStatus;

@Log
@Path("/status")
@Api(value = "/status", description = "Getting machine status")
@Produces(MediaType.APPLICATION_JSON)
@AllArgsConstructor
public class StatusResource {
	private final PhotonSaw psaw;
	
	@ApiOperation(value = "Returns the status of the machine.")
	@GET
	@Timed
	public MachineStatus get() {
		try {
			psaw.run("st");
		} catch (Exception e) {
			log.log(Level.SEVERE, "Failed to ask machine for status: ", e);
		}
		return new MachineStatus(psaw.getStatus());
	}	

}
