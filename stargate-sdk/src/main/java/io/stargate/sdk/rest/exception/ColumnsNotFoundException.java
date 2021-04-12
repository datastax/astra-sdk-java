package io.stargate.sdk.rest.exception;

/**
 * Specialized Error.
 *
 * @author Cedrick LUNVEN (@clunven)
 */
public class ColumnsNotFoundException extends RuntimeException {
    
    /** Serial. */
    private static final long serialVersionUID = -4491748257797687008L;

    public ColumnsNotFoundException(String colName) {
        super("Cannot find Column " + colName);
    }
    
    public ColumnsNotFoundException(String colName, Throwable parent) {
        super("Cannot find Column " + colName, parent);
    }

}
