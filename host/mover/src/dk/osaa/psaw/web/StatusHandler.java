package dk.osaa.psaw.web;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

import dk.osaa.psaw.core.PhotonSaw;
import dk.osaa.psaw.core.PhotonSawAPI;

public class StatusHandler extends AbstractHandler {	
	PhotonSawAPI photonSaw;
	
	StatusHandler (PhotonSawAPI ps) {
		photonSaw = ps;
	}

	@Override
	public void handle(String arg0, Request baseRequest, HttpServletRequest request,
			HttpServletResponse response) throws IOException, ServletException {
    	if (request.getPathInfo().equals("/status.json")) {
    		// TODO: Output the machine status as json
    		    		
	        response.setContentType("application/json");
	        response.setStatus(HttpServletResponse.SC_OK);	        
	        //ImageIO.write(image, "png", response.getOutputStream());
	        baseRequest.setHandled(true);
    	}
	}
}
