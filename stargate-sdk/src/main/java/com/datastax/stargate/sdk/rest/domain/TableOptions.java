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

package com.datastax.stargate.sdk.rest.domain;

import java.util.ArrayList;
import java.util.List;

/**
 * Configuration at table level.
 *
 * @author Cedrick LUNVEN (@clunven)
 */
public class TableOptions {
    
    /*
     * Defines the Time To Live (TTL), which determines the time period (in seconds) 
     * to expire data. If the value is >0, TTL is enabled for the entire table and an
     *  expiration timestamp is added to each column. The maximum value is 630720000 
     * (20 years). A new TTL timestamp is calculated each time the data is updated 
     * and the row is removed after the data expires.
     */
    private int defaultTimeToLive = 0;
    
    private List<ClusteringExpression> clusteringExpression = new ArrayList<>();
    
    /**
     * Default Constructor.
     */
    public TableOptions() {}
    
    /**
     * Constructor with Parameters.
     * @param ttl
     *      defaultTimeToLive
     * @param cols
     *      clustering expressions
     */
    public TableOptions(int ttl,  List<ClusteringExpression> cols) {
        this.defaultTimeToLive    = ttl;
        this.clusteringExpression = cols;
    }

    /**
     * Getter accessor for attribute 'defaultTimeToLive'.
     *
     * @return
     *       current value of 'defaultTimeToLive'
     */
    public int getDefaultTimeToLive() {
        return defaultTimeToLive;
    }

    /**
     * Setter accessor for attribute 'defaultTimeToLive'.
     * @param defaultTimeToLive
     * 		new value for 'defaultTimeToLive '
     */
    public void setDefaultTimeToLive(int defaultTimeToLive) {
        this.defaultTimeToLive = defaultTimeToLive;
    }

    /**
     * Getter accessor for attribute 'clusteringExpression'.
     *
     * @return
     *       current value of 'clusteringExpression'
     */
    public List<ClusteringExpression> getClusteringExpression() {
        return clusteringExpression;
    }

    /**
     * Setter accessor for attribute 'clusteringExpression'.
     * @param clusteringExpression
     * 		new value for 'clusteringExpression '
     */
    public void setClusteringExpression(List<ClusteringExpression> clusteringExpression) {
        this.clusteringExpression = clusteringExpression;
    }
}
