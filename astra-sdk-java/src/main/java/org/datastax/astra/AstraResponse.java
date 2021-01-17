package org.datastax.astra;

/**
 * Wrapper for Astra API RESPONSE.
 *
 * @author Cedrick LUNVEN (@clunven)
 *
 * @param <DATA>
 *      returned data by astra
 */
public class AstraResponse<DATA> {
    
    /**
     * Data field is always part of the response
     */
    private DATA data;
    
    public AstraResponse() {}
    
    /**
     * Default Constructor.
     */
    public AstraResponse(DATA t) {
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

}
