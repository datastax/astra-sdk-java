package io.stargate.sdk.rest.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Represent an creation request for an UDT.
 *
 * @author Cedrick LUNVEN (@clunven)
 */
public class CreateType implements Serializable {
    
    /** Serial. */
    private static final long serialVersionUID = -8968261494884974502L;

    /** Identifier. */
    private String name;

    /** if not exists. */
    private boolean ifNotExists = false;
    
    /** list of fields. */
    private List<TypeFieldDefinition> fields = new ArrayList<>();

    /**
     * Default Constructor
     */
    public CreateType() {}
    
    /**
     * Create and populate.
     * 
     * @param name
     *      type name
     * @param ifNotExist
     *      use if not exist at cql
     */
    public CreateType(String name, boolean ifNotExist) {
        this.name        = name;
        this.ifNotExists = ifNotExist;
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
     * Getter accessor for attribute 'ifNotExists'.
     *
     * @return
     *       current value of 'ifNotExists'
     */
    public boolean isIfNotExists() {
        return ifNotExists;
    }

    /**
     * Setter accessor for attribute 'ifNotExists'.
     * @param ifNotExists
     * 		new value for 'ifNotExists '
     */
    public void setIfNotExists(boolean ifNotExists) {
        this.ifNotExists = ifNotExists;
    }

    /**
     * Getter accessor for attribute 'fields'.
     *
     * @return
     *       current value of 'fields'
     */
    public List<TypeFieldDefinition> getFields() {
        return fields;
    }

    /**
     * Setter accessor for attribute 'fields'.
     * @param fields
     * 		new value for 'fields '
     */
    public void setFields(List<TypeFieldDefinition> fields) {
        this.fields = fields;
    }

}
