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

package com.datastax.stargate.sdk.doc;

import static com.datastax.stargate.sdk.utils.JsonUtils.marshall;
import static com.datastax.stargate.sdk.utils.JsonUtils.unmarshallBean;
import static com.datastax.stargate.sdk.utils.JsonUtils.unmarshallType;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.datastax.stargate.sdk.StargateClientNode;
import com.datastax.stargate.sdk.StargateHttpClient;
import com.datastax.stargate.sdk.core.ApiResponse;
import com.datastax.stargate.sdk.core.ApiResponseHttp;
import com.datastax.stargate.sdk.doc.domain.CollectionDefinition;
import com.datastax.stargate.sdk.doc.domain.DocumentResultPage;
import com.datastax.stargate.sdk.doc.domain.SearchDocumentQuery;
import com.datastax.stargate.sdk.doc.domain.SearchDocumentQuery.SearchDocumentQueryBuilder;
import com.datastax.stargate.sdk.utils.JsonUtils;
import com.fasterxml.jackson.core.type.TypeReference;

/**
 * Work on a dedicated collection without using the Pojo className.
 * 
 * @author Cedrick LUNVEN (@clunven)
 */
public class CollectionClient {
    
    /** Read document id. */
    public static final String DOCUMENT_ID = "documentId";
    
    /** Get Topology of the nodes. */
    private final StargateHttpClient stargateHttpClient;
    
    /** Namespace. */
    protected NamespaceClient namespaceClient;
    
    /** Collection name. */
    protected String collectionName;
    
    /* Mapping type. */
    private static TypeReference<ApiResponse<Map<String, LinkedHashMap<?,?>>>> RESPONSE_SEARCH =  
            new TypeReference<ApiResponse<Map<String, LinkedHashMap<?,?>>>>(){};

    /**
     * Full constructor.
     * 
     * @param namespaceClient NamespaceClient
     * @param collectionName String
     */
    public CollectionClient(StargateHttpClient stargateHttpClient, NamespaceClient namespaceClient,  String collectionName) {
        this.namespaceClient    = namespaceClient;
        this.collectionName     = collectionName;
        this.stargateHttpClient = stargateHttpClient;
    }
    
    /**
     * Get metadata of the collection. There is no dedicated resources we
     * use the list and filter with what we need.
     * 
     * @see <a href="https://stargate.io/docs/stargate/1.0/attachments/docv2.html#operation/getCollection">Reference Documentation</a>
     *
     * @return
     *      metadata of the collection if its exist or empty
     */
    public Optional<CollectionDefinition> find() {
        return namespaceClient.collections()
                .filter(c -> collectionName.equalsIgnoreCase(c.getName()))
                .findFirst();
    }
    
    /**
     * Check if the collection exist.
     * 
     * @return if the collection exists.
     */
    public boolean exist() {
        return namespaceClient.collectionNames()
                .anyMatch(collectionName::equals);
    }
    
    /**
     * Create the collection.
     * 
     * @see <a href="https://stargate.io/docs/stargate/1.0/attachments/docv2.html#operation/createCollection">Reference Documentation</a>
     */
    public void create() {
        stargateHttpClient.POST(collectionResource, 
             "{\"name\":\"" + collectionName + "\"}");
        
    }
    
    /**
     * Deleting the collection
     * 
     * @see <a href="https://stargate.io/docs/stargate/1.0/attachments/docv2.html#operation/deleteCollectionSchema">Reference Documentation</a>
     */
    public void delete() {
        stargateHttpClient.DELETE(collectionResource);
    }
    
    /**
     * Create a new document from any serializable object.
     * 
     * @see <a href="https://stargate.io/docs/stargate/1.0/attachments/docv2.html#operation/addDoc">Reference Documentation</a>
     * 
     * @param <DOC>
     *          working bean type
     * @param doc
     *          working bean instance
     * @return
     *          created document id
     */
    public <DOC> String create(DOC doc) {
        ApiResponseHttp res = stargateHttpClient.POST(collectionResource, marshall(doc));
        try {
            return (String) unmarshallBean(res.getBody(), Map.class)
                    .get(DOCUMENT_ID);
        } catch (Exception e) {
            throw new RuntimeException("Cannot marshall document id", e);
        }
    }

