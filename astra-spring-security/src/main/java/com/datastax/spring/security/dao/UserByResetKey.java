package com.datastax.spring.security.dao;

import static com.datastax.spring.security.dao.User.COLUMN_ID;
import static com.datastax.spring.security.dao.User.COLUMN_RESET_KEY;

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
@Table(UserByResetKey.TABLE_USER_BY_RESET_KEY)
public class UserByResetKey implements Serializable {

    /** Serial. */
    private static final long serialVersionUID = 3987339691466619401L;
    
    /** Table names. */
    public static final String TABLE_USER_BY_RESET_KEY = "user_by_reset_key";
    
    /** email. */
    @PrimaryKey
    @Column(COLUMN_RESET_KEY)
    private String resetKey;
   
    /** Unique identifier. */
    @Column(COLUMN_ID)
    private String id;
    
    /**
     * Default constructor
     */
    public UserByResetKey() {}
    
    /**
     * Constructor with parameters.
     *
     * @param resetKey
     *      resetKey
     * @param id
     *      user identifier
     */
    public UserByResetKey(String resetKey, String id) {
        this.resetKey = resetKey;
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
     * Getter accessor for attribute 'resetKey'.
     *
     * @return
     *       current value of 'resetKey'
     */
    public String getResetKey() {
        return resetKey;
    }

    /**
     * Setter accessor for attribute 'resetKey'.
     * @param resetKey
     * 		new value for 'resetKey '
     */
    public void setResetKey(String resetKey) {
        this.resetKey = resetKey;
    }

}
