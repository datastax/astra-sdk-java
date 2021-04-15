package io.stargate.sdk.rest;

import static io.stargate.sdk.core.ApiSupport.getHttpClient;
import static io.stargate.sdk.core.ApiSupport.getObjectMapper;
import static io.stargate.sdk.core.ApiSupport.handleError;
import static io.stargate.sdk.core.ApiSupport.startRequest;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.type.TypeReference;

import io.stargate.sdk.core.ApiResponse;
import io.stargate.sdk.core.ResultPage;
import io.stargate.sdk.rest.domain.QueryWithKey;
import io.stargate.sdk.rest.domain.Row;
import io.stargate.sdk.rest.domain.RowMapper;
import io.stargate.sdk.rest.domain.RowResultPage;
import io.stargate.sdk.rest.domain.SortField;
import io.stargate.sdk.utils.Assert;
import io.stargate.sdk.utils.JsonUtils;

/**
 * Operation on a record.
 * 
 * @author Cedrick LUNVEN (@clunven)
 */
public class KeyClient {
    
    /** Collection name. */
    private final TableClient tableClient;
    
    /** Search PK. */
    private final List< Object> key;
    
    /** Current auth token. */
    private final String token;
    
    /**
     * Full constructor.
     */
    public KeyClient(String token, TableClient tableClient, Object... key) {
        this.token          = token;
        this.tableClient    = tableClient;
        this.key            = new ArrayList<>(Arrays.asList(key));
    }
    
    /**
     * Build endpoint of this resource
     */
    private String getEndPointCurrentKey() {
        StringBuilder sbUrl = new StringBuilder(tableClient.getEndPointTable());
        Assert.notNull(key, "key");
        Assert.isTrue(!key.isEmpty(), "key");
        try {
            for(Object pk : key) {
                sbUrl.append("/" +  URLEncoder.encode(pk.toString(), StandardCharsets.UTF_8.toString()));
            }
        } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException("Cannot enode URL", e);
        }
        return sbUrl.toString();
    }
    
    /**
     * Retrieve a set of Rows from Primary key value.
     *
     * @param query
     * @return
     */
    // GET
    public RowResultPage find(QueryWithKey query) {
        Objects.requireNonNull(query);
        HttpResponse<String> response;
        try {
             // Invoke as JSON
            response = getHttpClient().send(
                    startRequest(buildQueryUrl(query), 
                            token).GET().build(), BodyHandlers.ofString());
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
    
    /**
     * Retrieve a set of Rows from Primary key value.
     *
     * @param query
     * @return
     */
    public <T> ResultPage<T> find(QueryWithKey query, RowMapper<T> mapper) {
        RowResultPage rrp = find(query);
        return new ResultPage<T>(rrp.getPageSize(), 
                rrp.getPageState().orElse(null),
                rrp.getResults().stream()
                   .map(mapper::map)
                   .collect(Collectors.toList()));
    }
    
    // DELETE
    public void delete() {
        HttpResponse<String> response;
        try {
            response = getHttpClient()
                    .send(startRequest(getEndPointCurrentKey(), token)
                           .DELETE().build(), BodyHandlers.ofString());
        } catch (Exception e) {
            throw new RuntimeException("Cannot search for Rows ", e);
        }
        
        handleError(response);
    }
    
    // PATCH
    public void update(Map<String, Object> newRecord) {
        HttpResponse<String> response;
        try {
            String reqBody = getObjectMapper().writeValueAsString(newRecord);
            response = getHttpClient().send(
                        startRequest(getEndPointCurrentKey(), token)
                            .method("PATCH", BodyPublishers.ofString(reqBody)).build(), BodyHandlers.ofString());
        } catch (Exception e) {
            throw new RuntimeException("Cannot search for Rows ", e);
        }
        
        handleError(response);
    }
    
    // PUT
    public void replace(Map<String, Object> newRecord) {
       HttpResponse<String> response;
        try {
            String reqBody = getObjectMapper().writeValueAsString(newRecord);
            response = getHttpClient().send(
                    startRequest(getEndPointCurrentKey(), token)
                    .PUT(BodyPublishers.ofString(reqBody)).build(),
                    BodyHandlers.ofString());
        } catch (Exception e) {
            throw new RuntimeException("Cannot search for Rows ", e);
        }
        handleError(response);
    }
    
    
    /**
     * Build complex URL as expected with primaryKey.
     */
    private String buildQueryUrl(QueryWithKey query) {
        try {
            StringBuilder sbUrl = new StringBuilder(getEndPointCurrentKey());
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
  

}
