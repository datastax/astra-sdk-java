package org.datastax.astra.doc.dto;

/**
 * All items returned by the document API use a DATA BLOC
 *
 * @author Cedrick LUNVEN (@clunven)
 *
 * @param <PAYLOAD>
 */
public class ApiResponse<PAYLOAD> {
    
    private PAYLOAD data;

    /**
     * Getter accessor for attribute 'data'.
     *
     * @return
     *       current value of 'data'
     */
    public PAYLOAD getData() {
        return data;
    }

    /**
     * Setter accessor for attribute 'data'.
     * @param data
     * 		new value for 'data '
     */
    public void setData(PAYLOAD data) {
        this.data = data;
    }

}
