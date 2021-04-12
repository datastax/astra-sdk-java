package io.stargate.sdk.rest.domain;

import java.io.Serializable;

/**
 * Code for table deifnition ordering.
 *
 * @author Cedrick LUNVEN (@clunven)
 */
public class ClusteringExpression implements Serializable {
    
    /** Serial. */
    private static final long serialVersionUID = -910292385355052561L;

    private Ordering order;
    
    private String column;
    
    /**
     * Default Constructor
     */
    public ClusteringExpression() {
    }
    
    public ClusteringExpression(String column, Ordering order) {
        super();
        this.order = order;
        this.column = column;
    }
    
    /**
     * Getter accessor for attribute 'order'.
     *
     * @return
     *       current value of 'order'
     */
    public Ordering getOrder() {
        return order;
    }
    
    /**
     * Setter accessor for attribute 'order'.
     * @param order
     *      new value for 'order '
     */
    public void setOrder(Ordering order) {
        this.order = order;
    }
    
    /**
     * Getter accessor for attribute 'column'.
     *
     * @return
     *       current value of 'column'
     */
    public String getColumn() {
        return column;
    }
    
    /**
     * Setter accessor for attribute 'column'.
     * @param column
     *      new value for 'column '
     */
    public void setColumn(String column) {
        this.column = column;
    }        
}
