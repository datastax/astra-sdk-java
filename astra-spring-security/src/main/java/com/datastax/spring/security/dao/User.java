package com.datastax.spring.security.dao;

import java.io.Serializable;
import java.time.Instant;
import java.util.Set;

import org.springframework.data.cassandra.core.mapping.CassandraType;
import org.springframework.data.cassandra.core.mapping.CassandraType.Name;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

/**
 * Table user with the mapper.
 * 
 * CREATE TABLE IF NOT EXISTS user_by_id (
 *  id             text,
 *  login          text,
 *  password       text,
 *  firstname      text,
 *  lastname       text,
 *  email          text,
 *  activated      boolean,
 *  lang_key       text,
 *  activation_key text,
 *  reset_key      text,
 *  reset_date     timestamp,
 *  authorities set of text,
 *  PRIMARY KEY(id)
 * );
 */
@Table(UserSchema.TABLE_USER)
public class User implements Serializable {

    /** Serial.*/
    private static final long serialVersionUID = -6968716091312326045L;
    
    /** Schema constant. */
    public static final String COLUMN_ID             = "id";
    
    /** Schema constant. */
    public static final String COLUMN_LOGIN          = "login";
    
    /** Schema constant. */
    public static final String COLUMN_PASSWORD       = "password";
    
    /** Schema constant. */
    public static final String COLUMN_FIRSTNAME      = "firstname";
    
    /** Schema constant. */
    public static final String COLUMN_LASTNAME       = "lastname";
    
    /** Schema constant. */
    public static final String COLUMN_EMAIL          = "email";
    
    /** Schema constant. */
    public static final String COLUMN_ACTIVATED      = "activated";
    
    /** Schema constant. */
    public static final String COLUMN_LANG_KEY       = "lang_key";
    
    /** Schema constant. */
    public static final String COLUMN_ACTIVATION_KEY = "activation_key";
    
    /** Schema constant. */
    public static final String COLUMN_RESET_KEY      = "reset_key";
    
    /** Schema constant. */
    public static final String COLUMN_RESET_DATE     = "reset_date";
    
    /** Schema constant. */
    public static final String COLUMN_AUTHORITIES    = "authorities";
    
    /** Schema constant. */
    public static final String COLUMN_CREATION_DATE  = "creation_date";
    
    /** Unique identifier. */
    @PrimaryKey
    @Column(COLUMN_ID)
    private String id;
    
    /** login. */
    @Column(COLUMN_ID)
    private String login;
    
    /** password. */
    @Column(COLUMN_PASSWORD)
    private String password;
    
    /** firstname. */
    @Column(COLUMN_FIRSTNAME)
    private String firstname;
    
    /** lastname. */
    @Column(COLUMN_LASTNAME)
    private String lastname;
    
    /** email. */
    @Column(COLUMN_EMAIL)
    private String email;
    
    /** activated. */
    @CassandraType(type = CassandraType.Name.BOOLEAN)
    @Column(COLUMN_ACTIVATED)
    private boolean activated;
    
    /** user lang. */
    @Column(COLUMN_LANG_KEY)
    private String langKey;
    
    /** account activation. */
    @Column(COLUMN_ACTIVATION_KEY)
    private String activationKey;
    
    /** reset password. */
    @Column(COLUMN_RESET_KEY)
    private String resetKey;
    
    /** reset available for some time. */
    @Column(COLUMN_RESET_DATE)
    @CassandraType(type = CassandraType.Name.TIMESTAMP)
    private Instant resetDate;
    
    /** roles. */
    @Column(COLUMN_AUTHORITIES)
    @CassandraType(type = CassandraType.Name.LIST, typeArguments = Name.TEXT)
    private Set<String> authorities;
    
    /**
     * Default constructor.
     */
    public User() {}

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

    /**
     * Getter accessor for attribute 'password'.
     *
     * @return
     *       current value of 'password'
     */
    public String getPassword() {
        return password;
    }

    /**
     * Setter accessor for attribute 'password'.
     * @param password
     * 		new value for 'password '
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Getter accessor for attribute 'firstname'.
     *
     * @return
     *       current value of 'firstname'
     */
    public String getFirstname() {
        return firstname;
    }

    /**
     * Setter accessor for attribute 'firstname'.
     * @param firstname
     * 		new value for 'firstname '
     */
    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    /**
     * Getter accessor for attribute 'lastname'.
     *
     * @return
     *       current value of 'lastname'
     */
    public String getLastname() {
        return lastname;
    }

    /**
     * Setter accessor for attribute 'lastname'.
     * @param lastname
     * 		new value for 'lastname '
     */
    public void setLastname(String lastname) {
        this.lastname = lastname;
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
     * Getter accessor for attribute 'activated'.
     *
     * @return
     *       current value of 'activated'
     */
    public boolean isActivated() {
        return activated;
    }

    /**
     * Setter accessor for attribute 'activated'.
     * @param activated
     * 		new value for 'activated '
     */
    public void setActivated(boolean activated) {
        this.activated = activated;
    }

    /**
     * Getter accessor for attribute 'langKey'.
     *
     * @return
     *       current value of 'langKey'
     */
    public String getLangKey() {
        return langKey;
    }

    /**
     * Setter accessor for attribute 'langKey'.
     * @param langKey
     * 		new value for 'langKey '
     */
    public void setLangKey(String langKey) {
        this.langKey = langKey;
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

    /**
     * Getter accessor for attribute 'resetDate'.
     *
     * @return
     *       current value of 'resetDate'
     */
    public Instant getResetDate() {
        return resetDate;
    }

    /**
     * Setter accessor for attribute 'resetDate'.
     * @param resetDate
     * 		new value for 'resetDate '
     */
    public void setResetDate(Instant resetDate) {
        this.resetDate = resetDate;
    }

    /**
     * Getter accessor for attribute 'authorities'.
     *
     * @return
     *       current value of 'authorities'
     */
    public Set<String> getAuthorities() {
        return authorities;
    }

    /**
     * Setter accessor for attribute 'authorities'.
     * @param authorities
     * 		new value for 'authorities '
     */
    public void setAuthorities(Set<String> authorities) {
        this.authorities = authorities;
    }

}
