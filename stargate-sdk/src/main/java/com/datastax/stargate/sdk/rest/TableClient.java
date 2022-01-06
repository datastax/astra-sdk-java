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

import static com.datastax.stargate.sdk.utils.JsonUtils.marshall;
import static com.datastax.stargate.sdk.utils.JsonUtils.unmarshallType;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.datastax.stargate.sdk.StargateClientNode;
import com.datastax.stargate.sdk.StargateHttpClient;
import com.datastax.stargate.sdk.core.ApiResponse;
import com.datastax.stargate.sdk.core.ApiResponseHttp;
import com.datastax.stargate.sdk.core.Page;
import com.datastax.stargate.sdk.core.Sort;
import com.datastax.stargate.sdk.rest.domain.ColumnDefinition;
import com.datastax.stargate.sdk.rest.domain.CreateIndex;
import com.datastax.stargate.sdk.rest.domain.CreateTable;
import com.datastax.stargate.sdk.rest.domain.IndexDefinition;
import com.datastax.stargate.sdk.rest.domain.Row;
import com.datastax.stargate.sdk.rest.domain.RowMapper;
import com.datastax.stargate.sdk.rest.domain.RowResultPage;
import com.datastax.stargate.sdk.rest.domain.SearchTableQuery;
import com.datastax.stargate.sdk.rest.domain.TableDefinition;
import com.datastax.stargate.sdk.rest.domain.TableOptions;
import com.datastax.stargate.sdk.utils.Assert;
import com.datastax.stargate.sdk.utils.JsonUtils;
import com.fasterxml.jackson.core.type.TypeReference;

/**
 * Operate on Tables in Cassandra.
 *
 * @author Cedrick LUNVEN (@clunven)
 */
public class TableClient {
    
    /** URL Parts. */
    public static final String PATH_COLUMNS  = "/columns";
    
    /** URL Parts. */
    public static final String PATH_INDEXES  = "/indexes";
    
    /** Http Client with load balancing anf failover. */
    private final StargateHttpClient stargateHttpClient;
    
    /** Namespace. */
    private KeyspaceClient keyspaceClient;
    
    /** Collection name. */
    private String tableName;
    
    /** Hold a reference to client to keep singletons.*/
    private Map <String, ColumnsClient> columnsClient = new HashMap<>();
    
    /** Hold a reference to client to keep singletons.*/
    private Map <String, IndexClient> indexsClient = new HashMap<>();
    
    private static final TypeReference<ApiResponse<List<ColumnDefinition>>> TYPE_LIST_COLUMNS = 
            new TypeReference<ApiResponse<List<ColumnDefinition>>>() {};
            
    private static final TypeReference<ApiResponse<List<LinkedHashMap<String,?>>>> TYPE_RESULTS =
            new TypeReference<ApiResponse<List<LinkedHashMap<String,?>>>>() {};
            
    private static final TypeReference<List<IndexDefinition>> TYPE_LIST_INDEX = 
            new TypeReference<List<IndexDefinition>>() {};              
    
    /**
     * Full constructor.
     * 
     * @param stargateHttpClient 
     *          stargateHttpClient
     * @param keyspaceClient 
     *          KeyspaceClient
     * @param tableName 
     *          name of the table
     */
    public TableClient(StargateHttpClient stargateHttpClient, KeyspaceClient keyspaceClient,  String tableName) {
        this.stargateHttpClient = stargateHttpClient;
        this.keyspaceClient     = keyspaceClient;
        this.tableName          = tableName;
        Assert.notNull(keyspaceClient, "keyspaceClient");
        Assert.hasLength(tableName,    "tableName");   
    }
    
    // ---------------------------------
    // ----          CRUD           ----
    // ---------------------------------
    
    /**
     * Get a table.
     * 
     * @see <a href="https://stargate.io/docs/stargate/1.0/attachments/restv2.html#operation/getTable">Reference Documentation</a>
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
     * Create a table.
     * 
     * @see <a href="https://stargate.io/docs/stargate/1.0/attachments/restv2.html#operation/createTable">Reference Documentation</a>
     * 
     * @param tcr creation request
     */
     public void create(CreateTable tcr) {
         tcr.setName(tableName);
         Assert.notNull(tcr, "CreateTable");
         stargateHttpClient.POST(keyspaceClient.tablesSchemaResource, marshall(tcr));
     }
     
