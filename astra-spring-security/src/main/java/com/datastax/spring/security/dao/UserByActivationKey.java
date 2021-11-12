package com.datastax.spring.security.dao;

import static com.datastax.spring.security.dao.User.COLUMN_ACTIVATION_KEY;
import static com.datastax.spring.security.dao.User.COLUMN_ID;

import java.io.Serializable;

import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table; 

/**
 * Working with UserbyEmail table.
 * 
 * CREATE TABLE IF NOT EXISTS user_by_email (
 *  email text,
 *  id text,
 *  PRIMARY KEY(email)
 * );
 * 
 * @author Cedrick LUNVEN (@clunven)
 */
@Table(UserByActivationKey.TABLE_USER_BY_ACTIVATION_KEY)
public class UserByActivationKey implements Serializable {

    /** Serial. */
    private static final long serialVersionUID = 3987339691466619401L;
    
    /** Table names. */
    public static final String TABLE_USER_BY_ACTIVATION_KEY = "user_by_activation_key";
    
    /** email. */
    @PrimaryKey
    @Column(COLUMN_ACTIVATION_KEY)
    private String activationKey;
   
    /** Unique identifier. */
    @Column(COLUMN_ID)
    private String id;
    
    /**
     * Default constructor
     */
    public UserByActivationKey() {}
    
    /**
     * Constructor with parameters.
     *
     * @param activationKey
     *      activationKey
     * @param id
     *      user identifier
     */
    public UserByActivationKey(String activationKey, String id) {
        this.activationKey = activationKey;
        this.id = id;
    }

    /**
     * Getter accessor for attribute 'id'.
     *
     * @return
     *       current value of 'id'
     */
    public String getId() {
        return id;
    }

    /**
     * Setter accessor for attribute 'id'.
     * @param id
     * 		new value for 'id '
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Getter accessor for attribute 'activationKey'.
     *
     * @return
     *       current value of 'activationKey'
     */
    public String getActivationKey() {
        return activationKey;
    }

    /**
     * Setter accessor for attribute 'activationKey'.
     * @param activationKey
     * 		new value for 'activationKey '
     */
    public void setActivationKey(String activationKey) {
        this.activationKey = activationKey;
    }

}
