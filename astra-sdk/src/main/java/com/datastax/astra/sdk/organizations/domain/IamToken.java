package com.datastax.astra.sdk.organizations.domain;

import java.util.List;

/**
 * Token use to work with Astra
 *
 * @author Cedrick LUNVEN (@clunven)
 */
public class IamToken {
    
    private String clientId;
    
    private List<String> roles;
    
    private String generatedOn;

    public IamToken() {}
    
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
     * Setter accessor for attribute 'clientId'.
     * @param clientId
     * 		new value for 'clientId '
     */
    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    /**
     * Getter accessor for attribute 'roles'.
     *
     * @return
     *       current value of 'roles'
     */
    public List<String> getRoles() {
        return roles;
    }

    /**
     * Setter accessor for attribute 'roles'.
     * @param roles
     * 		new value for 'roles '
     */
    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    /**
     * Getter accessor for attribute 'generatedOn'.
     *
     * @return
     *       current value of 'generatedOn'
     */
    public String getGeneratedOn() {
        return generatedOn;
    }

    /**
     * Setter accessor for attribute 'generatedOn'.
     * @param generatedOn
     * 		new value for 'generatedOn '
     */
    public void setGeneratedOn(String generatedOn) {
        this.generatedOn = generatedOn;
    }

}
