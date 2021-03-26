package io.stargate.sdk.rest;

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

    private String name;
    
    private String keyspace;
    
    private List<ColumnDefinition> columnDefinitions;
    
    private TablePrimaryKey primaryKey;
    
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
