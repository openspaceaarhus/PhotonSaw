package dk.osaa.psaw.web.api;

import java.io.IOException;
import java.util.TreeMap;

import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.util.ajax.JSON;

@SuppressWarnings({ "serial" })
public class JSONResult extends TreeMap<String, Object> {

	public void sendResponse(HttpServletResponse response) throws IOException {		
		response.setContentType("application/json");
		response.getOutputStream().print(JSON.toString(this));
	}
}
