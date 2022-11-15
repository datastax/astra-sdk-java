package com.datastax.stargate.sdk.rest;

import com.datastax.stargate.sdk.http.ServiceHttp;
import com.datastax.stargate.sdk.http.StargateHttpClient;
import com.datastax.stargate.sdk.rest.domain.CreateType;
import com.datastax.stargate.sdk.rest.domain.TypeDefinition;
import com.datastax.stargate.sdk.rest.domain.UpdateType;
import com.datastax.stargate.sdk.utils.Assert;

import java.util.Optional;
import java.util.function.Function;

import static com.datastax.stargate.sdk.utils.JsonUtils.marshall;

/**
 * Working with UDT.
 *
 * @author Cedrick LUNVEN (@clunven)
 */
public class TypeClient {
    
    /** Http Client with load balancing anf failover. */
    private final StargateHttpClient stargateHttpClient;
    
    /** Namespace. */
    private KeyspaceClient keyspaceClient;
    
    /** Collection name. */
    private String typeName;
    
    /**
     * Full constructor.
     * 
     * @param stargateHttpClient stargateHttpClient
     * @param keyspaceClient KeyspaceClient
     * @param typeName String
     */
    public TypeClient(StargateHttpClient stargateHttpClient, KeyspaceClient keyspaceClient, String typeName) {
        this.keyspaceClient     = keyspaceClient;
        this.stargateHttpClient = stargateHttpClient;
        this.typeName           = typeName;
        Assert.notNull(keyspaceClient, "keyspaceClient");
        Assert.hasLength(typeName,    "typeName");   
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
     * Create a type.
     * 
     * @see <a href="https://stargate.io/docs/stargate/1.0/attachments/restv2.html#operation/createType">Reference Documentation</a>
     * 
     * @param tcr creation request
     */
     public void create(CreateType tcr) {
         stargateHttpClient.POST(keyspaceClient.typesSchemaResource, marshall(tcr));
     }
     
     /**
      * updateOptions
      * 
      * @see <a href="https://stargate.io/docs/stargate/1.0/attachments/restv2.html#operation/updateType">Reference Documentation</a>
      * 
      * @param update to TableOptions
      */
     public void update(UpdateType update) {
         Assert.notNull(update, "updateQuery");
         update.setName(typeName);
         stargateHttpClient.PUT(keyspaceClient.typesSchemaResource, marshall(update));
     }
     
    /**
     * Delete a type
     * 
     * @see <a href="https://stargate.io/docs/stargate/1.0/attachments/restv2.html#operation/deleteType">Reference Documentation</a>
     */
    public void delete() {
        stargateHttpClient.DELETE(typeSchemaResource);
    }
    
    // ---------------------------------
    // ----       Utilities         ----
    // ---------------------------------

    /**
     * /v2/schemas/keyspaces/{keyspace}/tables/{tableName}
     */
    public Function<ServiceHttp, String> typeSchemaResource =
             (node) -> keyspaceClient.typesSchemaResource.apply(node) + "/" + typeName;
    
    /**
     * /v2/keyspaces/{keyspace}/tables/{tableName}
     */
    public Function<ServiceHttp, String> typeResource =
             (node) -> keyspaceClient.typesResource.apply(node) + "/" + typeName;
   
    /**
     * Getter accessor for attribute 'tableName'.
     *
     * @return current value of 'tableName'
     */
    public String getTypeName() {
        return typeName;
    }

}
