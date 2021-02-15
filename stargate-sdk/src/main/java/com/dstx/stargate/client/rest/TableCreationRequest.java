package com.dstx.stargate.client.rest;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.datastax.oss.driver.api.core.cql.ColumnDefinitions;

/**
 * Creation request of a table.
 *
 * @author Cedrick LUNVEN (@clunven)
 */
public class TableCreationRequest implements Serializable {
    
    /** Serial. */
    private static final long serialVersionUID = -163637535414120053L;

    private String name;
 
    private TablePrimaryKey primaryKey = new TablePrimaryKey();
    
    private List<ColumnDefinitions> columnDefinitions = new ArrayList<>();
    
    boolean ifNotExists = false;
    
    TableOptions tableOptions = new TableOptions();

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
    public List<ColumnDefinitions> getColumnDefinitions() {
        return columnDefinitions;
    }

    /**
     * Setter accessor for attribute 'columnDefinitions'.
     * @param columnDefinitions
     * 		new value for 'columnDefinitions '
     */
    public void setColumnDefinitions(List<ColumnDefinitions> columnDefinitions) {
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
    
    

}