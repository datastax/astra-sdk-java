package com.datastax.astra.sdk.devops.res;

import com.datastax.astra.sdk.devops.CloudProviderType;
import com.datastax.astra.sdk.devops.DatabaseTierType;

public class DatabaseAvailableRegion {

    private DatabaseTierType tier = DatabaseTierType.developer;
    
    private String description;
    
    private CloudProviderType cloudProvider = CloudProviderType.GCP;
    
    private String region;
    
    private String regionDisplay;
    
    private String regionContinent;
    
    private DatabaseCost cost;
    
    private int databaseCountUsed=1;
    
    private int databaseCountLimit=1;
    
    private int capacityUnitsUsed=1;
    
    private int capacityUnitsLimit=1;
    
    private int defaultStoragePerCapacityUnitGb=10;

    public DatabaseAvailableRegion() {
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

    /**
     * Getter accessor for attribute 'description'.
     *
     * @return
     *       current value of 'description'
     */
    public String getDescription() {
        return description;
    }

    /**
     * Setter accessor for attribute 'description'.
     * @param description
     * 		new value for 'description '
     */
    public void setDescription(String description) {
        this.description = description;
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
     * Getter accessor for attribute 'regionDisplay'.
     *
     * @return
     *       current value of 'regionDisplay'
     */
    public String getRegionDisplay() {
        return regionDisplay;
    }

    /**
     * Setter accessor for attribute 'regionDisplay'.
     * @param regionDisplay
     * 		new value for 'regionDisplay '
     */
    public void setRegionDisplay(String regionDisplay) {
        this.regionDisplay = regionDisplay;
    }

    /**
     * Getter accessor for attribute 'regionContinent'.
     *
     * @return
     *       current value of 'regionContinent'
     */
    public String getRegionContinent() {
        return regionContinent;
    }

    /**
     * Setter accessor for attribute 'regionContinent'.
     * @param regionContinent
     * 		new value for 'regionContinent '
     */
    public void setRegionContinent(String regionContinent) {
        this.regionContinent = regionContinent;
    }

    /**
     * Getter accessor for attribute 'cost'.
     *
     * @return
     *       current value of 'cost'
     */
    public DatabaseCost getCost() {
        return cost;
    }

    /**
     * Setter accessor for attribute 'cost'.
     * @param cost
     * 		new value for 'cost '
     */
    public void setCost(DatabaseCost cost) {
        this.cost = cost;
    }

    /**
     * Getter accessor for attribute 'databaseCountUsed'.
     *
     * @return
     *       current value of 'databaseCountUsed'
     */
    public int getDatabaseCountUsed() {
        return databaseCountUsed;
    }

    /**
     * Setter accessor for attribute 'databaseCountUsed'.
     * @param databaseCountUsed
     * 		new value for 'databaseCountUsed '
     */
    public void setDatabaseCountUsed(int databaseCountUsed) {
        this.databaseCountUsed = databaseCountUsed;
    }

    /**
     * Getter accessor for attribute 'databaseCountLimit'.
     *
     * @return
     *       current value of 'databaseCountLimit'
     */
    public int getDatabaseCountLimit() {
        return databaseCountLimit;
    }

    /**
     * Setter accessor for attribute 'databaseCountLimit'.
     * @param databaseCountLimit
     * 		new value for 'databaseCountLimit '
     */
    public void setDatabaseCountLimit(int databaseCountLimit) {
        this.databaseCountLimit = databaseCountLimit;
    }

    /**
     * Getter accessor for attribute 'capacityUnitsUsed'.
     *
     * @return
     *       current value of 'capacityUnitsUsed'
     */
    public int getCapacityUnitsUsed() {
        return capacityUnitsUsed;
    }

    /**
     * Setter accessor for attribute 'capacityUnitsUsed'.
     * @param capacityUnitsUsed
     * 		new value for 'capacityUnitsUsed '
     */
    public void setCapacityUnitsUsed(int capacityUnitsUsed) {
        this.capacityUnitsUsed = capacityUnitsUsed;
    }

    /**
     * Getter accessor for attribute 'capacityUnitsLimit'.
     *
     * @return
     *       current value of 'capacityUnitsLimit'
     */
    public int getCapacityUnitsLimit() {
        return capacityUnitsLimit;
    }

    /**
     * Setter accessor for attribute 'capacityUnitsLimit'.
     * @param capacityUnitsLimit
     * 		new value for 'capacityUnitsLimit '
     */
    public void setCapacityUnitsLimit(int capacityUnitsLimit) {
        this.capacityUnitsLimit = capacityUnitsLimit;
    }

    /**
     * Getter accessor for attribute 'defaultStoragePerCapacityUnitGb'.
     *
     * @return
     *       current value of 'defaultStoragePerCapacityUnitGb'
     */
    public int getDefaultStoragePerCapacityUnitGb() {
        return defaultStoragePerCapacityUnitGb;
    }

    /**
     * Setter accessor for attribute 'defaultStoragePerCapacityUnitGb'.
     * @param defaultStoragePerCapacityUnitGb
     * 		new value for 'defaultStoragePerCapacityUnitGb '
     */
    public void setDefaultStoragePerCapacityUnitGb(int defaultStoragePerCapacityUnitGb) {
        this.defaultStoragePerCapacityUnitGb = defaultStoragePerCapacityUnitGb;
    }

}
