package io.stargate.sdk.rest.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Represent an creation request for an UDT.
 *
 * @author Cedrick LUNVEN (@clunven)
 */
public class UpdateType implements Serializable {
    
    /** Serial. */
    private static final long serialVersionUID = -8968261494884974502L;

    /** Identifier. */
    private String name;
    
    /** list of fields. */
    private List<TypeFieldDefinition> addFields = new ArrayList<>();
    
    /** list of fields. */
    private List<TypeFieldUpdate> renameFields = new ArrayList<>();

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
     * Getter accessor for attribute 'addFields'.
     *
     * @return
     *       current value of 'addFields'
     */
    public List<TypeFieldDefinition> getAddFields() {
        return addFields;
    }

    /**
     * Setter accessor for attribute 'addFields'.
     * @param addFields
     * 		new value for 'addFields '
     */
    public void setAddFields(List<TypeFieldDefinition> addFields) {
        this.addFields = addFields;
    }

    /**
     * Getter accessor for attribute 'renameFields'.
     *
     * @return
     *       current value of 'renameFields'
     */
    public List<TypeFieldUpdate> getRenameFields() {
        return renameFields;
    }

    /**
     * Setter accessor for attribute 'renameFields'.
     * @param renameFields
     * 		new value for 'renameFields '
     */
    public void setRenameFields(List<TypeFieldUpdate> renameFields) {
        this.renameFields = renameFields;
    }


}
