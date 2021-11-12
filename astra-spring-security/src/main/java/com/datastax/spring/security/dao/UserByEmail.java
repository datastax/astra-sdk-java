package com.datastax.spring.security.dao;

import static com.datastax.spring.security.dao.User.COLUMN_EMAIL;
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
@Table(UserByEmail.TABLE_USER_BY_EMAIL)
public class UserByEmail implements Serializable {

    /** Serial. */
    private static final long serialVersionUID = 3987339691466619401L;
    
    /** Table names. */
    public static final String TABLE_USER_BY_EMAIL = "user_by_email";
    
    /** email. */
    @PrimaryKey
    @Column(COLUMN_EMAIL)
    private String email;
   
    /** Unique identifier. */
    @Column(COLUMN_ID)
    private String id;
    
    /**
     * Default constructor
     */
    public UserByEmail() {}
    
    /**
     * Constructor with parameters.
     *
     * @param email
     *      email
     * @param id
     *      user identifier
     */
    public UserByEmail(String email, String id) {
        this.email = email;
        this.id = id;
    }

    /**
     * Getter accessor for attribute 'email'.
     *
     * @return
     *       current value of 'email'
     */
    public String getEmail() {
        return email;
    }

    /**
     * Setter accessor for attribute 'email'.
     * @param email
     * 		new value for 'email '
     */
    public void setEmail(String email) {
        this.email = email;
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

}
