package dk.osaa.psaw.web.api;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.eclipse.jetty.util.ajax.JSON;

import lombok.Data;

@Data
public class JSONParameters {
	
	Object data;
	
	@SuppressWarnings("unchecked")
	public Map<String, Object> getMap() {
		if (data instanceof Map) {
			return (Map<String, Object>)data;
		} else {
			return null;
		}
	}
	
	public String getString(String key) {
		Object o = getMap().get(key);
		if (o instanceof String) {
			return (String)o;
		} else {
			return o.toString();
		}
	}
	
	public JSONParameters(HttpServletRequest request) throws IOException {
		assert(request.getContentType().equals("application/json"));
		data = JSON.parse(new InputStreamReader(request.getInputStream()));
	}

}
