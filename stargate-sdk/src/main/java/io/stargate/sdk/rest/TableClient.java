package io.stargate.sdk.rest;

import static io.stargate.sdk.core.ApiSupport.getHttpClient;
import static io.stargate.sdk.core.ApiSupport.getObjectMapper;
import static io.stargate.sdk.core.ApiSupport.handleError;
import static io.stargate.sdk.core.ApiSupport.startRequest;

import java.net.HttpURLConnection;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.fasterxml.jackson.core.type.TypeReference;

import io.stargate.sdk.core.ApiResponse;
import io.stargate.sdk.core.ResultPage;
import io.stargate.sdk.rest.domain.ColumnDefinition;
import io.stargate.sdk.rest.domain.RowMapper;
import io.stargate.sdk.rest.domain.RowResultPage;
import io.stargate.sdk.rest.domain.CreateTable;
import io.stargate.sdk.rest.domain.TableDefinition;
import io.stargate.sdk.rest.exception.TableNotFoundException;
import io.stargate.sdk.utils.Assert;

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
  
    /**
     * Get metadata of the collection. There is no dedicated resources we
     * use the list and filter with what we need.
     *
     * @return
     *      metadata of the collection if its exist or empty
     */
    public Optional<TableDefinition> find() {
        return keyspaceClient.tables()
                .filter(t -> tableName.equalsIgnoreCase(t.getName()))
                .findFirst();
    }
    
    /**
     * Check if the table exist.
     */
    public boolean exist() { 
        return keyspaceClient.tableNames()
                .anyMatch(tableName::equals);
    }
    
    /**
     * Syntax sugar
     */
    public String getEndPointSchemaCurrentTable() {
        return keyspaceClient.getEndPointSchemaKeyspace() + "/tables/" + tableName;
    }
    
    /**
     * Retrieve All columns.
     *
     * @return
     *      Sream of {@link ColumnDefinition} to describe a table
     */
    public Stream<ColumnDefinition> columns() {
        HttpResponse<String> response;
        try {
            // Invoke
            response = getHttpClient().send(
                    startRequest(getEndPointSchemaCurrentTable() + "/columns", restClient.getToken())
                    .GET().build(), BodyHandlers.ofString());
        } catch (Exception e) {
            throw new RuntimeException("Cannot retrieve table list", e);
        }
        handleError(response);
        try {
            TypeReference<ApiResponse<List<ColumnDefinition>>> expectedType = new TypeReference<>(){};
            return getObjectMapper().readValue(response.body(), expectedType)
                                    .getData().stream()
                                    .collect(Collectors.toSet()).stream();
        } catch (Exception e) {
            throw new RuntimeException("Cannot marshall collection list", e);
        }
    }
    
    /**
     * Retrieve All column names.
     * 
     * @return
     *      a list of columns names;
     */
    public  Stream<String> columnNames() {
        return columns().map(ColumnDefinition::getName);
    }
    
   /**
    * Create a table 
    * @param table creation request
    * 
    * @see https://docs.datastax.com/en/astra/docs/_attachments/restv2.html#operation/createTable
    */
    public void create(CreateTable tcr) {
        Assert.notNull(tcr, "TableCreationRequest");
        tcr.setName(this.tableName);
        HttpResponse<String> response;
        try {
            String reqBody = getObjectMapper().writeValueAsString(tcr);
           response = getHttpClient().send(
                   startRequest(keyspaceClient.getEndPointSchemaKeyspace() + "/tables/", restClient.getToken())
                   .POST(BodyPublishers.ofString(reqBody)).build(),
                   BodyHandlers.ofString());
        } catch (Exception e) {
            throw new RuntimeException("Cannot save document:", e);
        }
        handleError(response);
    }
    
    public void update(CreateTable tcr) {
        Assert.notNull(tcr, "TableCreationRequest");
        HttpResponse<String> response;
        try {
            String reqBody = getObjectMapper().writeValueAsString(tcr);
           response = getHttpClient().send(
                   startRequest(keyspaceClient.getEndPointSchemaKeyspace() + "/tables/", restClient.getToken())
                   .PUT(BodyPublishers.ofString(reqBody)).build(),
                   BodyHandlers.ofString());
        } catch (Exception e) {
            throw new RuntimeException("Cannot save document:", e);
        }
        handleError(response);
    }
    
    
    //Delete a table
    //https://docs.astra.datastax.com/reference#delete_api-rest-v2-schemas-keyspaces-keyspace-id-tables-table-id-1
    public void delete() {
        HttpResponse<String> response;
        try {
            response = getHttpClient().send(
                    startRequest(getEndPointSchemaCurrentTable(), 
                    restClient.getToken()).DELETE().build(), 
                    BodyHandlers.ofString());
        } catch (Exception e) {
            throw new RuntimeException("Cannot delete table " + tableName, e);
        }
        if (HttpURLConnection.HTTP_NOT_FOUND == response.statusCode()) {
            throw new TableNotFoundException(tableName);
        }
        handleError(response);
    }
    
    public RowResultPage search(Object query) {
        return null;
    }
    
    public <BEAN> ResultPage<BEAN> search(Object query, RowMapper<BEAN> rowMapper) {
        return null;
    }
    
    /**
     * Move to columns client
     */
    public ColumnsClient column(String columnId) {
        return new ColumnsClient(restClient, keyspaceClient, this, columnId);
    }
    

}
