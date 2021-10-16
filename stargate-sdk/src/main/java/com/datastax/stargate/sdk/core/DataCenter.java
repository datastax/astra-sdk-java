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

package com.datastax.stargate.sdk.core;

/**
 * Bean DataCenter.
 *
 * @author Cedrick LUNVEN (@clunven)
 */
public class DataCenter {
    
    /** Name. */
    private String name;
    
    /** Number of replicas. */
    private int replicas = 1;
    
    /**
     * Default constructor.
     */
    public DataCenter() {}
    
    /**
     * Constructor working with params.
     *
     * @param name
     *      name of the dc
     * @param replicas
     *      number of replicas
     */
    public DataCenter(String name, int replicas) {
        super();
        this.name = name;
        this.replicas = replicas;
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
     * Getter accessor for attribute 'replicas'.
     *
     * @return
     *       current value of 'replicas'
     */
    public int getReplicas() {
        return replicas;
    }
    /**
     * Setter accessor for attribute 'replicas'.
     * @param replicas
     * 		new value for 'replicas '
     */
    public void setReplicas(int replicas) {
        this.replicas = replicas;
    }
}
