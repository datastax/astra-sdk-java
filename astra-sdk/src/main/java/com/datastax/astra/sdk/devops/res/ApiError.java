package com.datastax.astra.sdk.devops.res;

import java.io.Serializable;

/**
 * Represents error code/message returned from the API.
 *
 * @author Cedrick LUNVEN (@clunven)
 */
public class ApiError implements Serializable {

    /** Serial. */
    private static final long serialVersionUID = 1L;

    /** Identifier for the error = code. */
    private long id;
    
    /** Error message. */
    private String message;
    
    public ApiError() {}
    
    
    /** {@inheritDoc} */
    @Override
    public String toString() {
        return "Returned Error id='" + id + "', message='" + message + "'";
    }
    
    /**
     * Getter accessor for attribute 'iD'.
     *
     * @return
     *       current value of 'iD'
     */
    public long getId() {
        return id;
    }

    /**
     * Setter accessor for attribute 'iD'.
     * @param iD
     * 		new value for 'iD '
     */
    public void setId(long iD) {
        id = iD;
    }

    /**
     * Getter accessor for attribute 'message'.
     *
     * @return
     *       current value of 'message'
     */
    public String getMessage() {
        return message;
    }

    /**
     * Setter accessor for attribute 'message'.
     * @param messages
     * 		new value for 'message '
     */
    public void setMessage(String message) {
        this.message = message;
    }



    
    
}
