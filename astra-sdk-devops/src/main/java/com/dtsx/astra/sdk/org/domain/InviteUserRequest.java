package com.dtsx.astra.sdk.org.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Request to invite an user.
 */
@JsonIgnoreProperties
public class InviteUserRequest implements Serializable {

    /** Serial. */
    private static final long serialVersionUID = 5395030300409323177L;

    /** organization id. */
    @JsonProperty("OrgID")
    private String orgId;
    
    /** email. */
    @JsonProperty("email")
    private String email;
    
    /** roles. */
    @JsonProperty("roles")
    private List<String> roles = new ArrayList<>();
    
    /**
     * Public constructor.
     *
     * @param orgId
     *      organization id
     * @param userEmail
     *      user email
     */
    public InviteUserRequest(String orgId, String userEmail) {
        this.orgId = orgId;
        this.email = userEmail;
    }
    
    /**
     * Add new roles.
     *
     * @param proles
     *      add a role
     */
    public void addRoles(String... proles) {
        if (proles != null) {
            roles.addAll(Arrays.asList(proles));
        }
    }

    /**
     * Getter accessor for attribute 'orgId'.
     *
     * @return
     *       current value of 'orgId'
     */
    public String getOrgId() {
        return orgId;
    }

    /**
     * Setter accessor for attribute 'orgId'.
     * @param orgId
     * 		new value for 'orgId '
     */
    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }

    /**
     * Getter accessor for attribute 'email'.
     *
     * @return
     *       current value of 'email'
     */
    public String getEmail() {
        return email;
    }

    /**
     * Setter accessor for attribute 'email'.
     * @param email
     * 		new value for 'email '
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Getter accessor for attribute 'roles'.
     *
     * @return
     *       current value of 'roles'
     */
    public List<String> getRoles() {
        return roles;
    }

    /**
     * Setter accessor for attribute 'roles'.
     * @param roles
     * 		new value for 'roles '
     */
    public void setRoles(List<String> roles) {
        this.roles = roles;
    }
    
    
}
