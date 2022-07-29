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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Represents Storage information for the db.
 *
 * @author Cedrick LUNVEN (@clunven)
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class DatabaseStorage {
    
    private int nodeCount;
    
    private int replicationFactor;
    
    private int totalStorage;
    
    private int usedStorage;

    /**
     * Getter accessor for attribute 'nodeCount'.
     *
     * @return
     *       current value of 'nodeCount'
     */
    public int getNodeCount() {
        return nodeCount;
    }

    /**
     * Setter accessor for attribute 'nodeCount'.
     * @param nodeCount
     * 		new value for 'nodeCount '
     */
    public void setNodeCount(int nodeCount) {
        this.nodeCount = nodeCount;
    }

    /**
     * Getter accessor for attribute 'replicationFactor'.
     *
     * @return
     *       current value of 'replicationFactor'
     */
    public int getReplicationFactor() {
        return replicationFactor;
    }

    /**
     * Setter accessor for attribute 'replicationFactor'.
     * @param replicationFactor
     * 		new value for 'replicationFactor '
     */
    public void setReplicationFactor(int replicationFactor) {
        this.replicationFactor = replicationFactor;
    }

    /**
     * Getter accessor for attribute 'totalStorage'.
     *
     * @return
     *       current value of 'totalStorage'
     */
    public int getTotalStorage() {
        return totalStorage;
    }

    /**
     * Setter accessor for attribute 'totalStorage'.
     * @param totalStorage
     * 		new value for 'totalStorage '
     */
    public void setTotalStorage(int totalStorage) {
        this.totalStorage = totalStorage;
    }

    /**
     * Getter accessor for attribute 'usedStorage'.
     *
     * @return
     *       current value of 'usedStorage'
     */
    public int getUsedStorage() {
        return usedStorage;
    }

    /**
     * Setter accessor for attribute 'usedStorage'.
     * @param usedStorage
     * 		new value for 'usedStorage '
     */
    public void setUsedStorage(int usedStorage) {
        this.usedStorage = usedStorage;
    }
    

}
