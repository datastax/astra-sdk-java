package io.stargate.sdk.rest;

import java.util.Map;
import java.util.Optional;

import com.datastax.oss.driver.api.core.cql.Row;

/**
 * Operation on a row.
 * 
 * @author Cedrick LUNVEN (@clunven)
 */
public class RowClient {
    
    /** Astra Client. */
    private final ApiRestClient restClient;
    
    /** Namespace. */
    private final KeyspaceClient keyspaceClient;
    
    /** Collection name. */
    private final String tableName;
    
    private final Map<String, Object> primaryKey;
    
    /**
     * Full constructor.
     */
    public RowClient(ApiRestClient restClient,  KeyspaceClient keyspaceClient,  
            String tableName,Map<String, Object> primaryKey) {
        this.restClient     = restClient;
        this.keyspaceClient = keyspaceClient;
        this.tableName      = tableName;
        this.primaryKey     = primaryKey;
    }
    
    public Optional<Row> find() {
        return Optional.empty();
    }
    
    public boolean exist() {
        return find().isPresent();
    }
    
    public void delete() {
    }
    
    public void update(Row newValue) {
    }

}
