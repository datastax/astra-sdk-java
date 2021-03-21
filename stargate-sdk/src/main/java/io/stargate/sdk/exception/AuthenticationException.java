package io.stargate.sdk.exception;

/**
 * Specialized Error.
 *
 * @author Cedrick LUNVEN (@clunven)
 */
public class AuthenticationException extends IllegalStateException {
    
    /** Serial. */
    private static final long serialVersionUID = -4491748257797687008L;
    
    public AuthenticationException(String msg) {
        super(msg);
    }
    
    public AuthenticationException() {
        this("Cannot authenticate, check token and/or credentials");
    }

    public AuthenticationException(String msg, Throwable parent) {
        super(msg, parent);
    }
    
    public AuthenticationException(Throwable parent) {
        this("Cannot authenticate, check token and/or credentials", parent);
    }

}
