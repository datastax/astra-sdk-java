package com.datastax.astra.devops.org.domain;

import java.io.Serializable;

/**
 * Bean holding Organization informations.
 *
 * @author Cedrick LUNVEN (@clunven)
 */
public class Organization implements Serializable {

    /** Serial number. */
    private static final long serialVersionUID = -418342272084366507L;

    /** unique identifer for the Organization. */
    private String id;
    
    /** Name for the organization. */
    private String name;
    
    /**
     * Default constructor.
     */
    public Organization() {
    }
    
    /**
     * Organization constructor.
     * 
     * @param id
     *      identifier
     * @param name
     *      name
     */
    public Organization(String id, String name) {
        super();
        this.id = id;
        this.name = name;
    }

    /**
     * Getter accessor for attribute 'id'.
     *
     * @return
     *       current value of 'id'
     */
    public String getId() {
        return id;
    }

    /**
     * Setter accessor for attribute 'id'.
     * @param id
     * 		new value for 'id '
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Getter accessor for attribute 'name'.
     *
     * @return
     *       current value of 'name'
     */
    public String getName() {
        return name;
    }

    /**
     * Setter accessor for attribute 'name'.
     * @param name
     * 		new value for 'name '
     */
    public void setName(String name) {
        this.name = name;
    }

}
