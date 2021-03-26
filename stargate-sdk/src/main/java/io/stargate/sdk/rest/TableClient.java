package io.stargate.sdk.rest;

import java.util.Optional;

import io.stargate.sdk.doc.ApiDocumentClient;
import io.stargate.sdk.doc.NamespaceClient;

/**
 * Operate on Tables in Cassandra.
 *
 * @author Cedrick LUNVEN (@clunven)
 */
public class TableClient {
    
    /** Astra Client. */
    private final ApiRestClient restClient;
    
    /** Namespace. */
    private final KeyspaceClient keyspaceClient;
    
    /** Collection name. */
    private final String tableName;
    
    /**
     * Full constructor.
     */
    public TableClient(ApiRestClient restClient,  KeyspaceClient keyspaceClient,  String tableName) {
        this.restClient     = restClient;
        this.keyspaceClient = keyspaceClient;
        this.tableName      = tableName;
    }
    
    // Get a table
    // https://docs.astra.datastax.com/reference#get_api-rest-v2-schemas-keyspaces-keyspace-id-tables-table-id-1
    Optional<TableDefinition> find() {
        return null;
    }
    
    boolean exist() { 
        return false;
    }
    
    // create a table
    //https://docs.astra.datastax.com/reference#post_api-rest-v2-schemas-keyspaces-keyspace-id-tables-1
    void create(TableCreationRequest tcr) {
        
    }
    
    //replace a table definition
    //https://docs.astra.datastax.com/reference#put_api-rest-v2-schemas-keyspaces-keyspace-id-tables-table-id-1
    void replaceTableDefinition() {}
    
    
    //Delete a table
    //https://docs.astra.datastax.com/reference#delete_api-rest-v2-schemas-keyspaces-keyspace-id-tables-table-id-1
    void delete() {}
    
    // https://docs.astra.datastax.com/reference#get_api-rest-v2-schemas-keyspaces-keyspace-id-tables-table-id-columns-1
    void listColumns() {}
    
    //https://docs.astra.datastax.com/reference#post_api-rest-v2-schemas-keyspaces-keyspace-id-tables-table-id-columns-1
    void createColum() {}
    
    
    
    

}
