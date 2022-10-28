package org.datastax.astra.sdk.domain;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Cedrick LUNVEN (@clunven)
 */
@JsonIgnoreProperties
public class ResponseAllUsers implements Serializable {
    
    /** Serial. */
    private static final long serialVersionUID = 3702319553127217656L;

    /** organization id. */
    @JsonProperty("OrgID")
    private String orgId;
    
    /** organization name. */
    @JsonProperty("OrgName")
    private String orgName;
    
    /** list of users. */
    @JsonProperty("Users")
    private List<User> users;

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
     * Getter accessor for attribute 'orgName'.
     *
     * @return
     *       current value of 'orgName'
     */
    public String getOrgName() {
        return orgName;
    }

    /**
     * Setter accessor for attribute 'orgName'.
     * @param orgName
     * 		new value for 'orgName '
     */
    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    /**
     * Getter accessor for attribute 'users'.
     *
     * @return
     *       current value of 'users'
     */
    public List<User> getUsers() {
        return users;
    }

    /**
     * Setter accessor for attribute 'users'.
     * @param users
     * 		new value for 'users '
     */
    public void setUsers(List<User> users) {
        this.users = users;
    }
    
}