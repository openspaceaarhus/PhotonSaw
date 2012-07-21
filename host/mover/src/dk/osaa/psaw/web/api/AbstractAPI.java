package dk.osaa.psaw.web.api;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Level;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lombok.extern.java.Log;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

import dk.osaa.psaw.core.PhotonSawAPI;

/**
 * All the classes with methods that are callable by the client inherit from this class.
 * Reflection is used by the handle method to find the actual method to call, which must implement the required signature.
 * A default method is also implemented which will list all the methods of the classs that implement the signature. 
 *  
 * @author Flemming Frandsen <dren.dk@gmail.com> <http://dren.dk>
 */
@Log
public class AbstractAPI extends AbstractHandler {
	
	PhotonSawAPI ps;
	String prefix;
	protected AbstractAPI(PhotonSawAPI ps, String prefix) {
		this.ps = ps;
		this.prefix = prefix;
	}

	Request baseRequest;
	HttpServletRequest request;
	HttpServletResponse response;
	
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
				method = this.getClass().getMethod(methodName, RESTCallParameters.class);
			} catch (Exception e) {
				log.log(Level.SEVERE, "Unknown REST method requested: "+this.getClass().getCanonicalName()+"+"+methodName, e);
				response.sendError(501, "Unknown REST method requested: "+this.getClass().getCanonicalName()+"+"+methodName);							
				return; 
			}
			
			RESTCallParameters parameters=null; // TODO parse out the parameters and stick them in this.
			
			RESTCallResult result; 
			try {
				result = (RESTCallResult)method.invoke(parameters);
			} catch (Exception e) {
				log.log(Level.SEVERE, "Failed while calling REST method "+this.getClass().getCanonicalName()+"+"+methodName, e);
				
				response.sendError(500, "Failed while processing request, check server logs");							
				return; 
			}
			
			if (result != null) {
				// TODO: json encode the resulting data and send it to the client.
				
			}
				
			return;
		}
	}

}
