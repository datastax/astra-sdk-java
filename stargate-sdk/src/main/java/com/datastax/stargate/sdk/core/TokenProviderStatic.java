package com.datastax.stargate.sdk.core;

/**
 * Static token, never expires..
 *
 * @author Cedrick LUNVEN (@clunven)
 */
public class TokenProviderStatic implements ApiTokenProvider {

    /** Reference to token. */
    private String token;
    
    /**
     * Constructor with all parameters.
     *
     * @param token
     */
    public TokenProviderStatic(String token) {
        this.token = token;
    }
    
    /** {@inheritDoc} */
    @Override
    public String getToken() {
        return token;
    }

}
