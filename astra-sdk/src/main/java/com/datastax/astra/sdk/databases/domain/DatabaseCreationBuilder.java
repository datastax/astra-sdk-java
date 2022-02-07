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

package com.datastax.astra.sdk.databases.domain;

/**
 * @author Cedrick LUNVEN (@clunven)
 */
public class DatabaseCreationBuilder {

    /** Name of the database--user friendly identifier. */
    protected String name;
    
    /** Keyspace name in database */
    protected String keyspace;
    
    /** CloudProvider where the database lives. */
    protected CloudProviderType cloudProvider;
    
    /** Default tier. */
    protected String tier = "developer";
    
    /** */
    protected int capacityUnits = 1;
    
    /** Region where to create theBD. */
    protected String region;
    
    /** User name. */
    protected String user;
    
    /** User password. */
    protected String password;
    
    /** Default constructor. */
    public DatabaseCreationBuilder() {}
    
    /**
     * Build from the name.
     *
     * @param name
     *      target db name.
     * @return
     *      current instance
     */
    public DatabaseCreationBuilder name(String name) {
        this.name = name;
        return this;
    }
    
    /**
     * Build from the keyspace.
     *
     * @param keyspace
     *      targte db keyspace.
     * @return
     *      current instance
     */
    public DatabaseCreationBuilder keyspace(String keyspace) {
        this.keyspace = keyspace;
        return this;
    }
    
    /**
     * Build from the cloudProvider.
     *
     * @param cloudProvider
     *      target db cloudProvider.
     * @return
     *      current instance
     */
    public DatabaseCreationBuilder cloudProvider(CloudProviderType cloudProvider) {
        this.cloudProvider = cloudProvider;
        return this;
    }
    
    /**
     * Build from the tier.
     *
     * @param tier
     *      target db tier.
     * @return
     *      current instance
     */
    public DatabaseCreationBuilder tier(String tier) {
        this.tier = tier;
        return this;
    }
    
    /**
     * Build from the region.
     *
     * @param region
     *      target db region.
     * @return
     *      current instance
     */
    public DatabaseCreationBuilder cloudRegion(String region) {
        this.region = region;
        return this;
    }
    
    /**
     * Build from the username.
     *
     * @param username
     *      target db username.
     * @return
     *      current instance
     */
    public DatabaseCreationBuilder username(String username) {
        this.user = username;
        return this;
    }
    
    /**
     * Build from the password.
     *
     * @param password
     *      target db password.
     * @return
     *      current instance
     */
    public DatabaseCreationBuilder password(String password) {
        this.password = password;
        return this;
    }
    
    /**
     * Build the immutable beans.
     *
     * @return
     *      the immutable instance
     */
    public DatabaseCreationRequest build() {
        return new DatabaseCreationRequest(this);
    }
}
