package com.datastax.astra.sdk.utils;

import com.datastax.stargate.sdk.core.ApiTokenProvider;

/**
 * Static token, never expires..
 *
 * @author Cedrick LUNVEN (@clunven)
 */
public class AstraTokenProvider implements ApiTokenProvider {

    /** Reference to token. */
    private String astraToken;
    
    /**
     * Constructor with all parameters.
     *
     * @param token
     *      authentication token
     */
    public AstraTokenProvider(String token) {
        this.astraToken = token;
    }
    
    /** {@inheritDoc} */
    @Override
    public String getToken() {
        return astraToken;
    }

}
