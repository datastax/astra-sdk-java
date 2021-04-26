package com.datastax.stargate.sdk.rest.exception;

/**
 * Specialized Error.
 *
 * @author Cedrick LUNVEN (@clunven)
 */
public class TableNotFoundException extends RuntimeException {
    
    /** Serial. */
    private static final long serialVersionUID = -4491748257797687008L;

    public TableNotFoundException(String colName) {
        super("Cannot find Table " + colName);
    }
    
    public TableNotFoundException(String colName, Throwable parent) {
        super("Cannot find Table " + colName, parent);
    }

}
