package com.datastax.stargate.sdk.rest;

import static com.datastax.stargate.sdk.utils.JsonUtils.marshall;

import java.util.Optional;

import com.datastax.stargate.sdk.rest.domain.CreateType;
import com.datastax.stargate.sdk.rest.domain.TypeDefinition;
import com.datastax.stargate.sdk.rest.domain.UpdateType;
import com.datastax.stargate.sdk.utils.Assert;
import com.datastax.stargate.sdk.utils.HttpApisClient;

/**
 * Working with UDT.
 *
 * @author Cedrick LUNVEN (@clunven)
 */
public class TypeClient {
    
    /** Namespace. */
    private final KeyspaceClient keyspaceClient;
    
    /** Wrapper handling header and error management as a singleton. */
    private final HttpApisClient http;
    
    /** Collection name. */
    private final String typeName;
    
    /**
     * Full constructor.
     * 
     * @param keyspaceClient KeyspaceClient
     * @param typeName String
     */
    public TypeClient(KeyspaceClient keyspaceClient,  String typeName) {
        this.keyspaceClient = keyspaceClient;
        this.typeName      = typeName;
        this.http           = HttpApisClient.getInstance();
    }
    
    // ---------------------------------
    // ----          CRUD           ----
    // ---------------------------------
   
    /**
     * Get metadata of the collection. There is no dedicated resources we
     * use the list and filter with what we need.
     *
     * @return metadata of the collection if its exist or empty
     */
    public Optional<TypeDefinition> find() {
        return keyspaceClient.types()
                .filter(t -> typeName.equalsIgnoreCase(t.getName()))
                .findFirst();
    }
    
    /**
     * Check if the table exist.
     * 
     * @return boolean
     */
    public boolean exist() { 
        return keyspaceClient.typeNames().anyMatch(typeName::equals);
    }
    
    /**
     * Create a table 
     * https://docs.datastax.com/en/astra/docs/_attachments/restv2.html#operation/createTable
     * 
     * @param tcr creation request
     */
     public void create(CreateType tcr) {
         http.POST(getEndPointSchemaTypes(), marshall(tcr));
     }
     
     /**
      * updateOptions
      * 
      * @param update to TableOptions
      */
     public void update(UpdateType update) {
         Assert.notNull(update, "updateQuery");
         update.setName(typeName);
         http.PUT(getEndPointSchemaTypes(), marshall(update));
     }
     
     /*
      * Delete a type
      * https://docs.astra.datastax.com/reference#delete_api-rest-v2-schemas-keyspaces-keyspace-id-tables-table-id-1
      */
    public void delete() {
         http.DELETE(getEndPointSchemaType());
    }
    
    // ---------------------------------
    // ----       Utilities         ----
    // ---------------------------------
    
    /**
     * Syntax sugar
     * 
     * @return String
     */
    public String getEndPointSchemaTypes() {
        return keyspaceClient.getEndPointSchemaKeyspace() + "/types";
    }
    
    /**
     * Syntax sugar
     * 
     * @return String
     */
    public String getEndPointSchemaType() {
        return getEndPointSchemaTypes() + "/" + typeName;
    }
    
    /**
     * Getter accessor for attribute 'tableName'.
     *
     * @return current value of 'tableName'
     */
    public String getTypeName() {
        return typeName;
    }

}
