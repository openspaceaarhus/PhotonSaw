package dk.osaa.psaw.web.resources;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import lombok.AllArgsConstructor;
import lombok.extern.java.Log;

import com.codahale.metrics.annotation.Timed;
import com.sun.jersey.api.representation.Form;
import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataBodyPart;
import com.sun.jersey.multipart.FormDataMultiPart;
import com.sun.jersey.multipart.FormDataParam;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

import dk.osaa.psaw.core.PhotonSaw;
import dk.osaa.psaw.web.api.StartPageData;
import dk.osaa.psaw.web.view.StartPageDataView;

@Path("/")
@Api(value = "Index-Page", description = "Start page")
@Produces(MediaType.TEXT_HTML)
@Log
@AllArgsConstructor
public class StartPage {
	private final PhotonSaw psaw;
	
	@ApiOperation(value = "The start page")
	@GET
	@Timed
	public StartPageDataView get() {
		StartPageData spd = new StartPageData();
		spd.setHello("<strong>World</strong>");
		for (int i=0;i<10;i++) {
			ArrayList<String> row = new ArrayList<>();
			for (int j=0;j<10;j++) {
				row.add(new Integer(i*j).toString());
			}			
			spd.getRows().add(row);
		}
		return new StartPageDataView(spd);
	}
	
	@ApiOperation(value = "Test POST target")
	@POST
	@Path("/pt/withfile")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Timed
	public StartPageDataView postWithFile(
			@PathParam("hmm") final String hmm,
			@FormDataParam("file") final InputStream fileInputStream,
	        @FormDataParam("file") final FormDataContentDisposition contentDispositionHeader,
	        @FormDataParam("test") String hest) {
		StartPageData spd = new StartPageData();
		return new StartPageDataView(spd);
	}
	
	
	@ApiOperation(value = "Test POST target")
	@POST
	@Path("/pt/postage")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Timed
	public StartPageDataView postWithoutFile(
			@FormDataParam("test1") String hest1,
			@FormDataParam("test2") String hest2,
			FormDataMultiPart mooltipart 
			) {
		Map<String, List<FormDataBodyPart>> params = mooltipart.getFields();
		
		for (String name : params.keySet()) {
			for (FormDataBodyPart value : params.get(name)) {
				log.info(name+"="+value.getValue());
			}
		} 
		
		StartPageData spd = new StartPageData();
		return new StartPageDataView(spd);
	}
	
	

}
