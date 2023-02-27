package com.dtsx.astra.sdk.org.domain;

import java.io.Serializable;

/**
 * Access customer keys.
 *
 * @author Cedrick LUNVEN (@clunven)
 */
public class Key implements Serializable {

    /** Serial key. */
    private static final long serialVersionUID = 3301168004898303754L;
    
    /** organization for the. */
    private String organizationId;
    
    /** cloud provider. */
    private String cloudProvider;
    
    /** key identifier. */
    private String keyId;
    
    /** Region for the key. */
    private String region;

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
     * Getter accessor for attribute 'keyId'.
     *
     * @return
     *       current value of 'keyId'
     */
    public String getKeyId() {
        return keyId;
    }

    /**
     * Setter accessor for attribute 'keyId'.
     * @param keyId
     * 		new value for 'keyId '
     */
    public void setKeyId(String keyId) {
        this.keyId = keyId;
    }

    /**
     * Getter accessor for attribute 'region'.
     *
     * @return
     *       current value of 'region'
     */
    public String getRegion() {
        return region;
    }

    /**
     * Setter accessor for attribute 'region'.
     * @param region
     * 		new value for 'region '
     */
    public void setRegion(String region) {
        this.region = region;
    }
    
}
