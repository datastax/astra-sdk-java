package com.dstx.stargate.client.rest;

import java.util.Optional;

public class ColumnsClient {

   //https://docs.astra.datastax.com/reference#get_api-rest-v2-schemas-keyspaces-keyspace-id-tables-table-id-columns-column-id-1
    public Optional<ColumnDefinition> find() {
        return Optional.empty();
    }
    
    public boolean exist() {
        return find().isPresent();
    }
    
    //https://docs.astra.datastax.com/reference#put_api-rest-v2-schemas-keyspaces-keyspace-id-tables-table-id-columns-column-id-1
    void replaceColumnDefinition() {}
    
    //https://docs.astra.datastax.com/reference#delete_api-rest-v2-schemas-keyspaces-keyspace-id-tables-table-id-columns-column-id-1
    void delete() {}
    
    
    
}
