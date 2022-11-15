package com.dtsx.astra.sdk.db.domain;

/**
 * Create a new region for a DB.
 */
public class DatabaseRegionCreationRequest {

    /** Default tier value. */
    private static final String DEFAULT_TIER = "serverless";

    /** Tier for the datacenter. */
    private String tier = DEFAULT_TIER;

    /** Cloud Provider. */
    private String cloudProvider;

    /** Region code. */
    private String region;

    /**
     * Full constructor.
     *
     * @param tier
     *      db tier
     * @param cloudProvider
     *      db cloud provider
     * @param region
     *      datacenter region
     */
    public DatabaseRegionCreationRequest(String tier, String cloudProvider, String region) {
        this.tier = tier;
        this.cloudProvider = cloudProvider;
        this.region = region;
    }

    /**
     * Set value for region
     *
     * @param region new value for region
     */
    public void setRegion(String region) {
        this.region = region;
    }

    /**
     * Set value for
     * @param tier
     *      new value for
     */
    public void setTier(String tier) {
        this.tier = tier;
    }

    /**
     * Set value for cloudProvider
     *
     * @param cloudProvider new value for cloudProvider
     */
    public void setCloudProvider(String cloudProvider) {
        this.cloudProvider = cloudProvider;
    }

    /**
     * Gets tier
     *
     * @return value of tier
     */
    public String getTier() {
        return tier;
    }

    /**
     * Gets cloudProvider
     *
     * @return value of cloudProvider
     */
    public String getCloudProvider() {
        return cloudProvider;
    }

    /**
     * Gets region
     *
     * @return value of region
     */
    public String getRegion() {
        return region;
    }
}
