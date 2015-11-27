package dk.osaa.psaw.web.resources;

import javax.servlet.http.HttpServletRequest;

import com.google.common.hash.Hashing;
import com.google.common.net.HttpHeaders;

import lombok.Getter;

@Getter
public class CachedAsset {
    private final byte[] resource;
	private final String eTag;
    private final long lastModifiedTime;

    public CachedAsset(byte[] resource, long lastModifiedTime) {
        this.resource = resource;
        this.eTag = '"' + Hashing.murmur3_128().hashBytes(resource).toString() + '"';
        this.lastModifiedTime = lastModifiedTime;
    }
    
    public boolean isCachedClientSide(HttpServletRequest req) {
        return eTag.equals(req.getHeader(HttpHeaders.IF_NONE_MATCH)) ||
                (req.getDateHeader(HttpHeaders.IF_MODIFIED_SINCE) >= lastModifiedTime);
    }	
}
