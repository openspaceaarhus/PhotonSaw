package dk.osaa.psaw.web.resources;

import static com.google.common.base.Preconditions.checkArgument;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.Date;
import java.util.TreeMap;
import java.util.logging.Level;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.StreamingOutput;

import com.google.common.io.Resources;
import com.google.common.net.HttpHeaders;
import com.google.common.net.MediaType;

import io.dropwizard.servlets.assets.ResourceURL;
import lombok.extern.java.Log;

@Log
public class StaticFileServer {
	private static final TreeMap<String, CachedAsset> assetCache = new TreeMap<>();

    private CachedAsset loadAsset(String key) throws URISyntaxException, IOException {
    	synchronized (assetCache) {
	        URL requestedResourceURL = Resources.getResource(key);
	
	        long lastModified = ResourceURL.getLastModified(requestedResourceURL);
	        if (lastModified < 1) {
	            // Something went wrong trying to get the last modified time: just use the current time
	            lastModified = System.currentTimeMillis();
	        }

	        CachedAsset result = assetCache.get(key); 
	    	if (result != null && result.getLastModifiedTime() == lastModified) {
	    		return result;
	    	}
	
	        // zero out the millis since the date we get back from If-Modified-Since will not have them
	        lastModified = (lastModified / 1000) * 1000;
	        result = new CachedAsset(Resources.toByteArray(requestedResourceURL), lastModified);
	        assetCache.put(key, result);
	        return result;
		}
    }

    private static final MediaType DEFAULT_MEDIA_TYPE = MediaType.HTML_UTF_8;
    
	public Response serve(HttpServletRequest req, String string) {
		return serve(req,string,null);
	}
	public Response serve(HttpServletRequest req, String resourceName, MediaType mediaType) {
        try {
            final CachedAsset cachedAsset = loadAsset(resourceName);
            if (cachedAsset == null) {
            	return Response.status(Status.NOT_FOUND).build();
            }

            if (cachedAsset.isCachedClientSide(req)) {
            	return Response.status(Status.NOT_MODIFIED).build();
            }

            StreamingOutput stream = new StreamingOutput() {				
				@Override
				public void write(OutputStream output) throws IOException, WebApplicationException {
					output.write(cachedAsset.getResource());
				}
			};

			ResponseBuilder resp = Response.ok(stream); 
            resp.lastModified(new Date(cachedAsset.getLastModifiedTime()));
            resp.header(HttpHeaders.ETAG, cachedAsset.getETag());

            if (mediaType == null) {
	            final String mimeTypeOfExtension = req.getServletContext()
	                                                  .getMimeType(req.getRequestURI());
	            mediaType = DEFAULT_MEDIA_TYPE;
	
	            if (mimeTypeOfExtension != null) {
	                try {
	                    mediaType = MediaType.parse(mimeTypeOfExtension);
	                } catch (IllegalArgumentException ignore) {}
	            }
            }

            resp.type(mediaType.type() + '/' + mediaType.subtype());

            if (mediaType.charset().isPresent()) {
                resp.header(HttpHeaders.CONTENT_ENCODING, mediaType.charset().get().toString());
            }

            return resp.build();
        } catch (RuntimeException | URISyntaxException | IOException ignored) {
        	log.log(Level.SEVERE, "Failed to serve "+resourceName, ignored);
        	return Response.status(Status.NOT_FOUND).build();
        }
	}
	
}
