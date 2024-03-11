package com.datastax.astra.devops.org.domain;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents response of CreateRole operation.
 */
@JsonIgnoreProperties
public class CreateRoleResponse implements Serializable {
    
    /** Serial. */
    private static final long serialVersionUID = 1L;

    /** Organization id. */
    @JsonProperty("OrgID")
    private String organizationId;
    
    /** role id. */
    @JsonProperty("ID")
    private String roleId;
    
    /** role name. */
    @JsonProperty("Name")
    private String roleName;
    
    /** role policy. */
    @JsonProperty("Policy")
    private RolePolicy policy;
    
    /** update time */
    @JsonProperty("LastUpdateDateTime")
    private String lastUpdateDateTime;
    
    /** user id. */
    @JsonProperty("LastUpdateUserID")
    private String lastUpdateUserID;
    
    /**
     * Default constructor
     */
    public CreateRoleResponse() {}

    /**
     * Getter accessor for attribute 'organizationId'.
     *
     * @return
     *       current value of 'organizationId'
     */
    public String getOrganizationId() {
        return organizationId;
    }

    /**
     * Setter accessor for attribute 'organizationId'.
     * @param organizationId
     * 		new value for 'organizationId '
     */
    public void setOrganizationId(String organizationId) {
        this.organizationId = organizationId;
    }

    /**
     * Getter accessor for attribute 'roleId'.
     *
     * @return
     *       current value of 'roleId'
     */
    public String getRoleId() {
        return roleId;
    }

    /**
     * Setter accessor for attribute 'roleId'.
     * @param roleId
     * 		new value for 'roleId '
     */
    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    /**
     * Getter accessor for attribute 'roleName'.
     *
     * @return
     *       current value of 'roleName'
     */
    public String getRoleName() {
        return roleName;
    }

    /**
     * Setter accessor for attribute 'roleName'.
     * @param roleName
     * 		new value for 'roleName '
     */
    public void setRoleName(String roleName) {
        this.roleName = roleName;
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

    /**
     * Getter accessor for attribute 'lastUpdateDateTime'.
     *
     * @return
     *       current value of 'lastUpdateDateTime'
     */
    public String getLastUpdateDateTime() {
        return lastUpdateDateTime;
    }

    /**
     * Setter accessor for attribute 'lastUpdateDateTime'.
     * @param lastUpdateDateTime
     * 		new value for 'lastUpdateDateTime '
     */
    public void setLastUpdateDateTime(String lastUpdateDateTime) {
        this.lastUpdateDateTime = lastUpdateDateTime;
    }

    /**
     * Getter accessor for attribute 'lastUpdateUserID'.
     *
     * @return
     *       current value of 'lastUpdateUserID'
     */
    public String getLastUpdateUserID() {
        return lastUpdateUserID;
    }

    /**
     * Setter accessor for attribute 'lastUpdateUserID'.
     * @param lastUpdateUserID
     * 		new value for 'lastUpdateUserID '
     */
    public void setLastUpdateUserID(String lastUpdateUserID) {
        this.lastUpdateUserID = lastUpdateUserID;
    }
}