    /**
     * Count items in a collection, it can be slow as we iterate over pages limitating
     * payload and marshalling as much as possible.
     * 
     * @return
     *      number of record
     */
    public int count() {
        AtomicInteger count = new AtomicInteger(0);
        // Invalid field provided for list of ids only
        SearchDocumentQuery query = SearchDocumentQuery
                .builder().select("field_not_exist")
                .withPageSize(2).build();
        ApiResponse<Map<String, LinkedHashMap<?,?>>> searchResults;
        do {
            ApiResponseHttp res = stargateHttpClient.GET(collectionResource, buildSuffixQueryUrl(query));
            searchResults = unmarshallType(res.getBody(), RESPONSE_SEARCH);
            if (null != searchResults && null != searchResults.getData()) {
                count.addAndGet(searchResults.getData().size());
            }
            // Looking for next page
            if (null != searchResults.getPageState())  {
                query = SearchDocumentQuery
                        .builder().select("field_not_exist")
                        .withPageState(searchResults.getPageState())
                        .withPageSize(20).build();
            }
        } while(searchResults.getPageState() != null);
        
        return count.get();
    }

    /**
     * This function will retrieve all documents in the Collection.
     * 
     * <b>USE WITH CAUTION.</b> Default behaviour is using paging, here we are
     * fetching all pages until no more. 
     * 
     * @param <DOC>
     *      generic for working bean
     * @param beanClass
     *      class for working bean 
     * @return
     *      all items in the the collection
     */
    public <DOC> Stream<ApiDocument<DOC>> findAll(Class<DOC> beanClass) {
        List<ApiDocument<DOC>> documents = new ArrayList<>();
        // Loop on pages up to no more pages (could be done)
        String pageState = null;
        do {
            DocumentResultPage<DOC> pageX = findPage(beanClass, SearchDocumentQuery.DEFAULT_PAGING_SIZE, pageState);
            if (pageX.getPageState().isPresent())  {
                pageState = pageX.getPageState().get();
            } else {
                pageState = null;
            }
            documents.addAll(pageX.getResults());
        } while(pageState != null);
        return documents.stream();
    }
    
    /**
     * Find all document matching the query.
     * 
     *  <b>USE WITH CAUTION.</b> Default behaviour is using paging, here we are
     * fetching all pages until no more. 
     * 
     * @param <DOC>
     *       generic for working bean
     * @param query
     *          list of filters
     * @param beanClass
     *          class for working bean  
     * @return
     *          all items matchin criteria
     */
    public <DOC> Stream<ApiDocument<DOC>> findAll(SearchDocumentQuery query, Class<DOC> beanClass) {
        List<ApiDocument<DOC>> documents = new ArrayList<>();
        // Loop on pages up to no more pages (could be done)
        String pageState = null;
        do {
            DocumentResultPage<DOC> pageX = findPage(query, beanClass);
            if (pageX.getPageState().isPresent())  {
                pageState = pageX.getPageState().get();
            } else {
                pageState = null;
            }
            documents.addAll(pageX.getResults());
            // Reuissing query for next page
            query.setPageState(pageState);
        } while(pageState != null);
        return documents.stream();
    }
    
    /**
     * List all items of a collection without filters.
     * 
     * Result is (always) paged, default page sze is 50, API only allow 100 MAX.
     * Here we get first page as we do not provide paging state
     * 
     * @param <DOC>
     *      generic for working bean
     * @param beanClass
     *      class for working bean 
     * @return
     *      a page of results
     */
    public <DOC> DocumentResultPage<DOC> findFirstPage(Class<DOC> beanClass) {
        return findFirstPage(beanClass, SearchDocumentQuery.DEFAULT_PAGING_SIZE);
    }
    
