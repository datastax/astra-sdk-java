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

package io.stargate.sdk.rest.domain;

import io.stargate.sdk.core.DataCenter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

/**
 * Represent a keyspace definition with its relevant MetaData.
 */
public class Keyspace {
    
    /** Unique identifier for the keyspace. */
    protected String name;
    
    /** This property is used for local deployments. (SimpleStrategy) */
    protected Integer replicas;
    
    /** This property is used for distributed deployment (NetworkTopologyStrategy). */
    protected List<DataCenter> datacenters;

    /**
     * Default constructor.
     */
    public Keyspace() {}
            
    /**
     * Constructor with parameters.
     *
     * @param name
     *      keyspace name
     * @param datacenters
     *      keyspace datacenter
     */
    public Keyspace(String name, List<DataCenter> datacenters) {
        super();
        this.name = name;
        this.datacenters = datacenters;
    }
    
    /**
     * Constructor with parameters.
     *
     * @param name
     *      keyspace name
     * @param replicas
     *      number of replicas.
     */
    public Keyspace(String name, int replicas) {
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
     * Getter accessor for attribute 'datacenters'.
     *
     * @return
     *       current value of 'datacenters'
     */
    public List<DataCenter> getDatacenters() {
        return datacenters;
    }

    /**
     * Setter accessor for attribute 'datacenters'.
     * @param datacenters
     * 		new value for 'datacenters '
     */
    public void setDatacenters(List<DataCenter> datacenters) {
        this.datacenters = datacenters;
    }

    /**
     * Getter accessor for attribute 'replicas'.
     *
     * @return
     *       current value of 'replicas'
     */
    public Integer getReplicas() {
        return replicas;
    }

    /**
     * Setter accessor for attribute 'replicas'.
     * @param replicas
     * 		new value for 'replicas '
     */
    public void setReplicas(Integer replicas) {
        this.replicas = replicas;
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        try {
            return new ObjectMapper().writeValueAsString(this);
        } catch (JsonProcessingException e) {
            return "{}";
        }
    }
    

}
