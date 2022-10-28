package com.dtsx.astra.sdk.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * Response HTTP.
 *
 * @author Cedrick LUNVEN (@clunven)
 */
public class ApiResponseHttp {
    
    /** JSON String. */
    private final String body;
    
    /** Http status code. */
    private final int code;
    
    /** Http Headers. **/
    private Map<String, String> headers = new HashMap<>();
    
    /**
     * Defaut constructor.
     * 
     * @param body
     *      request body
     * @param code
     *      request code
     */
    public ApiResponseHttp(String body, int code) {
        this.body = body;
        this.code = code;
    }
    
    /**
     * Full constructor.
     * 
     * @param body
     *      request body
     * @param code
     *      request code
     * @param headers
     *      request headers      
     */
    public ApiResponseHttp(String body, int code, Map<String, String> headers) {
        this.body = body;
        this.code = code;
        this.headers = headers;
    }

    /**
     * Getter accessor for attribute 'body'.
     *
     * @return
     *       current value of 'body'
     */
    public String getBody() {
        return body;
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
     * Getter accessor for attribute 'headers'.
     *
     * @return
     *       current value of 'headers'
     */
    public Map<String, String> getHeaders() {
        return headers;
    }
    

}
