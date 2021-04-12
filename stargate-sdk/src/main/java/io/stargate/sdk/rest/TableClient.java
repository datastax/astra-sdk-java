package io.stargate.sdk.rest;

import static io.stargate.sdk.core.ApiSupport.getHttpClient;
import static io.stargate.sdk.core.ApiSupport.getObjectMapper;
import static io.stargate.sdk.core.ApiSupport.handleError;
import static io.stargate.sdk.core.ApiSupport.startRequest;

import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.fasterxml.jackson.core.type.TypeReference;

import io.stargate.sdk.core.ApiResponse;
import io.stargate.sdk.core.ResultPage;
import io.stargate.sdk.rest.domain.ColumnDefinition;
import io.stargate.sdk.rest.domain.CreateTable;
import io.stargate.sdk.rest.domain.QueryRowsByPrimaryKey;
import io.stargate.sdk.rest.domain.Row;
import io.stargate.sdk.rest.domain.RowMapper;
import io.stargate.sdk.rest.domain.RowResultPage;
import io.stargate.sdk.rest.domain.SortField;
import io.stargate.sdk.rest.domain.TableDefinition;
import io.stargate.sdk.rest.domain.TableOptions;
import io.stargate.sdk.rest.exception.TableNotFoundException;
import io.stargate.sdk.utils.Assert;
import io.stargate.sdk.utils.JsonUtils;

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
    public String getEndPointTableSchema() {
        return keyspaceClient.getEndPointSchemaKeyspace() + "/tables/" + tableName;
    }
    
    /**
     * Syntax sugar
     */
    public String getEndPointTablePrimaryKey() {
        return restClient.getEndPointApiRest() 
                + "/v2/keyspaces/" + keyspaceClient.getKeyspace() 
                + "/" + tableName;
    }
    
    /**
     * Syntax sugar
     */
    public String getEndPointTableData() {
        return restClient.getEndPointApiRest() 
                + "/v2/keyspaces/" + keyspaceClient.getKeyspace() 
                + "/tables/" + tableName;
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
                    startRequest(getEndPointTableSchema() + "/columns", restClient.getToken())
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
    
    public void updateOptions(TableOptions to) {
        Assert.notNull(to, "TableCreationRequest");
        HttpResponse<String> response;
        try {
           CreateTable ct = CreateTable.builder().build();
           ct.setPrimaryKey(null);
           ct.setColumnDefinitions(null);
           ct.setName(tableName);
           ct.setTableOptions(to);
           String reqBody = getObjectMapper().writeValueAsString(ct);
           response = getHttpClient().send(
                   startRequest(getEndPointTableSchema() , restClient.getToken())
                   .PUT(BodyPublishers.ofString(reqBody)).build(),
                   BodyHandlers.ofString());
        } catch (Exception e) {
            throw new RuntimeException("Cannot save document:", e);
        }
        handleError(response);
    }
    
    /*
     * Delete a table
     *
     * @see https://docs.astra.datastax.com/reference#delete_api-rest-v2-schemas-keyspaces-keyspace-id-tables-table-id-1
     */
    public void delete() {
        HttpResponse<String> response;
        try {
            response = getHttpClient().send(
                    startRequest(getEndPointTableSchema(), 
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
    
    /**
     * Retrieve a set of Rows from Primary key value.
     *
     * @param query
     * @return
     */
    public RowResultPage findByPrimaryKey(QueryRowsByPrimaryKey query) {
        Objects.requireNonNull(query);
        HttpResponse<String> response;
        try {
             // Invoke as JSON
            response = getHttpClient().send(
                    startRequest(buildQueryUrl(query), restClient.getToken()).GET().build(), BodyHandlers.ofString());
        } catch (Exception e) {
            throw new RuntimeException("Cannot search for Rows ", e);
        }   
        
        handleError(response);
         
        try {
             ApiResponse<List<LinkedHashMap<String,?>>> result = getObjectMapper()
                     .readValue(response.body(), 
                             new TypeReference<ApiResponse<List<LinkedHashMap<String,?>>>>(){});
             return new RowResultPage(query.getPageSize(), result.getPageState(), result.getData()
                    .stream()
                    .map(map -> {
                        Row r = new Row();
                        for (Entry<String, ?> val: map.entrySet()) {
                            r.put(val.getKey(), val.getValue());
                        }
                        return r;
                    })
                    .collect(Collectors.toList()));
        } catch (Exception e) {
            throw new RuntimeException("Cannot marshall document results", e);
        }
    }
    
    
    private String buildQueryUrl(QueryRowsByPrimaryKey query) {
        try {
            StringBuilder sbUrl = new StringBuilder(getEndPointTablePrimaryKey());
            Assert.isTrue(!query.getPrimaryKey().isEmpty(), "Primary key should not be null");
            // Append URL with part of the primary key
            for(Object pk : query.getPrimaryKey()) {
                sbUrl.append("/" +  URLEncoder.encode(pk.toString(), StandardCharsets.UTF_8.toString()));
            }
            // Add query Params
            sbUrl.append("?page-size=" + query.getPageSize());
            // Depending on query you forge your URL
            if (query.getPageState().isPresent()) {
                sbUrl.append("&page-state=" + 
                        URLEncoder.encode(query.getPageState().get(), StandardCharsets.UTF_8.toString()));
            }
            // Fields to retrieve
            if (null != query.getFieldsToRetrieve() && !query.getFieldsToRetrieve().isEmpty()) {
                sbUrl.append("&fields=" + URLEncoder.encode(JsonUtils.collectionAsJson(query.getFieldsToRetrieve()), StandardCharsets.UTF_8.toString()));
            }
            // Fields to sort on 
            if (null != query.getFieldsToSort() && !query.getFieldsToSort().isEmpty()) {
                Map<String, String> sortFields = new LinkedHashMap<>();
                for (SortField sf : query.getFieldsToSort()) {
                    sortFields.put(sf.getFieldName(), sf.getOrder().name());
                }
                sbUrl.append("&sort=" + URLEncoder.encode(JsonUtils.mapAsJson(sortFields), StandardCharsets.UTF_8.toString()));
            }
            return sbUrl.toString();
        } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException("Cannot enode URL", e);
        }
    }
    
    // DATA
    
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

    /**
     * Getter accessor for attribute 'tableName'.
     *
     * @return
     *       current value of 'tableName'
     */
    public String getTableName() {
        return tableName;
    }
    

}
