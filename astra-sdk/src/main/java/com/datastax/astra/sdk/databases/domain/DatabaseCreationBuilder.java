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
    
    /** */
    protected DatabaseTierType tier = DatabaseTierType.developer;
    /** */
    protected int capacityUnits = 1;
    
    /** */
    protected String region;
    
    /** */
    protected String user;
    
    /** */
    protected String password;
    
    /** 
     * 
    */
    public DatabaseCreationBuilder() {}
    
    public DatabaseCreationBuilder name(String name) {
        this.name = name;
        return this;
    }
    
    /**
     * 
     */
    public DatabaseCreationBuilder keyspace(String keyspace) {
        this.keyspace = keyspace;
        return this;
    }
    
    /**
     * 
     */
    public DatabaseCreationBuilder cloudProvider(CloudProviderType cloudProvider) {
        this.cloudProvider = cloudProvider;
        return this;
    }
    
    /**
     * 
     */
    public DatabaseCreationBuilder tier(DatabaseTierType tier) {
        this.tier = tier;
        return this;
    }
    
    /**
     * 
     */
    public DatabaseCreationBuilder cloudRegion(String region) {
        this.region = region;
        return this;
    }
    
    /**
     * 
     */
    public DatabaseCreationBuilder username(String username) {
        this.user = username;
        return this;
    }
    
    /**
     * 
     */
    public DatabaseCreationBuilder password(String password) {
        this.password = password;
        return this;
    }
    
    /**
     * 
     */
    public DatabaseCreationRequest build() {
        return new DatabaseCreationRequest(this);
    }
}
