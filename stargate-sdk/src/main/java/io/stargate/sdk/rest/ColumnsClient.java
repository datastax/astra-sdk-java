package io.stargate.sdk.rest;

import java.util.Optional;

import io.stargate.sdk.doc.ApiDocumentClient;
import io.stargate.sdk.doc.CollectionClient;
import io.stargate.sdk.doc.NamespaceClient;
import io.stargate.sdk.rest.domain.ColumnDefinition;

/**
 * Working with a Column.
 *
 * @author Cedrick LUNVEN (@clunven)
 */
public class ColumnsClient {

    /** Astra Client. */
    private final ApiRestClient restClient;
    
    /** Namespace. */
    private final KeyspaceClient keyspaceClient;
    
    /** Namespace. */
    private final TableClient tableClient;
    
    /** Unique document identifer. */
    private final String columnId;
    
    /**
     * Constructor focusing on a single Column
     *
     * @param restClient
     *      working with rest
     * @param keyspaceClient
     *      keyspace resource client
     * @param tableClient
     *       table resource client
     * @param columnId
     *      current column identifier
     */
    public ColumnsClient(ApiRestClient restClient, KeyspaceClient keyspaceClient, TableClient tableClient, String columnId) {
        this.restClient     = restClient;
        this.keyspaceClient = keyspaceClient;
        this.tableClient    = tableClient;
        this.columnId       = columnId;
    }
    
    /**
     * Add a column.
     *
     * @param cd
     */
    public void create(ColumnDefinition cd) {
        
    }
    
    /**
     * Update a column.
     *
     * @param cd
     */
    public void update(ColumnDefinition cd) {
        
    }
    
    /**
     * Delete a column.
     */
    public void delete() {
        
    }
    
    /**
     * Retrieve a column.
     *
     * @return
     */
    public Optional<ColumnDefinition> find() {
        return Optional.empty();
    }
    
    /**
     * Check if the column exist on the 
     * @return
     */
    public boolean exist() {
        return find().isPresent();
    }  
    
}
