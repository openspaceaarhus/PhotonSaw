package dk.osaa.psaw.web.api;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.logging.Level;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lombok.extern.java.Log;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

import dk.osaa.psaw.core.PhotonSaw;

/**
 * All the classes with methods that are callable by the client inherit from this class.
 * Reflection is used by the handle method to find the actual method to call, which must implement the required signature.
 * A default method is also implemented which will list all the methods of the classs that implement the signature. 
 *  
 * @author Flemming Frandsen <dren.dk@gmail.com> <http://dren.dk>
 */
@Log
public class AbstractJSONHandler extends AbstractHandler {
	
	protected PhotonSaw ps;
	protected Request baseRequest;
	protected HttpServletRequest request;
	protected HttpServletResponse response;
	
	String prefix;
	protected AbstractJSONHandler(PhotonSaw ps, String prefix) {
		this.ps = ps;
		this.prefix = prefix;
	}
	
	@Override
	public void handle(String arg0, Request baseRequest, HttpServletRequest request,
			HttpServletResponse response) throws IOException, ServletException {
		this.baseRequest = baseRequest;
		this.request = request;
		this.response = response;		
		
		String path = request.getPathInfo();
		String pathprefix = "/api/"+prefix+"/";
		if (path.startsWith(pathprefix)) {
			String methodName = path.substring(pathprefix.length());
			
			if (methodName.equals("") || methodName.equals("index.html")) {
				methodName = "index";
			}			
			
			Method method;
			try { 
				method = this.getClass().getMethod(methodName, JSONParameters.class);
			} catch (Exception e) {
				log.log(Level.SEVERE, "Unknown method requested: "+this.getClass().getCanonicalName()+"+"+methodName, e);
				response.sendError(501, "Unknown method requested: "+this.getClass().getCanonicalName()+"+"+methodName);							
		        baseRequest.setHandled(true);
		        
				return; 
			}
			
			if (method == null) {
				log.log(Level.SEVERE, "Unknown method requested: "+this.getClass().getCanonicalName()+"+"+methodName);
				response.sendError(501, "Unknown method requested: "+this.getClass().getCanonicalName()+"+"+methodName);							
		        baseRequest.setHandled(true);
			}
			
			JSONParameters parameters=null;			
			if (request.getContentType() != null && request.getContentType().startsWith("application/json")) {
				parameters = new JSONParameters(request); 
			} else {
				log.warning("Request to "+this.getClass().getCanonicalName()+"+"+methodName+" with request type: "+request.getContentType());
			}
			
			JSONResult result = null;
			try {
				result = (JSONResult)method.invoke(this, parameters);
			} catch (Exception e) {
				log.log(Level.SEVERE, "Failed while calling REST method "+this.getClass().getCanonicalName()+"+"+methodName, e);
				
				response.sendError(500, "Failed while processing request, check server logs");
		        baseRequest.setHandled(true);				
				return; 
			}
			
			if (result != null) {
		        response.setStatus(HttpServletResponse.SC_OK);	        
				result.sendResponse(response);
		        baseRequest.setHandled(true);
			}
				
			return;
		}
	}
	
	public JSONResult index(JSONParameters param) throws IOException {
		response.setContentType("text/plain");
		
		response.getOutputStream().print("Methods callable in "+this.getClass().getCanonicalName()+"\n");				
		for (Method m : this.getClass().getMethods()) {
			if (m.getReturnType().equals(JSONResult.class) 
				&& m.getParameterTypes().length == 1 && m.getParameterTypes()[0].equals(JSONParameters.class) 
				&& !m.getName().equals("index")) {
				response.getOutputStream().print("\t"+m.getName()+"\n");				
			}
		}		
		
        response.setStatus(HttpServletResponse.SC_OK);	        
        baseRequest.setHandled(true);
		return null;
	} 

}
