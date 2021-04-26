package com.datastax.stargate.sdk.core;

import java.io.Serializable;

/**
 * Specialized error for Stargate.
 * 
 * @author Cedrick LUNVEN (@clunven)
 */
public class ApiError implements Serializable {
    
    /** Serial number. */
    private static final long serialVersionUID = 1312739916968639792L;

    /** Description. */
    private String description;
    
    /** Code. */
    private int code;

    /**
     * Getter accessor for attribute 'description'.
     *
     * @return
     *       current value of 'description'
     */
    public String getDescription() {
        return description;
    }

    /**
     * Setter accessor for attribute 'description'.
     * @param description
     * 		new value for 'description '
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Getter accessor for attribute 'code'.
     *
     * @return
     *       current value of 'code'
     */
    public int getCode() {
        return code;
    }

    /**
     * Setter accessor for attribute 'code'.
     * @param code
     * 		new value for 'code '
     */
    public void setCode(int code) {
        this.code = code;
    }
    
    

}
