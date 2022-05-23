package com.datastax.astra.sdk.organizations.domain;

import java.io.Serializable;

/**
 * Hold the definition to create a key.
 * 
 * @author Cedrick LUNVEN (@clunven)
 */
public class KeyDefinition implements Serializable {

    /** Serial number. */
    private static final long serialVersionUID = -4758063833693353755L;
    
    /** Organization identifier. */
    private String orgId;
    
    /** key for aws. */
    private KeyRegionDefinition aws;
    
    /** key for gcp. */
    private KeyRegionDefinition gcp;

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
     * Getter accessor for attribute 'aws'.
     *
     * @return
     *       current value of 'aws'
     */
    public KeyRegionDefinition getAws() {
        return aws;
    }

    /**
     * Setter accessor for attribute 'aws'.
     * @param aws
     * 		new value for 'aws '
     */
    public void setAws(KeyRegionDefinition aws) {
        this.aws = aws;
    }

    /**
     * Getter accessor for attribute 'gcp'.
     *
     * @return
     *       current value of 'gcp'
     */
    public KeyRegionDefinition getGcp() {
        return gcp;
    }

    /**
     * Setter accessor for attribute 'gcp'.
     * @param gcp
     * 		new value for 'gcp '
     */
    public void setGcp(KeyRegionDefinition gcp) {
        this.gcp = gcp;
    }
}
