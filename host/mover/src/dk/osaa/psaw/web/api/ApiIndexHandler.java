package dk.osaa.psaw.web.api;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lombok.val;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.eclipse.jetty.server.handler.HandlerList;

public class ApiIndexHandler extends AbstractHandler {

	HandlerList handlers;
	public ApiIndexHandler(HandlerList handlers) {
		this.handlers = handlers;
	}

	@Override
	public void handle(String arg0, Request baseRequest, HttpServletRequest request,
			HttpServletResponse response) throws IOException, ServletException {
		
		String path = request.getPathInfo();
		if (!(path.equals("/api") || path.equals("/api/"))) {
			return;
		}

		response.setContentType("text/plain");			
		response.getOutputStream().print("API modules:\n");
		
		for (val h : handlers.getHandlers()) {
			if (h instanceof AbstractJSONHandler) {
				AbstractJSONHandler ah = (AbstractJSONHandler)h;
				response.getOutputStream().print("\t/api/"+ah.getPrefix()+"\t"+ah.getDescription()+"\n");				
			}
		}
				
        response.setStatus(HttpServletResponse.SC_OK);	        
        baseRequest.setHandled(true);		
	}

}
