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
import java.util.List;

/**
 * Sample output
 * 
 * {
 *  "keyspace_name": "sdk_test_ks",
 *  "options": [
 *   { "key": "class_name", "value": "org.apache.cassandra.index.sai.StorageAttachedIndex" },
 *   { "key": "target", "value": "keys(bar2)" }
 *  ],
 *  "table_name": "foo",
 *  "index_name": "idx_2",
 *  "kind": "CUSTOM"
 * }
 *
 * @author Cedrick LUNVEN (@clunven)
 */
public class IndexDefinition implements Serializable {
    
    /** Serial. */
    private static final long serialVersionUID = 460202584337456067L;

    /** index name. */
    private String index_name;
    
    /** table name. */
    private String table_name;
    
    /** keyspace name. */
    private String keyspace_name;
    
    /** index options. */
    private List<KVPair> options;
    
    /** index type. */
    private String kind;

    /**
     * Getter accessor for attribute 'index_name'.
     *
     * @return
     *       current value of 'index_name'
     */
    public String getIndex_name() {
        return index_name;
    }

    /**
     * Setter accessor for attribute 'index_name'.
     * @param index_name
     * 		new value for 'index_name '
     */
    public void setIndex_name(String index_name) {
        this.index_name = index_name;
    }

    /**
     * Getter accessor for attribute 'table_name'.
     *
     * @return
     *       current value of 'table_name'
     */
    public String getTable_name() {
        return table_name;
    }

    /**
     * Setter accessor for attribute 'table_name'.
     * @param table_name
     * 		new value for 'table_name '
     */
    public void setTable_name(String table_name) {
        this.table_name = table_name;
    }

    /**
     * Getter accessor for attribute 'keyspace_name'.
     *
     * @return
     *       current value of 'keyspace_name'
     */
    public String getKeyspace_name() {
        return keyspace_name;
    }

    /**
     * Setter accessor for attribute 'keyspace_name'.
     * @param keyspace_name
     * 		new value for 'keyspace_name '
     */
    public void setKeyspace_name(String keyspace_name) {
        this.keyspace_name = keyspace_name;
    }

    /**
     * Getter accessor for attribute 'options'.
     *
     * @return
     *       current value of 'options'
     */
    public List<KVPair> getOptions() {
        return options;
    }

    /**
     * Setter accessor for attribute 'options'.
     * @param options
     * 		new value for 'options '
     */
    public void setOptions(List<KVPair> options) {
        this.options = options;
    }

    /**
     * Getter accessor for attribute 'kind'.
     *
     * @return
     *       current value of 'kind'
     */
    public String getKind() {
        return kind;
    }

    /**
     * Setter accessor for attribute 'kind'.
     * @param kind
     * 		new value for 'kind '
     */
    public void setKind(String kind) {
        this.kind = kind;
    }

}
