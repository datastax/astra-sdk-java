/*
 * Copyright DataStax, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.datastax.astra.boot.autoconfigure;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Load the client properties. 
 *
 * @author Cedrick LUNVEN (@clunven)
 */
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
