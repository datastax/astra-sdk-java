package com.dstx.astra.sdk.devops.req;

import com.dstx.astra.sdk.devops.CloudProviderType;
import com.dstx.astra.sdk.devops.DatabaseTierType;

public class DatabaseCreationBuilder {

    /** Name of the database--user friendly identifier. */
    protected String name;
    
    /** Keyspace name in database */
    protected String keyspace;
    
    /** CloudProvider where the database lives. */
    protected CloudProviderType cloudProvider;
    
    protected DatabaseTierType tier = DatabaseTierType.developer;
    protected int capacityUnits = 1;
    
    protected String region;
    
    protected String user;
    
    protected String password;
    
    public DatabaseCreationBuilder() {}
    
    public DatabaseCreationBuilder name(String name) {
        this.name = name;
        return this;
    }
    
    public DatabaseCreationBuilder keyspace(String keyspace) {
        this.keyspace = keyspace;
        return this;
    }
    
    public DatabaseCreationBuilder cloudProvider(CloudProviderType cloudProvider) {
        this.cloudProvider = cloudProvider;
        return this;
    }
    
    public DatabaseCreationBuilder tier(DatabaseTierType tier) {
        this.tier = tier;
        return this;
    }
    
    public DatabaseCreationBuilder cloudRegion(String region) {
        this.region = region;
        return this;
    }
    
    public DatabaseCreationBuilder username(String username) {
        this.user = username;
        return this;
    }
    
    public DatabaseCreationBuilder password(String password) {
        this.password = password;
        return this;
    }
    
    public DatabaseCreationRequest build() {
        return new DatabaseCreationRequest(this);
    }
    
    
    
}
