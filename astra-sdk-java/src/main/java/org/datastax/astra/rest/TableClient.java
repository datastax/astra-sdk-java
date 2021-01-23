package org.datastax.astra.rest;

import java.util.Optional;

public class TableClient {
    
    private ApiRestClient  restClient;
    private KeyspaceClient ksClient;
    private String         tableName;
    
    
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
