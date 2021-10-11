package com.datastax.stargate.sdk.core;

/**
 * Static token, never expires..
 *
 * @author Cedrick LUNVEN (@clunven)
 */
public class ApiTokenProviderFixed implements ApiTokenProvider {

    /** Reference to token. */
    private String token;
    
    /**
     * Constructor with all parameters.
     *
     * @param token
     *      static token to be used
     */
    public ApiTokenProviderFixed(String token) {
        this.token = token;
    }
    
    /** {@inheritDoc} */
    @Override
    public String getToken() {
        return token;
    }

}
