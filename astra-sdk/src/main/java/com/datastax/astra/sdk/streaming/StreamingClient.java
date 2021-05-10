package com.datastax.astra.sdk.streaming;

import java.util.stream.Stream;

import com.datastax.astra.sdk.streaming.domain.Tenant;
import com.datastax.astra.sdk.utils.ApiDevopsSupport;

/**
 * Group resources of streaming (tenants, providers).
 *
 * @author Cedrick LUNVEN (@clunven)
 */
public class StreamingClient extends ApiDevopsSupport {

    /** Constants. */
    public static final String PATH_STREAMING  = "/streaming";
    
    /**
     * Full constructor.
     */
    public StreamingClient(String token) {
       super(token);
    }
    
    public Stream<Tenant> tenants() {
        System.out.println(bearerAuthToken);
        return null;
    }
}
