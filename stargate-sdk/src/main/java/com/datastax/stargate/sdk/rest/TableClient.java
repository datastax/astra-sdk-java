/*
 * Copyright DataStax, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.datastax.stargate.sdk.rest;

import static com.datastax.stargate.sdk.core.ApiSupport.getHttpClient;
import static com.datastax.stargate.sdk.core.ApiSupport.getObjectMapper;
import static com.datastax.stargate.sdk.core.ApiSupport.handleError;
import static com.datastax.stargate.sdk.core.ApiSupport.startRequest;

import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.datastax.stargate.sdk.core.ApiResponse;
import com.datastax.stargate.sdk.core.ResultPage;
import com.datastax.stargate.sdk.rest.domain.ColumnDefinition;
import com.datastax.stargate.sdk.rest.domain.CreateIndex;
import com.datastax.stargate.sdk.rest.domain.CreateTable;
import com.datastax.stargate.sdk.rest.domain.IndexDefinition;
import com.datastax.stargate.sdk.rest.domain.Row;
import com.datastax.stargate.sdk.rest.domain.RowMapper;
import com.datastax.stargate.sdk.rest.domain.RowResultPage;
import com.datastax.stargate.sdk.rest.domain.SearchTableQuery;
import com.datastax.stargate.sdk.rest.domain.SortField;
import com.datastax.stargate.sdk.rest.domain.TableDefinition;
import com.datastax.stargate.sdk.rest.domain.TableOptions;
import com.datastax.stargate.sdk.rest.exception.TableNotFoundException;
import com.datastax.stargate.sdk.utils.Assert;
import com.datastax.stargate.sdk.utils.JsonUtils;
import com.fasterxml.jackson.core.type.TypeReference;

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
    
    /** Hold a reference to client to keep singletons.*/
    private Map <String, ColumnsClient> columnsClient = new HashMap<>();
    
    /** Hold a reference to client to keep singletons.*/
    private Map <String, IndexClient> indexsClient = new HashMap<>();
    
    
    /**
     * Full constructor.
     * 
     * @param restClient ApiRestClient
     * @param keyspaceClient KeyspaceClient
     * @param tableName String
     */
    public TableClient(ApiRestClient restClient,  KeyspaceClient keyspaceClient,  String tableName) {
        this.restClient     = restClient;
        this.keyspaceClient = keyspaceClient;
        this.tableName      = tableName;
    }
    
    // ==============================================================
    // ========================= SCHEMA TABLE   =====================
    // ==============================================================
    
    /**
     * Syntax sugar
     * 
     * @return String
     */
    public String getEndPointTableSchema() {
        return keyspaceClient.getEndPointSchemaKeyspace() + "/tables/" + tableName;
    }
    
    /**
     * Getter accessor for attribute 'tableName'.
     *
     * @return current value of 'tableName'
     */
    public String getTableName() {
        return tableName;
    }
    
    /**
     * Get metadata of the collection. There is no dedicated resources we
     * use the list and filter with what we need.
     *
     * @return metadata of the collection if its exist or empty
     */
    public Optional<TableDefinition> find() {
        return keyspaceClient.tables()
                .filter(t -> tableName.equalsIgnoreCase(t.getName()))
                .findFirst();
    }
    
    /**
     * Check if the table exist.
     * 
     * @return boolean
     */
    public boolean exist() { 
        return keyspaceClient.tableNames()
                .anyMatch(tableName::equals);
    }
    
    /**
     * Create a table 
     * https://docs.datastax.com/en/astra/docs/_attachments/restv2.html#operation/createTable
     * 
     * @param tcr creation request
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
     
     /**
      * updateOptions
      * 
      * @param to TableOptions
      */
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
      * https://docs.astra.datastax.com/reference#delete_api-rest-v2-schemas-keyspaces-keyspace-id-tables-table-id-1
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
    
    
    // ==============================================================
    // ========================= SCHEMA COLUMNS =====================
    // ==============================================================
    
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
     * createColumn
     * 
     * @param colName String
     * @param cd ColumnDefinition
     */
    public void createColumn(String colName, ColumnDefinition cd) {
        column(colName).create(cd);
    }
    
    /**
     * Move to columns client
     * 
     * @param columnId String
     * @return ColumnsClient
     */
    public ColumnsClient column(String columnId) {
        Assert.hasLength(columnId, "columnName");
        if (!columnsClient.containsKey(columnId)) {
            columnsClient.put(columnId, 
                    new ColumnsClient(restClient, keyspaceClient, this, columnId));
        }
        return columnsClient.get(columnId);
    }
    
    // ==============================================================
    // ========================= SCHEMA INDEX   =====================
    // ==============================================================
    
    /**
     * Move to columns client
     * 
     * @param indexName String
     * @return IndexClient
     */
    public IndexClient index(String indexName) {
        Assert.hasLength(indexName, "indexName");
        if (!indexsClient.containsKey(indexName)) {
            indexsClient.put(indexName, 
                    new IndexClient(restClient, keyspaceClient, this, indexName));
        }
        return indexsClient.get(indexName);
    }
    
    /**
     * createIndex
     * 
     * @param idxName String
     * @param ci CreateIndex
     */
    public void createIndex(String idxName, CreateIndex ci) {
        index(idxName).create(ci);
    }
    
    /**
     * Retrieve All indexes for a table.
     *
     * @return
     *      Stream of {@link IndexDefinition} to describe a table
     */
    public Stream<IndexDefinition> indexes() {
        HttpResponse<String> response;
        try {
            // Invoke
            response = getHttpClient().send(
                    startRequest(getEndPointTableSchema() + "/indexes", restClient.getToken())
                    .GET().build(), BodyHandlers.ofString());
        } catch (Exception e) {
            throw new RuntimeException("Cannot retrieve indexes list for a table", e);
        }
        handleError(response);
        try {
            TypeReference<List<IndexDefinition>> expectedType = new TypeReference<>(){};
            return getObjectMapper().readValue(response.body(), expectedType)
                                    .stream()
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
    public Stream<String> indexesNames() {
        return indexes().map(IndexDefinition::getIndex_name);
    }
   
    
    // ==============================================================
    // ========================= DATA ===============================
    // ==============================================================
    
    /**
     * Syntax sugar
     * 
     * @return String
     */
    public String getEndPointTable() {
        return restClient.getEndPointApiRest() 
                + "/v2/keyspaces/" + keyspaceClient.getKeyspace() 
                + "/" + tableName;
    }
   
    /**
     * TODO: Can do better with a bean - SERIALIZATIO + look at target column type
     * 
     * @param record Map
     */
    public void upsert(Map<String, Object> record) {
        Assert.notNull(record, "New Record");
        Assert.isTrue(!record.isEmpty(), "New record should not be empty");
        HttpResponse<String> response;
        try {
           String reqBody = getObjectMapper().writeValueAsString(record);
           response = getHttpClient().send(
                   startRequest(getEndPointTable(), restClient.getToken())
                   .POST(BodyPublishers.ofString(reqBody)).build(),
                   BodyHandlers.ofString());
        } catch (Exception e) {
            throw new RuntimeException("Cannot save document:", e);
        }
        handleError(response);
    }
    
    /**
     * search
     * 
     * @param query SearchTableQuery
     * @return RowResultPage
     */
    public RowResultPage search(SearchTableQuery query) {
        HttpResponse<String> response;
        try {
             // Invoke as JSON
            response = getHttpClient().send(startRequest(
                    buildQueryUrl(query), restClient.getToken()).GET().build(), 
                            BodyHandlers.ofString());
        } catch (Exception e) {
            throw new RuntimeException("Cannot search for documents ", e);
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
    
    /**
     * Retrieve a set of Rows from Primary key value.
     * 
     * @param <T> ResultPage
     * @param query SearchTableQuery
     * @param mapper RowMapper
     * @return ResultPage
     */
    public <T> ResultPage<T> search(SearchTableQuery query, RowMapper<T> mapper) {
        RowResultPage rrp = search(query);
        return new ResultPage<T>(
                rrp.getPageSize(), 
                rrp.getPageState().orElse(null), 
                rrp.getResults().stream()
                   .map(mapper::map)
                   .collect(Collectors.toList()));
    }
    
    /**
     * buildQueryUrl
     * 
     * @param query SearchTableQuery
     * @return String
     */
    private String buildQueryUrl(SearchTableQuery query) {
        try {
            StringBuilder sbUrl = new StringBuilder(getEndPointTable());
            // Add query Params
            sbUrl.append("?page-size=" + query.getPageSize());
            // Depending on query you forge your URL
            if (query.getPageState().isPresent()) {
                sbUrl.append("&page-state=" + 
                        URLEncoder.encode(query.getPageState().get(), StandardCharsets.UTF_8.toString()));
            }
            if (query.getWhere().isPresent()) {
                sbUrl.append("&where=" + 
                        URLEncoder.encode(query.getWhere().get(), StandardCharsets.UTF_8.toString()));
            }
            // Fields to retrieve
            if (null != query.getFieldsToRetrieve() && !query.getFieldsToRetrieve().isEmpty()) {
                sbUrl.append("&fields=" + 
                        URLEncoder.encode(
                                String.join(",", query.getFieldsToRetrieve()), StandardCharsets.UTF_8.toString()));
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

    /**
     * Move to the Table client
     * 
     * @param keys Object
     * @return KeyClient
     */
    public KeyClient key(Object... keys) {
        Assert.notNull(keys, "key");
        return new KeyClient(restClient.getToken(), this, keys);
    }
}
