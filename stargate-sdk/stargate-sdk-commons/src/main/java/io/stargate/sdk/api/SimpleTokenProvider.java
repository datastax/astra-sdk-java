package io.stargate.sdk.api;

/**
 * Static token, never expires..
 *
 * @author Cedrick LUNVEN (@clunven)
 */
public class SimpleTokenProvider implements TokenProvider {

    /** Reference to token. */
    private String token;
    
    /**
     * Constructor with all parameters.
     *
     * @param token
     *      static token to be used
     */
    public SimpleTokenProvider(String token) {
        this.token = token;
    }
    
    /** {@inheritDoc} */
    @Override
    public String getToken() {
        return token;
    }

}
