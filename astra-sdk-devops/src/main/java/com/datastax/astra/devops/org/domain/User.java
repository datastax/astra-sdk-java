package com.datastax.astra.devops.org.domain;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represent a User
 */
@JsonIgnoreProperties
public class User implements Serializable {
    
    /** Serial. */
    private static final long serialVersionUID = -5559139394251225663L;

    /** User id. */
    @JsonProperty("UserID")
    private String userId;
    
    /** user email. */
    @JsonProperty("Email")
    private String email;
    
    /** user status. */
    @JsonProperty("Status")
    private UserStatus status;
    
    /** user roles. */
    @JsonProperty("Roles")
    private List<Role> roles;

    /**
     * Default constructor.
     */
    public User() {}

    /**
     * Getter accessor for attribute 'userId'.
     *
     * @return
     *       current value of 'userId'
     */
    public String getUserId() {
        return userId;
    }

    /**
     * Setter accessor for attribute 'userId'.
     * @param userId
     * 		new value for 'userId '
     */
    public void setUserId(String userId) {
        this.userId = userId;
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
     * Getter accessor for attribute 'status'.
     *
     * @return
     *       current value of 'status'
     */
    public UserStatus getStatus() {
        return status;
    }

    /**
     * Setter accessor for attribute 'status'.
     * @param status
     * 		new value for 'status '
     */
    public void setStatus(UserStatus status) {
        this.status = status;
    }

    /**
     * Getter accessor for attribute 'roles'.
     *
     * @return
     *       current value of 'roles'
     */
    public List<Role> getRoles() {
        return roles;
    }

    /**
     * Setter accessor for attribute 'roles'.
     * @param roles
     * 		new value for 'roles '
     */
    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }
 
}
