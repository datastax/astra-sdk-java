package com.dtsx.astra.sdk.org.domain;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Wrapper for token creation.
 */
@JsonIgnoreProperties
public class CreateTokenResponse implements Serializable {

    /** Serial. */
    private static final long serialVersionUID = -2033488126365806669L;

    /** client id. */
    private String clientId;

    /** organization id. */
    private String orgId;

    /** secret for a token. */
    private String secret;

    /** value for a token.. */
    private String token;

    /** generated date. */
    private String generatedOn;

    /** list of roles. */
    private List<String> roles;

    /**
     * Default constructor.
     */
    public CreateTokenResponse() {}

    /**
     * Getter accessor for attribute 'clientId'.
     *
     * @return current value of 'clientId'
     */
    public String getClientId() {
        return clientId;
    }

    /**
     * Setter accessor for attribute 'clientId'.
     * 
     * @param clientId
     *            new value for 'clientId '
     */
    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    /**
     * Getter accessor for attribute 'roles'.
     *
     * @return current value of 'roles'
     */
    public List<String> getRoles() {
        return roles;
    }

    /**
     * Setter accessor for attribute 'roles'.
     * 
     * @param roles
     *            new value for 'roles '
     */
    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    /**
     * Getter accessor for attribute 'orgId'.
     *
     * @return current value of 'orgId'
     */
    public String getOrgId() {
        return orgId;
    }

    /**
     * Setter accessor for attribute 'orgId'.
     * 
     * @param orgId
     *            new value for 'orgId '
     */
    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }

    /**
     * Getter accessor for attribute 'secret'.
     *
     * @return current value of 'secret'
     */
    public String getSecret() {
        return secret;
    }

    /**
     * Setter accessor for attribute 'secret'.
     * 
     * @param secret
     *            new value for 'secret '
     */
    public void setSecret(String secret) {
        this.secret = secret;
    }

    /**
     * Getter accessor for attribute 'token'.
     *
     * @return current value of 'token'
     */
    public String getToken() {
        return token;
    }

    /**
     * Setter accessor for attribute 'token'.
     * 
     * @param token
     *            new value for 'token '
     */
    public void setToken(String token) {
        this.token = token;
    }

    /**
     * Getter accessor for attribute 'generatedOn'.
     *
     * @return current value of 'generatedOn'
     */
    public String getGeneratedOn() {
        return generatedOn;
    }

    /**
     * Setter accessor for attribute 'generatedOn'.
     * 
     * @param generatedOn
     *            new value for 'generatedOn '
     */
    public void setGeneratedOn(String generatedOn) {
        this.generatedOn = generatedOn;
    }

}
