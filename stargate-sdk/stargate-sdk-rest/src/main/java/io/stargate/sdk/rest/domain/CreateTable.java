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

import io.stargate.sdk.core.Ordering;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Creation request of a table.
 *
 * @author Cedrick LUNVEN (@clunven)
 */
public class CreateTable implements Serializable {
    
    /** Serial. */
    private static final long serialVersionUID = -163637535414120053L;

    /** Table name. */
    private String name;

    /** if not exist flag. */
    private boolean ifNotExists = false;
    
    /** table primary key. */
    private TablePrimaryKey primaryKey = new TablePrimaryKey();
    
    /** table columns. */
    private List<ColumnDefinition> columnDefinitions = new ArrayList<>();
    
    /** table options (TTL). */
    private TableOptions tableOptions = new TableOptions();
    
    /**
     * Default constructor.
     */
    public CreateTable() {}
    
    /**
     * Constructor with the builder.
     *
     * @param builder
     *      current builder
     */
    private CreateTable(CreateTableBuilder builder) {
        this.ifNotExists = builder.ifNotExists;
        this.name        = builder.name;
        builder.pk.entrySet().stream().forEach(pk -> {
            columnDefinitions.add(pk.getValue());
            primaryKey.getPartitionKey().add(pk.getKey());
        });
        builder.cc.entrySet().stream().forEach(cc -> {
            columnDefinitions.add(cc.getValue());
            primaryKey.getClusteringKey().add(cc.getKey());
            tableOptions.getClusteringExpression().add(new ClusteringExpression(cc.getKey(), builder.ccOrder.get(cc.getKey())));
        });
        builder.cols.entrySet().stream().forEach(col -> {
            columnDefinitions.add(col.getValue());
        });
    }
    
    /**
     * Easily create the builder.
     *      
     * @return
     *      the builder
     */
    public static CreateTableBuilder builder() {
        return new CreateTableBuilder();
    }
    
    /**
     * Pattern builder for class {@link CreateTable}.
     */
    public static class CreateTableBuilder {
        
        /** name. */
        private String name;
        
        /** exist flag. */
        private boolean ifNotExists = false;
        
        /** columns definitions. */
        private Map<String, ColumnDefinition> pk = new HashMap<>();
        
        /** columns definitions. */
        private Map<String, ColumnDefinition> cc = new HashMap<>();
        
        /** columns definitions. */
        private Map<String, Ordering>  ccOrder = new HashMap<>();
        
        /** columns definitions. */
        private Map<String, ColumnDefinition> cols = new HashMap<>();
        
        /**
         * Helper for name.
         *
         * @param name
         *      current name
         * @return
         *      self reference
         */
        public CreateTableBuilder name(String name) {
            this.name = name;
            return this;
        }
        
        /**
         * Helper for if not exist.
         *
         * @param ine
         *      current ine
         * @return
         *      self reference
         */
        public CreateTableBuilder ifNotExist(boolean ine) {
            this.ifNotExists = ine;
            return this;
        }
       
        /**
         * Helper for column static
         *
         * @param name
         *      current name
         * @param type
         *      current type
         * @return
         *      self reference
         */
        public CreateTableBuilder addColumnStatic(String name, String type) {
            if (pk.containsKey(name)) {
                throw new IllegalArgumentException("Cannot add simple column " + name + ", it has already been defined as partition key");
            }
            if (cc.containsKey(name)) {
                throw new IllegalArgumentException("Cannot add simple column " + name + ", it has already been defined as clustering key");
            }
            if (cols.containsKey(name)) {
                throw new IllegalArgumentException("Cannot add simple column " + name + ", it has already been defined as simple column");
            }
            cols.put(name, new ColumnDefinition(name, type, true));
            return this;
        }
        
        /**
         * Helper for column
         *
         * @param name
         *      current name
         * @param type
         *      current type
         * @return
         *      self reference
         */        
        public CreateTableBuilder addColumn(String name, String type) {
            if (pk.containsKey(name)) {
                throw new IllegalArgumentException("Cannot add simple column " + name + ", it has already been defined as partition key");
            }
            if (cc.containsKey(name)) {
                throw new IllegalArgumentException("Cannot add simple column " + name + ", it has already been defined as clustering key");
            }
            if (cols.containsKey(name)) {
                throw new IllegalArgumentException("Cannot add simple column " + name + ", it has already been defined as simple column");
            }
            cols.put(name, new ColumnDefinition(name, type));
            return this;
        }
        
        /**
         * Helper for column PK
         *
         * @param name
         *      current name
         * @param type
         *      current type
         * @return
         *      self reference
         */        
        public CreateTableBuilder addPartitionKey(String name, String type) {
            if (pk.containsKey(name)) {
                throw new IllegalArgumentException("Cannot add partitionKey column " + name + ", it has already been defined as partition key");
            }
            if (cc.containsKey(name)) {
                throw new IllegalArgumentException("Cannot add partitionKey column " + name + ", it has already been defined as clustering key");
            }
            if (cols.containsKey(name)) {
                throw new IllegalArgumentException("Cannot add partitionKey column " + name + ", it has already been defined as simple column");
            }
            pk.put(name, new ColumnDefinition(name, type));
            return this;
        }
        
        /**
         * Helper for column CC.
         *
         * @param name
         *      current name
         * @param type
         *      current type
         * @param order
         *      order  
         * @return
         *      self reference
         */
        public CreateTableBuilder addClusteringKey(String name, String type, Ordering order) {
            if (pk.containsKey(name)) {
                throw new IllegalArgumentException("Cannot add partitionKey column " + name + ", it has already been defined as partition key");
            }
            if (cc.containsKey(name)) {
                throw new IllegalArgumentException("Cannot add partitionKey column " + name + ", it has already been defined as clustering key");
            }
            if (cols.containsKey(name)) {
                throw new IllegalArgumentException("Cannot add partitionKey column " + name + ", it has already been defined as simple column");
            }
            cc.put(name, new ColumnDefinition(name, type));
            ccOrder.put(name, order);
            return this;
        }
        
        /**
         * Generate target bean.
         * 
         * @return
         *      target create table bean.
         */
        public CreateTable build() {
            return new CreateTable(this);
        }
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
     * Getter accessor for attribute 'ifNotExists'.
     *
     * @return
     *       current value of 'ifNotExists'
     */
    public boolean isIfNotExists() {
        return ifNotExists;
    }

    /**
     * Setter accessor for attribute 'ifNotExists'.
     * @param ifNotExists
     * 		new value for 'ifNotExists '
     */
    public void setIfNotExists(boolean ifNotExists) {
        this.ifNotExists = ifNotExists;
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
    
    

}