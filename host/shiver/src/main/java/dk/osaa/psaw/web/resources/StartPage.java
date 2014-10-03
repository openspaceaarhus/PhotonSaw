package dk.osaa.psaw.web.resources;

import java.util.ArrayList;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import lombok.AllArgsConstructor;

import com.codahale.metrics.annotation.Timed;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

import dk.osaa.psaw.core.PhotonSaw;
import dk.osaa.psaw.web.api.StartPageData;
import dk.osaa.psaw.web.view.StartPageDataView;

@Path("/")
@Api(value = "/index.html", description = "Start page")
@Produces(MediaType.TEXT_HTML)
@AllArgsConstructor
public class StartPage {
	private final PhotonSaw psaw;
	
	@ApiOperation(value = "The start page")
	@GET
	@Timed
	public StartPageDataView get() {
		StartPageData spd = new StartPageData();
		spd.setHello("<strong>World</strong>");
		for (int i=0;i<100;i++) {
			ArrayList<String> row = new ArrayList<>();
			for (int j=0;j<100;j++) {
				row.add(new Integer(i*j).toString());
			}			
			spd.getRows().add(row);
		}
		return new StartPageDataView(spd);
	}	

}