    /**
     * Find a page
     * @param <DOC>
     *      generic for working bean
     * @param beanClass
     *      class for working bean 
     * @param pageSize
     *      size of expected page
     * @return
     *      a page of results
     */
    public <DOC> DocumentResultPage<DOC> findFirstPage(Class<DOC> beanClass, int pageSize) {
        return findPage(beanClass, pageSize, null);
    }
    
    /**
     * Search for a page.
     *
     * @param <DOC>
     *      generic for working bean
     * @param beanClass
     *      class for working bean 
     * @param pageSize
     *      size of expected page
     * @param pageState
     *      cursor in research
     * @return
     *      a page of results
     */
    public <DOC> DocumentResultPage<DOC> findPage(Class<DOC> beanClass, int pageSize, String pageState) {
        SearchDocumentQueryBuilder builder = SearchDocumentQuery.builder().withPageSize(pageSize);
        if (null != pageState) {
            builder.withPageState(pageState);
        }
        return findPage(builder.build(), beanClass);
    }
    
    /**
     * Find a page given some search.
     *
     * @param <DOC>
     *      generic for working bean
     * @param query
     *      filters for the query
     * @param beanClass
     *      class for working bean 
     * @return
     *      a page of results
     * @see https://docs.astra.datastax.com/reference#get_api-rest-v2-namespaces-namespace-id-collections-collection-id-1
     */
    public <DOC> DocumentResultPage<DOC> findPage(SearchDocumentQuery query, Class<DOC> beanClass) {
        try {
            ApiResponseHttp res = stargateHttpClient.GET(collectionResource, buildSuffixQueryUrl(query));
            ApiResponse<Map<String, LinkedHashMap<?,?>>> searchResults = unmarshallType(res.getBody(), RESPONSE_SEARCH);
            return new DocumentResultPage<DOC>(query.getPageSize(), 
                    searchResults.getPageState(), searchResults.getData()
                    .entrySet().stream()
                    .map(doc -> new ApiDocument<DOC>(doc.getKey(), JsonUtils.getObjectMapper().convertValue(doc.getValue(), beanClass)))
                    .collect(Collectors.toList()));
        } catch (Exception e) {
            throw new RuntimeException("Cannot marshall document results", e);
        }
    }
    
    /**
     * Build the filters based on values in the query.
     *
     * @param query
     *      current query
     * @return
     *      the URL
     */
    private String buildSuffixQueryUrl(SearchDocumentQuery query) {
        try {
            StringBuilder sbUrl = new StringBuilder();
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
            if (query.getFieldsToRetrieve().isPresent() && !query.getFieldsToRetrieve().get().isEmpty()) {
                sbUrl.append("&fields=" + 
                        URLEncoder.encode(JsonUtils.collectionAsJson(query.getFieldsToRetrieve().get()), StandardCharsets.UTF_8.toString()));
            }
            return sbUrl.toString();
        } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException("Cannot enode URL", e);
        }
    }
    
    // ---------------------------------
    // ----    Sub Resources        ----
    // ---------------------------------
    
    /**
     * Move to document Resource
     * 
     * @param docId String
     * @return DocumentClient
     */
    public DocumentClient document(String docId) {
        return new DocumentClient(stargateHttpClient, this, docId);
    }
    
    // ---------------------------------
    // ----       Resources         ----
    // ---------------------------------
    
    /**
     * Getter accessor for attribute 'collectionName'.
     *
     * @return
     *       current value of 'collectionName'
     */
    public String getCollectionName() {
        return collectionName;
    }
    
    /** 
     * /v2/schemas/namespaces/{namespace}/collections/{collection} 
     */
    public Function<StargateClientNode, String> collectionResource = 
            (node) -> namespaceClient.collectionsResource.apply(node) +  "/" + collectionName;
    
}