     /**
      * Replace a table definition (table options only).
      * 
      * @see <a href="https://stargate.io/docs/stargate/1.0/attachments/restv2.html#operation/replaceTable">Reference Documentation</a>
      * 
      * @param to TableOptions
      */
     public void updateOptions(TableOptions to) {
         Assert.notNull(to, "TableCreationRequest");
         CreateTable ct = CreateTable.builder().build();
         ct.setPrimaryKey(null);
         ct.setColumnDefinitions(null);
         ct.setName(tableName);
         ct.setTableOptions(to);
         stargateHttpClient.PUT(tableSchemaResource, marshall(ct));
     }
     
    /**
     * Delete a table.
     * 
     * @see <a href="https://stargate.io/docs/stargate/1.0/attachments/restv2.html#operation/deleteTable">Reference Documentation</a>
     */
     public void delete() {
         stargateHttpClient.DELETE(tableSchemaResource);
     }
     
     // ---------------------------------
     // ----       DATA              ----
     // ---------------------------------
     
     /**
      * Add Rows.
      * 
      * @see <a href="https://stargate.io/docs/stargate/1.0/attachments/restv2.html#operation/addRows">Reference Documentation</a>
      * 
      * @param record 
      *         map of recors
      */
     public void upsert(Map<String, Object> record) {
         Assert.notNull(record, "New Record");
         Assert.isTrue(!record.isEmpty(), "New record should not be empty");
         stargateHttpClient.POST(tableResource, marshall(record));
     }
     
     /**
      * Search a table.
      * 
      * @see <a href="https://stargate.io/docs/stargate/1.0/attachments/restv2.html#operation/searchTable">Reference Documentation</a>
      * 
      * @param query
      *         search params with filters and ordering
      * @return RowResultPage
      */
     public RowResultPage search(SearchTableQuery query) {
         // Parameters validation
         Assert.notNull(query, "query");
         // Invoke Http endpint
         ApiResponseHttp res = stargateHttpClient.GET(tableResource, buildSearchUrlSuffix(query));
         // Marshall results
         ApiResponse<List<LinkedHashMap<String,?>>> result = unmarshallType(res.getBody(), TYPE_RESULTS);
         // Build result
         return new RowResultPage(
                 query.getPageSize(), 
                 result.getPageState(), 
                 result.getData().stream()
                       .map(map -> {
                         Row r = new Row();
                         for (Entry<String, ?> val : map.entrySet()) {
                             r.put(val.getKey(), val.getValue());
                         }
                         return r; 
                        })
                      .collect(Collectors.toList()));
     }

     /**
      * Retrieve a set of Rows from Primary key value.
      * 
      * @see <a href="https://stargate.io/docs/stargate/1.0/attachments/restv2.html#operation/getRows">Reference Documentation</a>
      * 
      * @param <T> 
      *     marshalling bean
      * @param query 
      *     SearchTableQuery
      * @param mapper 
      *     mapping result to bean with a mapper
      * @return ResultPage
      *     pageable result
      */
     public <T> Page<T> search(SearchTableQuery query, RowMapper<T> mapper) {
         RowResultPage rrp = search(query);
         return new Page<T>(
                 rrp.getPageSize(), 
                 rrp.getPageState().orElse(null), 
                 rrp.getResults().stream()
                    .map(mapper::map)
                    .collect(Collectors.toList()));
     }
     
