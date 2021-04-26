package com.datastax.astra.sdk.utils;

import java.io.Serializable;

/**
 * Parser to read the CSV FiLE.
 * 
 * @author Cedrick LUNVEN (@clunven)
 */
public class Token implements Serializable {
    
    /** Serial.*/
    private static final long serialVersionUID = -2071340043293340134L;

    /** Client identifier. */
    private final String clientId;
    
    /** Client secret. */
    private final String clientSecret;

    /** token. **/
    private final String token;
    
    /** role. */
    private final String role;
    
    /**
     * Constructor wil all fields.
     *
     * @param clientId
     * @param clientSecret
     * @param token
     * @param role
     */
    public Token(String clientId, String clientSecret, String token, String role) {
        super();
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.token = token;
        this.role = role;
    }

    /**
     * Getter accessor for attribute 'clientId'.
     *
     * @return
     *       current value of 'clientId'
     */
    public String getClientId() {
        return clientId;
    }

    /**
     * Getter accessor for attribute 'clientSecret'.
     *
     * @return
     *       current value of 'clientSecret'
     */
    public String getClientSecret() {
        return clientSecret;
    }

    /**
     * Getter accessor for attribute 'token'.
     *
     * @return
     *       current value of 'token'
     */
    public String getToken() {
        return token;
    }

    /**
     * Getter accessor for attribute 'role'.
     *
     * @return
     *       current value of 'role'
     */
    public String getRole() {
        return role;
    }

}
