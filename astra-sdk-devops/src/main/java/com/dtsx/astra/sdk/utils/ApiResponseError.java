package com.dtsx.astra.sdk.utils;

import java.util.List;

/**
 * Hold response.
 */
public class ApiResponseError {

    /**
     * Errors.
     */
    private List<ApiError> errors;

    /**
     * Response Errors
     */
    public ApiResponseError() {}

    /**
     * Gets errors
     *
     * @return value of errors
     */
    public List<ApiError> getErrors() {
        return errors;
    }
}
