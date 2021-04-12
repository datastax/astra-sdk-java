package io.stargate.sdk.rest.domain;

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

    private String name;

    private boolean ifNotExists = false;
    
    private TablePrimaryKey primaryKey = new TablePrimaryKey();
    
    private List<ColumnDefinition> columnDefinitions = new ArrayList<>();
    
    private TableOptions tableOptions = new TableOptions();
    
    public CreateTable() {}
    
    private CreateTable(CreateTableBuilder builder) {
        this.ifNotExists = builder.ifNotExists;
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
    
    public static CreateTableBuilder builder() {
        return new CreateTableBuilder();
    }
    
    /**
     * Pattern builder for class {@link CreateTable}.
     */
    public static class CreateTableBuilder {
        
        private boolean ifNotExists = false;
        
        private Map<String, ColumnDefinition> pk = new HashMap<>();
        
        private Map<String, ColumnDefinition> cc = new HashMap<>();
        
        private Map<String, Ordering>  ccOrder = new HashMap<>();
        
        private Map<String, ColumnDefinition> cols = new HashMap<>();
        
        public CreateTableBuilder ifNotExist(boolean ine) {
            this.ifNotExists = ine;
            return this;
        }
       
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