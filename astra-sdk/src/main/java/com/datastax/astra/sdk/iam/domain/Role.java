package com.datastax.astra.sdk.iam.domain;

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
    
    /** Some Predefined roles. */
    public static final String BILLING_ADMIN               = "Billing Admin";
    public static final String ORGANIZATION_ADMINISTRATOR  = "Organization Administrator";
    public static final String UI_VIEW_ONLY                = "UI View Only";
    public static final String USER_ADMIN                  = "Admin User";
    public static final String USER_ADMIN_API              = "API Admin User";
    public static final String USER_RO                     = "RO User";
    public static final String USER_RO_API                 = "API RO User";
    public static final String USER_RW                     = "R/W User";
    public static final String USER_RW_API                 = "API R/W User";
    public static final String SERVICE_ACCOUNT_ADMIN       = "Admin Svt Acct";
    public static final String SERVICE_ACCOUNT_ADMIN_API   = "API Admin Svc Acct";
    public static final String SERVICE_ACCOUNT_RW          = "R/W Svt Acct";
    public static final String SERVICE_ACCOUNT_RW_API      = "API R/W Admin Svc Acct";
    public static final String SERVICE_ACCOUNT_RO          = "RO Svt Acct";
    public static final String SERVICE_ACCOUNT_RO_API      = "API RO Svc Acct";

    @JsonProperty("ID")
    private String id;
    
    @JsonProperty("Name")
    private String name;
    
    @JsonProperty("Type")
    private String type;
    
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
     * Getter accessor for attribute 'type'.
     *
     * @return
     *       current value of 'type'
     */
    public String getType() {
        return type;
    }

    /**
     * Setter accessor for attribute 'type'.
     * @param type
     * 		new value for 'type '
     */
    public void setType(String type) {
        this.type = type;
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
