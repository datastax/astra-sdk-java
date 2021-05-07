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

package com.datastax.astra.sdk.devops.res;

import java.io.Serializable;

import com.datastax.astra.sdk.devops.CloudProviderType;
import com.datastax.astra.sdk.devops.DatabaseTierType;

/**
 * Represent a Cassandra DataCenter (ring) in a database instance.
 *
 * @author Cedrick LUNVEN (@clunven)
 */
public class Datacenter implements Serializable {
    
    /** Serial. */
    private static final long serialVersionUID = 8242671294103939311L;

    /* unique identifier for the ring. */
    private String id;
    
    /** datacenter_name cassandra property. */
    private String name;
    
    /** Reference tier. */
    private DatabaseTierType tier;
    
    /** Reference cloud provider. */
    private CloudProviderType cloudProvider;
    
    /** Cloud region (=AZ for AWS), e.g: europe-west1 */
    private String region;
    
    /** Cloud zone (=REGION for AWS), e.g: emea */
    private String regionZone;
    
    /** Cloud region classification eg: standard */
    private String regionClassification;
    
    /** Capaccity Units. */
    private int capacityUnits;
    
    /** Secure bundle URL. */
    private String secureBundleUrl;
    
    /** Secure bundle URL. */
    private String secureBundleInternalUrl;
    
    /** Secure bundle URL. */
    private String secureBundleMigrationProxyUrl;
    
    /** Secure bundle URL. */
    private String secureBundleMigrationProxyInternalUrl;

    /**
     * Getter accessor for attribute 'id'.
     *
     * @return
     *       current value of 'id'
     */
    public String getId() {
        return id;
    }

    /**
     * Setter accessor for attribute 'id'.
     * @param id
     * 		new value for 'id '
     */
    public void setId(String id) {
        this.id = id;
    }

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
     * Getter accessor for attribute 'regionZone'.
     *
     * @return
     *       current value of 'regionZone'
     */
    public String getRegionZone() {
        return regionZone;
    }

    /**
     * Setter accessor for attribute 'regionZone'.
     * @param regionZone
     * 		new value for 'regionZone '
     */
    public void setRegionZone(String regionZone) {
        this.regionZone = regionZone;
    }

    /**
     * Getter accessor for attribute 'regionClassification'.
     *
     * @return
     *       current value of 'regionClassification'
     */
    public String getRegionClassification() {
        return regionClassification;
    }

    /**
     * Setter accessor for attribute 'regionClassification'.
     * @param regionClassification
     * 		new value for 'regionClassification '
     */
    public void setRegionClassification(String regionClassification) {
        this.regionClassification = regionClassification;
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
     * Getter accessor for attribute 'secureBundleUrl'.
     *
     * @return
     *       current value of 'secureBundleUrl'
     */
    public String getSecureBundleUrl() {
        return secureBundleUrl;
    }

    /**
     * Setter accessor for attribute 'secureBundleUrl'.
     * @param secureBundleUrl
     * 		new value for 'secureBundleUrl '
     */
    public void setSecureBundleUrl(String secureBundleUrl) {
        this.secureBundleUrl = secureBundleUrl;
    }

    /**
     * Getter accessor for attribute 'secureBundleInternalUrl'.
     *
     * @return
     *       current value of 'secureBundleInternalUrl'
     */
    public String getSecureBundleInternalUrl() {
        return secureBundleInternalUrl;
    }

    /**
     * Setter accessor for attribute 'secureBundleInternalUrl'.
     * @param secureBundleInternalUrl
     * 		new value for 'secureBundleInternalUrl '
     */
    public void setSecureBundleInternalUrl(String secureBundleInternalUrl) {
        this.secureBundleInternalUrl = secureBundleInternalUrl;
    }

    /**
     * Getter accessor for attribute 'secureBundleMigrationProxyUrl'.
     *
     * @return
     *       current value of 'secureBundleMigrationProxyUrl'
     */
    public String getSecureBundleMigrationProxyUrl() {
        return secureBundleMigrationProxyUrl;
    }

    /**
     * Setter accessor for attribute 'secureBundleMigrationProxyUrl'.
     * @param secureBundleMigrationProxyUrl
     * 		new value for 'secureBundleMigrationProxyUrl '
     */
    public void setSecureBundleMigrationProxyUrl(String secureBundleMigrationProxyUrl) {
        this.secureBundleMigrationProxyUrl = secureBundleMigrationProxyUrl;
    }

    /**
     * Getter accessor for attribute 'secureBundleMigrationProxyInternalUrl'.
     *
     * @return
     *       current value of 'secureBundleMigrationProxyInternalUrl'
     */
    public String getSecureBundleMigrationProxyInternalUrl() {
        return secureBundleMigrationProxyInternalUrl;
    }

    /**
     * Setter accessor for attribute 'secureBundleMigrationProxyInternalUrl'.
     * @param secureBundleMigrationProxyInternalUrl
     * 		new value for 'secureBundleMigrationProxyInternalUrl '
     */
    public void setSecureBundleMigrationProxyInternalUrl(String secureBundleMigrationProxyInternalUrl) {
        this.secureBundleMigrationProxyInternalUrl = secureBundleMigrationProxyInternalUrl;
    }

}
