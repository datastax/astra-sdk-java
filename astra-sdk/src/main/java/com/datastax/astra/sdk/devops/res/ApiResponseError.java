package com.datastax.astra.sdk.devops.res;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Response Wrapper.
 *
 * @author Cedrick LUNVEN (@clunven)
 */
public class ApiResponseError implements Serializable {

    /**
     * Serial.
     */
    private static final long serialVersionUID = 4340469194989763449L;
    
    /**
     * Error List.
     */
    private List<ApiError> errors = new ArrayList<>();

    /**
     * Getter accessor for attribute 'errors'.
     *
     * @return
     *       current value of 'errors'
     */
    public List<ApiError> getErrors() {
        return errors;
    }

    /**
     * Setter accessor for attribute 'errors'.
     * @param errors
     * 		new value for 'errors '
     */
    public void setErrors(List<ApiError> errors) {
        this.errors = errors;
    }
}
