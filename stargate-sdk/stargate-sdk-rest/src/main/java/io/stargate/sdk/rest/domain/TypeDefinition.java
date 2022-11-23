package io.stargate.sdk.rest.domain;

import java.io.Serializable;
import java.util.List;

/**
 * Bean representing a user defined type.
 *
 * @author Cedrick LUNVEN (@clunven)
 */
public class TypeDefinition implements Serializable {

    /** Serial. */
    private static final long serialVersionUID = 7918696008437898181L;
    
    /** Identifier of the UDT. */
    private String name;
    
    /** Reference keyspace for the UDT. */
    private String keyspace;
    
    /** Definition of the fields. */
    private List<TypeFieldDefinition> fields;
    
    /**
     * Default constructor.
     */
    public TypeDefinition() {}

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
     * Getter accessor for attribute 'keyspace'.
     *
     * @return
     *       current value of 'keyspace'
     */
    public String getKeyspace() {
        return keyspace;
    }

    /**
     * Setter accessor for attribute 'keyspace'.
     * @param keyspace
     * 		new value for 'keyspace '
     */
    public void setKeyspace(String keyspace) {
        this.keyspace = keyspace;
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
