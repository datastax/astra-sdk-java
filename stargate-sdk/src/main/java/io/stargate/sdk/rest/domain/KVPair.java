package io.stargate.sdk.rest.domain;

/**
 * Wrapper for entries in map.
 *
 * @author Cedrick LUNVEN (@clunven)
 */
public class KVPair {
    
    /** key. */
    private String key;
    
    /** value. */
    private String value;

    /**
     * Getter accessor for attribute 'key'.
     *
     * @return
     *       current value of 'key'
     */
    public String getKey() {
        return key;
    }

    /**
     * Setter accessor for attribute 'key'.
     * @param key
     * 		new value for 'key '
     */
    public void setKey(String key) {
        this.key = key;
    }

    /**
     * Getter accessor for attribute 'value'.
     *
     * @return
     *       current value of 'value'
     */
    public String getValue() {
        return value;
    }

    /**
     * Setter accessor for attribute 'value'.
     * @param value
     * 		new value for 'value '
     */
    public void setValue(String value) {
        this.value = value;
    }
    
    

}
