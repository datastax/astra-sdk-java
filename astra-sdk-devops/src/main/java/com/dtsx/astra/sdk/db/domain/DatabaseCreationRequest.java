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

package com.dtsx.astra.sdk.db.domain;

/**
 * Database creation request
 *
 * @author Cedrick LUNVEN (@clunven)
 *
 */
public class DatabaseCreationRequest {

    /** Default region. **/
    public static final String DEFAULT_REGION  = "us-east1";

    /** Default tier. **/
    public static final String DEFAULT_TIER    = "serverless";

    /** Default cloud. **/
    public static final CloudProviderType DEFAULT_CLOUD = CloudProviderType.GCP;

    /** CloudProvider where the database lives. */
    private CloudProviderType cloudProvider = DEFAULT_CLOUD;

    /** Region. */
    private String region = DEFAULT_REGION;

    /** Database type. */
    private String tier = DEFAULT_TIER;

    /** Name of the database--user friendly identifier. */
    private String name;

    /** Keyspace name in database */
    private String keyspace;

    /**
     * CapacityUnits is the amount of space available (horizontal scaling) 
     * for the database. For free tier the max CU's is 1, and 100 
     * for CXX/DXX the max is 12 on startup.
     */
    private int capacityUnits = 1;

    /**
     * default constructor.
     */
    public DatabaseCreationRequest() {}

    /**
     * Constructor with the builder.
     *
     * @param builder
     *      current builder
     */
    public DatabaseCreationRequest(DatabaseCreationBuilder builder) {
        this.capacityUnits = builder.capacityUnits;
        this.cloudProvider = builder.cloudProvider;
        this.keyspace      = builder.keyspace;
        this.name          = builder.name;
        this.region        = builder.region;
        this.tier          = builder.tier;
    }
    
    /**
     * Create a builder.
     *
     * @return
     *      get the builder
     */
    public static DatabaseCreationBuilder builder() {
        return new DatabaseCreationBuilder();
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
     * Getter accessor for attribute 'keyspace'.
     *
     * @return
     *       current value of 'keyspace'
     */
    public String getKeyspace() {
        return keyspace;
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
     * Getter accessor for attribute 'tier'.
     *
     * @return
     *       current value of 'tier'
     */
    public String getTier() {
        return tier;
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
     * Getter accessor for attribute 'region'.
     *
     * @return
     *       current value of 'region'
     */
    public String getRegion() {
        return region;
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return "DatabaseCreationRequest [name=" + name + ", keyspace=" + keyspace + ", cloudProvider=" + cloudProvider + ", tier="
                + tier + ", capacityUnits=" + capacityUnits + ", region=" + region + "]";
    }

}
