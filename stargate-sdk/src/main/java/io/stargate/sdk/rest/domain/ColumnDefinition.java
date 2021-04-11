package io.stargate.sdk.rest.domain;

import java.io.Serializable;

/**
 * Work with the column.
 *
 * @author Cedrick LUNVEN (@clunven)
 */
public class ColumnDefinition implements Serializable {
    
    /** Serial. */
    private static final long serialVersionUID = 5953054505785338100L;

    /** Unique identifier in the class. */
    protected String name;
    
    /** Type Definition if static. */
    protected String typeDefinition;
    
    /** Check if value is static. */
    protected Boolean isStatic;
    
    /**
     * Default Constructor
     */
    public ColumnDefinition() {}
    
    /**
     * Full constructor.
     * 
     * @param name
     *      column identifier
     * @param type
     *      type as a string
     * @param isStatic
     *      static columns
     */
    public ColumnDefinition(String name, String type, Boolean isStatic) {
        this(name, type);
        this.isStatic = isStatic;
    }
    
    public ColumnDefinition(String name, String type) {
        this.name = name;
        this.typeDefinition = type;
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
     * Getter accessor for attribute 'typeDefinition'.
     *
     * @return
     *       current value of 'typeDefinition'
     */
    public String getTypeDefinition() {
        return typeDefinition;
    }

    /**
     * Setter accessor for attribute 'typeDefinition'.
     * @param typeDefinition
     * 		new value for 'typeDefinition '
     */
    public void setTypeDefinition(String typeDefinition) {
        this.typeDefinition = typeDefinition;
    }

    /**
     * Getter accessor for attribute 'isStatic'.
     *
     * @return
     *       current value of 'isStatic'
     */
    public Boolean isStatic() {
        return isStatic;
    }

    /**
     * Setter accessor for attribute 'isStatic'.
     * @param isStatic
     * 		new value for 'isStatic '
     */
    public void setStatic(Boolean isStatic) {
        this.isStatic = isStatic;
    }
    
}
