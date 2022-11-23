package io.stargate.sdk.rest.domain;

import java.io.Serializable;

/**
 * POJO
 * @author Cedrick LUNVEN (@clunven)
 */
public class TypeFieldUpdate implements Serializable {

    /** Serial. */
    private static final long serialVersionUID = -330858240133871548L;

    /** origin name. */
    private String from;
    
    /** new name. */
    private String to;
    
    /**
     * Default constructor.
     */
    public TypeFieldUpdate() {}
    
    /**
     * Default constructor.
     * 
     * @param from
     *      original field name
     * @param to
     *      target field name
     */
    public TypeFieldUpdate(String from, String to) {
        this.from = from;
        this.to = to;
    }

    /**
     * Getter accessor for attribute 'from'.
     *
     * @return
     *       current value of 'from'
     */
    public String getFrom() {
        return from;
    }

    /**
     * Setter accessor for attribute 'from'.
     * @param from
     * 		new value for 'from '
     */
    public void setFrom(String from) {
        this.from = from;
    }

    /**
     * Getter accessor for attribute 'to'.
     *
     * @return
     *       current value of 'to'
     */
    public String getTo() {
        return to;
    }

    /**
     * Setter accessor for attribute 'to'.
     * @param to
     * 		new value for 'to '
     */
    public void setTo(String to) {
        this.to = to;
    }
}
