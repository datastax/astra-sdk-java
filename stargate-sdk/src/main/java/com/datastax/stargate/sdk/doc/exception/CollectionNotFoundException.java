package com.datastax.stargate.sdk.doc.exception;

/**
 * Specialized Error.
 *
 * @author Cedrick LUNVEN (@clunven)
 */
public class CollectionNotFoundException extends RuntimeException {
    
    /** Serial. */
    private static final long serialVersionUID = -4491748257797687008L;

    public CollectionNotFoundException(String colName) {
        super("Cannot find Collection " + colName);
    }
    
    public CollectionNotFoundException(String colName, Throwable parent) {
        super("Cannot find Collection " + colName, parent);
    }

}
