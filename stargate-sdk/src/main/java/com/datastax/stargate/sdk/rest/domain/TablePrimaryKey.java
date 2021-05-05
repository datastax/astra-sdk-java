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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class TablePrimaryKey implements Serializable {

    /** Serial. */
    private static final long serialVersionUID = -4506920292523388120L;

    private List<String> partitionKey = new ArrayList<>();
    
    private List<String> clusteringKey = new ArrayList<>();

    public TablePrimaryKey() {}
    
    /**
     * Getter accessor for attribute 'partitionKey'.
     *
     * @return
     *       current value of 'partitionKey'
     */
    public List<String> getPartitionKey() {
        return partitionKey;
    }

    /**
     * Setter accessor for attribute 'partitionKey'.
     * @param partitionKey
     * 		new value for 'partitionKey '
     */
    public void setPartitionKey(List<String> partitionKey) {
        this.partitionKey = partitionKey;
    }

    /**
     * Getter accessor for attribute 'clusteringKey'.
     *
     * @return
     *       current value of 'clusteringKey'
     */
    public List<String> getClusteringKey() {
        return clusteringKey;
    }

    /**
     * Setter accessor for attribute 'clusteringKey'.
     * @param clusteringKey
     * 		new value for 'clusteringKey '
     */
    public void setClusteringKey(List<String> clusteringKey) {
        this.clusteringKey = clusteringKey;
    }
    
}
