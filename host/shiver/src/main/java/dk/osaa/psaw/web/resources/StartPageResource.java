package dk.osaa.psaw.web.resources;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.codahale.metrics.annotation.Timed;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;


@Path("/")
@Api(value = "Index-Page", description = "Start page")
@Produces(MediaType.TEXT_HTML)
public class StartPageResource {
	
	private static final StaticFileServer staticFileServer = new StaticFileServer();
	
	@ApiOperation(value = "The start page")
	@GET
	@Timed
	public Response get(@Context HttpServletRequest req) {
		return staticFileServer.serve(req, "static/index.html");
	}
}
