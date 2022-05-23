package com.datastax.astra.sdk.organizations.domain;

import java.io.Serializable;

/**
 * Hold key region.
 *
 * @author Cedrick LUNVEN (@clunven)
 */
public class KeyRegionDefinition implements Serializable {

    /** Serial number. */
    private static final long serialVersionUID = 5963216153488939655L;
    
    /** key identifier. */
    private String keyID;
    
    /** region. */
    private String region;

    /**
     * Getter accessor for attribute 'keyID'.
     *
     * @return
     *       current value of 'keyID'
     */
    public String getKeyID() {
        return keyID;
    }

    /**
     * Setter accessor for attribute 'keyID'.
     * @param keyID
     * 		new value for 'keyID '
     */
    public void setKeyID(String keyID) {
        this.keyID = keyID;
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
