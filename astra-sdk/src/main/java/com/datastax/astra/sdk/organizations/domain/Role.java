package com.datastax.astra.sdk.organizations.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Cedrick LUNVEN (@clunven)
 */
@JsonIgnoreProperties
public class Role implements Serializable {
    
    /** Serial. */
    private static final long serialVersionUID = -8104860806037524739L;
    
    @JsonProperty("ID")
    private String id;
    
    @JsonProperty("Name")
    private String name;
   
    @JsonProperty("Policy")
    private RolePolicy policy;
    
    /**
     * @author Cedrick LUNVEN (@clunven)
     */
    public static final class Policy {
        String description;
        List<String> resources = new ArrayList<>();
        List<String> actions = new ArrayList<>();
        String effect;
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

    /**
     * Getter accessor for attribute 'policy'.
     *
     * @return
     *       current value of 'policy'
     */
    public RolePolicy getPolicy() {
        return policy;
    }

    /**
     * Setter accessor for attribute 'policy'.
     * @param policy
     * 		new value for 'policy '
     */
    public void setPolicy(RolePolicy policy) {
        this.policy = policy;
    }  

}
