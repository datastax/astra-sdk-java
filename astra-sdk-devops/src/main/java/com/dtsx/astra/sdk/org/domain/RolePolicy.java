package com.dtsx.astra.sdk.org.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Dto to interact with API.
 *
 * @author Cedrick LUNVEN (@clunven)
 */
public class RolePolicy implements Serializable {
    
    /** Serial. */
    private static final long serialVersionUID = 5232715799485159463L;

    /** Description. */
    private String description;
    
    /** effect. */
    private String effect = "allow";
    
    /** policy resources. */
    private List<String> resources = new ArrayList<>();
    
    /** policy actions. */
    private List<String> actions = new ArrayList<>();

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

    /**
     * Getter accessor for attribute 'effect'.
     *
     * @return
     *       current value of 'effect'
     */
    public String getEffect() {
        return effect;
    }

    /**
     * Setter accessor for attribute 'effect'.
     * @param effect
     * 		new value for 'effect '
     */
    public void setEffect(String effect) {
        this.effect = effect;
    }

    /**
     * Getter accessor for attribute 'resources'.
     *
     * @return
     *       current value of 'resources'
     */
    public List<String> getResources() {
        return resources;
    }

    /**
     * Setter accessor for attribute 'resources'.
     * @param resources
     * 		new value for 'resources '
     */
    public void setResources(List<String> resources) {
        this.resources = resources;
    }

    /**
     * Getter accessor for attribute 'actions'.
     *
     * @return
     *       current value of 'actions'
     */
    public List<String> getActions() {
        return actions;
    }

    /**
     * Setter accessor for attribute 'actions'.
     * @param actions
     * 		new value for 'actions '
     */
    public void setActions(List<String> actions) {
        this.actions = actions;
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return "RolePolicy [description=" + description + ", effect=" + effect + ", resources=" + resources + ", actions="
                + actions + "]";
    }
    
    

}
