package io.stargate.sdk.utils;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Wrapper for Astra API RESPONSE.
 *
 * @author Cedrick LUNVEN (@clunven)
 *
 * @param <DATA>
 *      returned data by astra
 */
@JsonIgnoreProperties
public class ApiResponse<DATA> {
    
    /**
     * Data field is always part of the response
     */
    private DATA data;
    
    /**
     * for Page queries
     */
    private String pageState;
    
    /**
     * Default constructor.
     */
    public ApiResponse() {}
    
    /**
     * Default Constructor.
     */
    public ApiResponse(DATA t) {
        this.data = t;
    }

    /**
     * Getter accessor for attribute 'data'.
     *
     * @return
     *       current value of 'data'
     */
    public DATA getData() {
        return data;
    }

    /**
     * Getter accessor for attribute 'pageState'.
     *
     * @return
     *       current value of 'pageState'
     */
    public String getPageState() {
        return pageState;
    }

    /**
     * Setter accessor for attribute 'pageState'.
     * @param pageState
     * 		new value for 'pageState '
     */
    public void setPageState(String pageState) {
        this.pageState = pageState;
    }

    /**
     * Setter accessor for attribute 'data'.
     * @param data
     * 		new value for 'data '
     */
    public void setData(DATA data) {
        this.data = data;
    }

}
