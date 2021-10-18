package com.datastax.stargate.sdk.doc.domain;

import java.io.Serializable;

/**
 * Retrieve a list of functions.
 *
 * @author Cedrick LUNVEN (@clunven)
 */
public class FunctionDefinition implements Serializable {
    
    /** Serial number. */
    private static final long serialVersionUID = 3246876638679751121L;

    /** name of the function. */
    private String name;
    
    /** description of the function. */
    private String description;
    
    /**
     * Default constructor.
     */
    public FunctionDefinition() {}
    
    /**
     * Constructor with parameter.
     *
     * @param name
     *      current name
     * @param description
     *      current description
     */
    public FunctionDefinition(String name, String description) {
        this.name        = name;
        this.description = description;
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

    /**
     * Getter accessor for attribute 'description'.
     *
     * @return
     *       current value of 'description'
     */
    public String getDescription() {
        return description;
    }

    /**
     * Setter accessor for attribute 'description'.
     * @param description
     * 		new value for 'description '
     */
    public void setDescription(String description) {
        this.description = description;
    }
    
    

}
