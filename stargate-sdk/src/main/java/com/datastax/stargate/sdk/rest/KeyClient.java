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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.datastax.stargate.sdk.StargateClientNode;
import com.datastax.stargate.sdk.StargateHttpClient;
import com.datastax.stargate.sdk.core.ApiResponse;
import com.datastax.stargate.sdk.core.ApiResponseHttp;
import com.datastax.stargate.sdk.core.ResultPage;
import com.datastax.stargate.sdk.rest.domain.QueryWithKey;
import com.datastax.stargate.sdk.rest.domain.Row;
import com.datastax.stargate.sdk.rest.domain.RowMapper;
import com.datastax.stargate.sdk.rest.domain.RowResultPage;
import com.datastax.stargate.sdk.rest.domain.SortField;
import com.datastax.stargate.sdk.utils.Assert;
import com.datastax.stargate.sdk.utils.JsonUtils;
import com.fasterxml.jackson.core.type.TypeReference;

/**
 * Operation on a record.
 * 
 * @author Cedrick LUNVEN (@clunven)
 */
public class KeyClient {
    
    /** Reference to http client. */
    private final StargateHttpClient stargateClient;
    
    /** Collection name. */
    private TableClient tableClient;
 
    /** Search PK. */
    private List< Object> key = new ArrayList<>();
    
    /** Type for result. */
    private static final  TypeReference<ApiResponse<List<LinkedHashMap<String,?>>>> TYPE_RESULTS = 
            new TypeReference<ApiResponse<List<LinkedHashMap<String,?>>>>(){};
    
    /**
     * Full constructor.
     *
     * @param tableClient TableClient
     * @param keys Object
     */
    public KeyClient(StargateHttpClient stargateClient, TableClient tableClient, Object... keys) {
        this.tableClient    = tableClient;
        this.key            = new ArrayList<>(Arrays.asList(keys));
        this.stargateClient = stargateClient;
        Assert.notNull(key, "key");
        Assert.isTrue(!key.isEmpty(), "key");
    }
    
    // ---------------------------------
    // ----          CRUD           ----
    // ---------------------------------
    
    /**
     * Retrieve a set of Rows from Primary key value.
     * 
     * @see <a href="https://stargate.io/docs/stargate/1.0/attachments/restv2.html#operation/searchTable">Reference Documentation</a>
     * 
     * @param query QueryWithKey
     * @return RowResultPage
     */
    public RowResultPage find(QueryWithKey query) {
        // Parameter validatioons
        Objects.requireNonNull(query);
        // Invoke endpoint
        ApiResponseHttp res = stargateClient.GET(primaryKeyResource, buildSearchUrlSuffix(query));
        // Marshall response
        ApiResponse<List<LinkedHashMap<String,?>>> result = unmarshallType(res.getBody(), TYPE_RESULTS);
        // Build outout
        return new RowResultPage(query.getPageSize(), result.getPageState(), 
           result.getData().stream()
                 .map(map -> {
                        Row r = new Row();
                        for (Entry<String, ?> val: map.entrySet()) {
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
     * @param <T> T
     * @param query QueryWithKey
     * @param mapper RowMapper
     * @return ResultPage
     */
    public <T> ResultPage<T> find(QueryWithKey query, RowMapper<T> mapper) {
        // Parameter validatioons
        Objects.requireNonNull(query);
        Objects.requireNonNull(mapper);
        // Find
        RowResultPage rrp = find(query);
        // Mapping
        return new ResultPage<T>(rrp.getPageSize(), 
                rrp.getPageState().orElse(null),
                rrp.getResults().stream()
                   .map(mapper::map)
                   .collect(Collectors.toList()));
    }
    
    /**
     * Delete by key
     * 
     * @see <a href="https://stargate.io/docs/stargate/1.0/attachments/restv2.html#operation/deleteRows">Reference Documentation</a>
     */
    public void delete() {
        stargateClient.DELETE(primaryKeyResource);
    }
    
    /**
     * update
     * 
     * @see <a href="https://stargate.io/docs/stargate/1.0/attachments/restv2.html#operation/updateRows">Reference Documentation</a>
     * @param newRecord Map
     */
    public void update(Map<String, Object> newRecord) {
        stargateClient.PATCH(primaryKeyResource, marshall(newRecord));
    }
    
    /**
     * replace
     * 
     * @see <a href="https://stargate.io/docs/stargate/1.0/attachments/restv2.html#operation/replaceRows">Reference Documentation</a>
     * 
     * @param newRecord Map
     */
    public void replace(Map<String, Object> newRecord) {
       stargateClient.PUT(primaryKeyResource, marshall(newRecord));
    }
    
    // ---------------------------------
    // ----       Utilities         ----
    // ---------------------------------
    
    /**
     * Build complex URL as expected with primaryKey.
     * 
     * @param query QueryWithKey
     * @return String
     */
    private String buildSearchUrlSuffix(QueryWithKey query) {
        try {
            StringBuilder sbUrl = new StringBuilder();
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
    
    /**
     * /v2/schemas/keyspaces/{keyspace}/tables/{tableName}/{key1}/...{keyn}
     */
    public Function<StargateClientNode, String> primaryKeyResource = (node) -> {
        StringBuilder sbUrl = new StringBuilder(tableClient.tableResource.apply(node));
        try {
            for(Object pk : key) {
                sbUrl.append("/" + URLEncoder.encode(pk.toString(), StandardCharsets.UTF_8.toString()));
            }
        } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException("Cannot enode URL", e);
        }
        return sbUrl.toString();
    };
}
