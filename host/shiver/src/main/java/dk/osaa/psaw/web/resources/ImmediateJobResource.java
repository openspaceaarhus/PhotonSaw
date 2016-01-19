package dk.osaa.psaw.web.resources;

import java.io.InputStream;

import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

import lombok.AllArgsConstructor;
import lombok.extern.java.Log;

import com.codahale.metrics.annotation.Timed;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiImplicitParam;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;

import dk.osaa.psaw.core.PhotonSaw;
import dk.osaa.psaw.web.api.JobStartStatus;

@Log
@Path("/job/immediate")
@Api(value = "/job", description = "Builds a job from an SVG and starts the job immediatly")
@Produces(MediaType.APPLICATION_JSON)
@AllArgsConstructor
public class ImmediateJobResource {
	private final PhotonSaw psaw;
		
	@Timed
	@ApiOperation(value = "Builds a job from an SVG and starts the job")
	@POST
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public JobStartStatus start(
			@QueryParam("fest") Boolean fest,
		    @FormDataParam("hest") Boolean hest,
		    @FormDataParam("file") InputStream is,
		    @FormDataParam("file") FormDataContentDisposition fileDetail) {
		
		log.info("Got file");
		
		return new JobStartStatus(fileDetail.getFileName());
	}	
	
}
