package io.stargate.sdk.doc;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties
public class CollectionMetaData implements Serializable {
    
    /** Serial. */
    private static final long serialVersionUID = 8579135728885849205L;

    /** unique identifier of the the collection. */
    private String name;
    
    /** status to use upgrade. */
    private boolean upgradeAvailable;
    
    /** upgrade capability like SAI_INDEX_UPGRADE. */
    private String upgradeType;
    
    /**
     * Default constructor.
     */
    public CollectionMetaData() {
        super();
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
     * Getter accessor for attribute 'upgradeAvailable'.
     *
     * @return
     *       current value of 'upgradeAvailable'
     */
    public boolean isUpgradeAvailable() {
        return upgradeAvailable;
    }

    /**
     * Setter accessor for attribute 'upgradeAvailable'.
     * @param upgradeAvailable
     * 		new value for 'upgradeAvailable '
     */
    public void setUpgradeAvailable(boolean upgradeAvailable) {
        this.upgradeAvailable = upgradeAvailable;
    }

    /**
     * Getter accessor for attribute 'upgradeType'.
     *
     * @return
     *       current value of 'upgradeType'
     */
    public String getUpgradeType() {
        return upgradeType;
    }

    /**
     * Setter accessor for attribute 'upgradeType'.
     * @param upgradeType
     * 		new value for 'upgradeType '
     */
    public void setUpgradeType(String upgradeType) {
        this.upgradeType = upgradeType;
    }
    
    
}