     /**
      * Build URL suffix from the filtered and sorted fields.
      * 
      * @param query 
      *     SearchTableQuery
      * @return
      *     suffix for the URL
      */
     private String buildSearchUrlSuffix(SearchTableQuery query) {
         try {
             StringBuilder sbUrl = new StringBuilder();
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
                 for (Sort sf : query.getFieldsToSort()) {
                     sortFields.put(sf.getFieldName(), sf.getOrder().name());
                 }
                 sbUrl.append("&sort=" + URLEncoder.encode(JsonUtils.mapAsJson(sortFields), StandardCharsets.UTF_8.toString()));
             }
             return sbUrl.toString();
         } catch (UnsupportedEncodingException e) {
             throw new IllegalArgumentException("Cannot enode URL", e);
         }
     }
     
     // ---------------------------------
     // ----       Columns           ----
     // ---------------------------------
     
    /**
     * Retrieve All columns.
     * 
     * @see <a href="https://stargate.io/docs/stargate/1.0/attachments/restv2.html#operation/getColumns">Reference Documentation</a>
     *
     * @return
     *      Sream of {@link ColumnDefinition} to describe a table
     */
    public Stream<ColumnDefinition> columns() {
        ApiResponseHttp res =  stargateHttpClient.GET(columnsSchemaResource);;
        return unmarshallType(res.getBody(), TYPE_LIST_COLUMNS)
                .getData().stream()
                .collect(Collectors.toSet()).stream();
    }
    
    /**
     * Create a Column.
     * 
     * @see <a href="https://stargate.io/docs/stargate/1.0/attachments/restv2.html#operation/createColumn">Reference Documentation</a>
     *
     * 
     * @param colName String
     * @param cd ColumnDefinition
     */
    public void createColumn(String colName, ColumnDefinition cd) {
        column(colName).create(cd);
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
    
    // ---------------------------------
    // ----       Indices           ----
    // ---------------------------------
    
    /**
     * Retrieve All indexes for a table.
     * 
     * @see <a href="https://stargate.io/docs/stargate/1.0/attachments/restv2.html#operation/getIndexes">Reference Documentation</a>
     *
     * @return
     *      Stream of {@link IndexDefinition} to describe a table
     */
    public Stream<IndexDefinition> indexes() {
        ApiResponseHttp res =  stargateHttpClient.GET(indexesSchemaResource);
        return unmarshallType(res.getBody(), TYPE_LIST_INDEX)
                    .stream()
                    .collect(Collectors.toSet()).stream();
    }
    
    /**
     * Create an index.
     * 
     * @see <a href="https://stargate.io/docs/stargate/1.0/attachments/restv2.html#operation/createIndex">Reference Documentation</a>
     *
     * @param idxName String
     * @param ci CreateIndex
     */
    public void createIndex(String idxName, CreateIndex ci) {
        index(idxName).create(ci);
    }
    
    /**
     * Retrieve All indexes names.
     * 
     * @return
     *      a list of columns names;
     */
    public Stream<String> indexesNames() {
        return indexes().map(IndexDefinition::getIndex_name);
    }
    
    // ---------------------------------
    // ----    Sub Resources        ----
    // ---------------------------------
    
    /**
     * Move to columns client
     * 
     * @param columnId String
     * @return ColumnsClient
     */
    public ColumnsClient column(String columnId) {
        Assert.hasLength(columnId, "columnName");
        if (!columnsClient.containsKey(columnId)) {
            columnsClient.put(columnId, new ColumnsClient(stargateHttpClient, this, columnId));
        }
        return columnsClient.get(columnId);
    }
    
    /**
     * Move to columns client
     * 
     * @param indexName String
     * @return IndexClient
     */
    public IndexClient index(String indexName) {
        Assert.hasLength(indexName, "indexName");
        if (!indexsClient.containsKey(indexName)) {
            indexsClient.put(indexName, new IndexClient(stargateHttpClient, this, indexName));
        }
        return indexsClient.get(indexName);
    }
    
    /**
     * Move to the Table client
     * 
     * @param keys Object
     * @return KeyClient
     */
    public KeyClient key(Object... keys) {
        Assert.notNull(keys, "key");
        return new KeyClient(stargateHttpClient, this, keys);
    }
    
    // ---------------------------------
    // ----       Resources         ----
    // ---------------------------------

    /**
     * /v2/schemas/keyspaces/{keyspace}/tables/{tableName}
     */
    public Function<StargateClientNode, String> tableSchemaResource = 
             (node) -> keyspaceClient.tablesSchemaResource.apply(node) + "/" + tableName;
    
    /**
     * /v2/keyspaces/{keyspace}/tables/{tableName}
     */
    public Function<StargateClientNode, String> tableResource = 
             (node) -> keyspaceClient.keyspaceResource.apply(node) + "/" + tableName;
  
    /**
     * /v2/schemas/keyspaces/{keyspace}/tables/{tableName}/columns
     */
    public Function<StargateClientNode, String> columnsSchemaResource = 
            (node) -> tableSchemaResource.apply(node) + PATH_COLUMNS;

    /**
     * /v2/schemas/keyspaces/{keyspace}/tables/{tableName}/indexes
     */
    public Function<StargateClientNode, String> indexesSchemaResource = 
            (node) -> tableSchemaResource.apply(node) + PATH_INDEXES;
     
    /**
     * Getter accessor for attribute 'tableName'.
     *
     * @return current value of 'tableName'
     */
    public String getTableName() {
        return tableName;
    }
}
