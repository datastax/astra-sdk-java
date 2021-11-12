package com.datastax.spring.security.dao;

/**
 * Constants for Spring Security authorities.
 */
/**
 * Default Spring Security authorities.
 *
 * @author Cedrick LUNVEN (@clunven)
 * @author JHipster Team
 */
public enum AuthoritiesConstants {
    
    /** admin. */
    ADMIN("ROLE_ADMIN"),

    /** user. */
    USER("ROLE_USER"),

    /** anonymous. */
    ANONYMOUS("ROLE_ANONYMOUS");
    
    /** User key. */
    private String key;
    
    /**
     * Hidden constructor.
     *
     * @param key
     *      current key
     */
    private AuthoritiesConstants(String key) {
        this.key = key;
    }
    
    /**
     * Getter for key.
     *
     * @return
     *      key value
     */
    public String getKey() {
        return this.key;
    }

}
