package org.datastax.astra.sdk.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Hold object returned by accessing servlerss list.
 *
 * @author Cedrick LUNVEN (@clunven)
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class DatabaseRegionServerless {
    
    /** Region Name. */
    private String name;
    
    /** Cloud provider. */
    private String cloudProvider;
    
    /** Name of the region picked. */
    private String displayName;
    
    /** Zone. */
    private String zone;
    
    /** Classification. */
    private String classification;
    
    /** working region. */
    private boolean enabled;
    
    /** limited ? */
    private boolean reservedForQualifiedUsers;
    
    /**
     * Default Constructor.
     */
    public DatabaseRegionServerless() {
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
     * Getter accessor for attribute 'cloudProvider'.
     *
     * @return
     *       current value of 'cloudProvider'
     */
    public String getCloudProvider() {
        return cloudProvider;
    }

    /**
     * Setter accessor for attribute 'cloudProvider'.
     * @param cloudProvider
     * 		new value for 'cloudProvider '
     */
    public void setCloudProvider(String cloudProvider) {
        this.cloudProvider = cloudProvider;
    }

    /**
     * Getter accessor for attribute 'displayName'.
     *
     * @return
     *       current value of 'displayName'
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Setter accessor for attribute 'displayName'.
     * @param displayName
     * 		new value for 'displayName '
     */
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Getter accessor for attribute 'zone'.
     *
     * @return
     *       current value of 'zone'
     */
    public String getZone() {
        return zone;
    }

    /**
     * Setter accessor for attribute 'zone'.
     * @param zone
     * 		new value for 'zone '
     */
    public void setZone(String zone) {
        this.zone = zone;
    }

    /**
     * Getter accessor for attribute 'classification'.
     *
     * @return
     *       current value of 'classification'
     */
    public String getClassification() {
        return classification;
    }

    /**
     * Setter accessor for attribute 'classification'.
     * @param classification
     * 		new value for 'classification '
     */
    public void setClassification(String classification) {
        this.classification = classification;
    }

    /**
     * Getter accessor for attribute 'enabled'.
     *
     * @return
     *       current value of 'enabled'
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Setter accessor for attribute 'enabled'.
     * @param enabled
     * 		new value for 'enabled '
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * Getter accessor for attribute 'reservedForQualifiedUsers'.
     *
     * @return
     *       current value of 'reservedForQualifiedUsers'
     */
    public boolean isReservedForQualifiedUsers() {
        return reservedForQualifiedUsers;
    }

    /**
     * Setter accessor for attribute 'reservedForQualifiedUsers'.
     * @param reservedForQualifiedUsers
     * 		new value for 'reservedForQualifiedUsers '
     */
    public void setReservedForQualifiedUsers(boolean reservedForQualifiedUsers) {
        this.reservedForQualifiedUsers = reservedForQualifiedUsers;
    }
    
    

}
