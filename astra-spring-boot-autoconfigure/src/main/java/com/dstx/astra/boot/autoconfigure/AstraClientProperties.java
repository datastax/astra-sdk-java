package com.dstx.astra.boot.autoconfigure;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "astra")
public class AstraClientProperties {
    
    /** Database unique identifier.  */
    private String databaseId;
    
    /** Astra database region. */
    private String cloudRegion;
   
    /** Application Token. */
    private String applicationToken;
   
    /** working with Astra. */
    private String secureConnectBundlePath;
    
    /** setup Astra from an external file. */
    private String keyspace;
    
    /** used as username for cqlSession. */
    private String clientId;
    
    /** used as password for cqlSession. */
    private String clientSecret;

    /**
     * Getter accessor for attribute 'databaseId'.
     *
     * @return
     *       current value of 'databaseId'
     */
    public String getDatabaseId() {
        return databaseId;
    }

    /**
     * Setter accessor for attribute 'databaseId'.
     * @param databaseId
     * 		new value for 'databaseId '
     */
    public void setDatabaseId(String databaseId) {
        this.databaseId = databaseId;
    }

    /**
     * Getter accessor for attribute 'cloudRegion'.
     *
     * @return
     *       current value of 'cloudRegion'
     */
    public String getCloudRegion() {
        return cloudRegion;
    }

    /**
     * Setter accessor for attribute 'cloudRegion'.
     * @param cloudRegion
     * 		new value for 'cloudRegion '
     */
    public void setCloudRegion(String cloudRegion) {
        this.cloudRegion = cloudRegion;
    }

    /**
     * Getter accessor for attribute 'applicationToken'.
     *
     * @return
     *       current value of 'applicationToken'
     */
    public String getApplicationToken() {
        return applicationToken;
    }

    /**
     * Setter accessor for attribute 'applicationToken'.
     * @param applicationToken
     * 		new value for 'applicationToken '
     */
    public void setApplicationToken(String applicationToken) {
        this.applicationToken = applicationToken;
    }

    /**
     * Getter accessor for attribute 'secureConnectBundlePath'.
     *
     * @return
     *       current value of 'secureConnectBundlePath'
     */
    public String getSecureConnectBundlePath() {
        return secureConnectBundlePath;
    }

    /**
     * Setter accessor for attribute 'secureConnectBundlePath'.
     * @param secureConnectBundlePath
     * 		new value for 'secureConnectBundlePath '
     */
    public void setSecureConnectBundlePath(String secureConnectBundlePath) {
        this.secureConnectBundlePath = secureConnectBundlePath;
    }

    /**
     * Getter accessor for attribute 'keyspace'.
     *
     * @return
     *       current value of 'keyspace'
     */
    public String getKeyspace() {
        return keyspace;
    }

    /**
     * Setter accessor for attribute 'keyspace'.
     * @param keyspace
     * 		new value for 'keyspace '
     */
    public void setKeyspace(String keyspace) {
        this.keyspace = keyspace;
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
     * Setter accessor for attribute 'clientId'.
     * @param clientId
     * 		new value for 'clientId '
     */
    public void setClientId(String clientId) {
        this.clientId = clientId;
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
     * Setter accessor for attribute 'clientSecret'.
     * @param clientSecret
     * 		new value for 'clientSecret '
     */
    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }
    

}
