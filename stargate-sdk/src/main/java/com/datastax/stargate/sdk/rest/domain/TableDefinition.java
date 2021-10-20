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
 * Represent a table definition when working with Rest API.
 * 
 * @author Cedrick LUNVEN (@clunven)
 */
public class TableDefinition implements Serializable {
    
    /** Serial. */
    private static final long serialVersionUID = 455851052070910341L;

    /** table name. */
    private String name;
    
    /** keyspace name. */
    private String keyspace;
    
    /** column definition. */
    private List<ColumnDefinition> columnDefinitions;
    
    /** primary key. */
    private TablePrimaryKey primaryKey;
    
    /** table options. */
    private TableOptions tableOptions;

    /**
     * Default constructor.
     */
    public TableDefinition() {
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
     * Getter accessor for attribute 'keyspace'.
     *
     * @return
     *       current value of 'keyspace'
     */
    public String getKeyspace() {
        return keyspace;
    }

    /**
     * Setter accessor for attribute 'keyspace'.
     * @param keyspace
     * 		new value for 'keyspace '
     */
    public void setKeyspace(String keyspace) {
        this.keyspace = keyspace;
    }

    /**
     * Getter accessor for attribute 'columnDefinitions'.
     *
     * @return
     *       current value of 'columnDefinitions'
     */
    public List<ColumnDefinition> getColumnDefinitions() {
        return columnDefinitions;
    }

    /**
     * Setter accessor for attribute 'columnDefinitions'.
     * @param columnDefinitions
     * 		new value for 'columnDefinitions '
     */
    public void setColumnDefinitions(List<ColumnDefinition> columnDefinitions) {
        this.columnDefinitions = columnDefinitions;
    }

    /**
     * Getter accessor for attribute 'primaryKey'.
     *
     * @return
     *       current value of 'primaryKey'
     */
    public TablePrimaryKey getPrimaryKey() {
        return primaryKey;
    }

    /**
     * Setter accessor for attribute 'primaryKey'.
     * @param primaryKey
     * 		new value for 'primaryKey '
     */
    public void setPrimaryKey(TablePrimaryKey primaryKey) {
        this.primaryKey = primaryKey;
    }

    /**
     * Getter accessor for attribute 'tableOptions'.
     *
     * @return
     *       current value of 'tableOptions'
     */
    public TableOptions getTableOptions() {
        return tableOptions;
    }

    /**
     * Setter accessor for attribute 'tableOptions'.
     * @param tableOptions
     * 		new value for 'tableOptions '
     */
    public void setTableOptions(TableOptions tableOptions) {
        this.tableOptions = tableOptions;
    }
    
    
    
}
