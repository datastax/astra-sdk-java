package io.stargate.sdk.rest.exception;

/**
 * Specialized Error.
 *
 * @author Cedrick LUNVEN (@clunven)
 */
public class IndexNotFoundException extends RuntimeException {
    
    /** Serial. */
    private static final long serialVersionUID = -4491748257797687008L;

    public IndexNotFoundException(String idxName) {
        super("Cannot find Index " + idxName);
    }
    
    public IndexNotFoundException(String idxName, Throwable parent) {
        super("Cannot find Index " + idxName, parent);
    }

}
