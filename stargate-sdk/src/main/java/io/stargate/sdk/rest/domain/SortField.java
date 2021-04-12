package io.stargate.sdk.rest.domain;

public class SortField {
    
    /** reference to field to sort. */
    private String fieldName;
    
    /** Order. */
    private Ordering order;

    public SortField() {}
    
    public SortField(String fieldName, Ordering order) {
        super();
        this.fieldName = fieldName;
        this.order = order;
    }

    /**
     * Getter accessor for attribute 'fieldName'.
     *
     * @return
     *       current value of 'fieldName'
     */
    public String getFieldName() {
        return fieldName;
    }

    /**
     * Setter accessor for attribute 'fieldName'.
     * @param fieldName
     * 		new value for 'fieldName '
     */
    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
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
     * 		new value for 'order '
     */
    public void setOrder(Ordering order) {
        this.order = order;
    }
    
     
    
}
