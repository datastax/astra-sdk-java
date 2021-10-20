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

package com.datastax.stargate.sdk.doc.domain;

import java.util.List;

import com.datastax.stargate.sdk.core.DataCenter;
import com.datastax.stargate.sdk.rest.domain.Keyspace;

/**
 * Object abstraction for document api.
 *
 * @author Cedrick LUNVEN (@clunven)
 */
public class Namespace extends Keyspace {
    
    /**
     * Default constructor.
     */
    public Namespace() {}
            
    /**
     * Full constructor.
     * 
     * @param name
     *      namespace name
     * @param datacenters
     *      list of datacenters
     */
    public Namespace(String name, List<DataCenter> datacenters) {
        super(name,datacenters);
    }
    
    /**
     * Constructor with replicas.
     * 
     * @param name
     *      namespace name
     * @param replicas
     *      number of replicas
     */
    public Namespace(String name, int replicas) {
        super(name, replicas);
    }
    
}
