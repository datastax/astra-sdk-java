package com.datastax.spring.security.dao;

import static com.datastax.spring.security.dao.User.*;

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
@Table(UserByLogin.TABLE_USER_BY_LOGIN)
public class UserByLogin implements Serializable {

    /** Serial. */
    private static final long serialVersionUID = 3987339691466619401L;
    
    /** Table names. */
    public static final String TABLE_USER_BY_LOGIN = "user_by_login";
    
    /** email. */
    @PrimaryKey
    @Column(COLUMN_LOGIN)
    private String login;
   
    /** Unique identifier. */
    @Column(COLUMN_ID)
    private String id;
    
    /**
     * Default constructor
     */
    public UserByLogin() {}
    
    /**
     * Constructor with parameters.
     *
     * @param login
     *      login
     * @param id
     *      user identifier
     */
    public UserByLogin(String login, String id) {
        this.login = login;
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
     * Getter accessor for attribute 'login'.
     *
     * @return
     *       current value of 'login'
     */
    public String getLogin() {
        return login;
    }

    /**
     * Setter accessor for attribute 'login'.
     * @param login
     * 		new value for 'login '
     */
    public void setLogin(String login) {
        this.login = login;
    }

}
