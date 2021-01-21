package org.datastax.astra.devops;

/**
 * Database creation request
 *
 * @author Cedrick LUNVEN (@clunven)
 *
 */
public class DatabaseCreationRequest {
    
    /** Name of the database--user friendly identifier. */
    private String name;
    
    /** Keyspace name in database */
    private String keyspace;
    
    /** CloudProvider where the database lives. */
    private CloudProvider cloudProvider;
    
    private DatabaseTier tier = DatabaseTier.developer;
    
    /**
     * CapacityUnits is the amount of space available (horizontal scaling) 
     * for the database. For free tier the max CU's is 1, and 100 
     * for CXX/DXX the max is 12 on startup.
     */
    private int capacityUnits = 1;
    
    private String region;
    
    private String user;
    
    private String password;
    
    public DatabaseCreationRequest() {}

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
    public CloudProvider getCloudProvider() {
        return cloudProvider;
    }

    /**
     * Setter accessor for attribute 'cloudProvider'.
     * @param cloudProvider
     * 		new value for 'cloudProvider '
     */
    public void setCloudProvider(CloudProvider cloudProvider) {
        this.cloudProvider = cloudProvider;
    }

    /**
     * Getter accessor for attribute 'tier'.
     *
     * @return
     *       current value of 'tier'
     */
    public DatabaseTier getTier() {
        return tier;
    }

    /**
     * Setter accessor for attribute 'tier'.
     * @param tier
     * 		new value for 'tier '
     */
    public void setTier(DatabaseTier tier) {
        this.tier = tier;
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
     * Getter accessor for attribute 'user'.
     *
     * @return
     *       current value of 'user'
     */
    public String getUser() {
        return user;
    }

    /**
     * Setter accessor for attribute 'user'.
     * @param user
     * 		new value for 'user '
     */
    public void setUser(String user) {
        this.user = user;
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

}
