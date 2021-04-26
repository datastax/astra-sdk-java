package com.datastax.astra.sdk.devops.res;

import java.util.Set;

import com.datastax.astra.sdk.devops.CloudProviderType;
import com.datastax.astra.sdk.devops.DatabaseTierType;

/**
 * Wrapper for DatabaseInfo attribut in findDatabase.
 * 
 * @author Cedrick LUNVEN (@clunven)
 */
public class DatabaseInfo {
    
    /** Name of the database--user friendly identifier. */
    private String name;
    
    /** Default keyspaces. */
    private String keyspace;
    
    /** Keyspace name in database. */
    private Set<String> keyspaces;
    
    /** Datacenter where the database lives. */
    private Set<Datacenter> datacenters;
    
    /** CloudProvider where the database lives. */
    private CloudProviderType cloudProvider;
    
    /** Tier defines the compute power (vertical scaling) for the database. */
    private DatabaseTierType tier;
    
    /**
     * CapacityUnits is the amount of space available (horizontal scaling) 
     * for the database. For free tier the max CU's is 1, and 12 for C10 
     * the max is 12 on startup. 
     */
    private int capacityUnits;
    
    /** Region refers to the cloud region.. */
    private String region;
    
    /** Additional keyspaces names in database. */
    private Set<String> additionalKeyspaces;

    /**
     * Getter accessor for attribute 'name'.
     *
     * @return
     *       current value of 'name'
     */
    public String getName() {
        return name;
    }

    /**
     * Setter accessor for attribute 'name'.
     * @param name
     * 		new value for 'name '
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Getter accessor for attribute 'keyspaces'.
     *
     * @return
     *       current value of 'keyspaces'
     */
    public Set<String> getKeyspaces() {
        return keyspaces;
    }

    /**
     * Setter accessor for attribute 'keyspaces'.
     * @param keyspaces
     * 		new value for 'keyspaces '
     */
    public void setKeyspaces(Set<String> keyspaces) {
        this.keyspaces = keyspaces;
    }

    /**
     * Getter accessor for attribute 'datacenters'.
     *
     * @return
     *       current value of 'datacenters'
     */
    public Set<Datacenter> getDatacenters() {
        return datacenters;
    }

    /**
     * Setter accessor for attribute 'datacenters'.
     * @param datacenters
     * 		new value for 'datacenters '
     */
    public void setDatacenters(Set<Datacenter> datacenters) {
        this.datacenters = datacenters;
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
     * Getter accessor for attribute 'cloudProvider'.
     *
     * @return
     *       current value of 'cloudProvider'
     */
    public CloudProviderType getCloudProvider() {
        return cloudProvider;
    }

    /**
     * Setter accessor for attribute 'cloudProvider'.
     * @param cloudProvider
     * 		new value for 'cloudProvider '
     */
    public void setCloudProvider(CloudProviderType cloudProvider) {
        this.cloudProvider = cloudProvider;
    }

   
    /**
     * Getter accessor for attribute 'capacityUnits'.
     *
     * @return
     *       current value of 'capacityUnits'
     */
    public int getCapacityUnits() {
        return capacityUnits;
    }

    /**
     * Setter accessor for attribute 'capacityUnits'.
     * @param capacityUnits
     * 		new value for 'capacityUnits '
     */
    public void setCapacityUnits(int capacityUnits) {
        this.capacityUnits = capacityUnits;
    }

    /**
     * Getter accessor for attribute 'region'.
     *
     * @return
     *       current value of 'region'
     */
    public String getRegion() {
        return region;
    }

    /**
     * Setter accessor for attribute 'region'.
     * @param region
     * 		new value for 'region '
     */
    public void setRegion(String region) {
        this.region = region;
    }

    /**
     * Getter accessor for attribute 'additionalKeyspaces'.
     *
     * @return
     *       current value of 'additionalKeyspaces'
     */
    public Set<String> getAdditionalKeyspaces() {
        return additionalKeyspaces;
    }

    /**
     * Setter accessor for attribute 'additionalKeyspaces'.
     * @param additionalKeyspaces
     * 		new value for 'additionalKeyspaces '
     */
    public void setAdditionalKeyspaces(Set<String> additionalKeyspaces) {
        this.additionalKeyspaces = additionalKeyspaces;
    }

    /**
     * Getter accessor for attribute 'tier'.
     *
     * @return
     *       current value of 'tier'
     */
    public DatabaseTierType getTier() {
        return tier;
    }

    /**
     * Setter accessor for attribute 'tier'.
     * @param tier
     * 		new value for 'tier '
     */
    public void setTier(DatabaseTierType tier) {
        this.tier = tier;
    }
    

}
