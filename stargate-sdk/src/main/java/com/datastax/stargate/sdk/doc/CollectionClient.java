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

import static com.datastax.stargate.sdk.core.ApiSupport.getHttpClient;
import static com.datastax.stargate.sdk.core.ApiSupport.getObjectMapper;
import static com.datastax.stargate.sdk.core.ApiSupport.handleError;
import static com.datastax.stargate.sdk.core.ApiSupport.startRequest;
import static com.datastax.stargate.sdk.doc.NamespaceClient.PATH_COLLECTIONS;
import static com.datastax.stargate.sdk.doc.NamespaceClient.PATH_NAMESPACES;

import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.datastax.stargate.sdk.core.ApiResponse;
import com.datastax.stargate.sdk.doc.domain.CollectionDefinition;
import com.datastax.stargate.sdk.doc.domain.DocumentResultPage;
import com.datastax.stargate.sdk.doc.domain.SearchDocumentQuery;
import com.datastax.stargate.sdk.doc.domain.SearchDocumentQuery.SearchDocumentQueryBuilder;
import com.datastax.stargate.sdk.doc.exception.CollectionNotFoundException;
import com.datastax.stargate.sdk.utils.Assert;
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
    
    /** Astra Client. */
    protected final ApiDocumentClient docClient;
    
    /** Namespace. */
    protected final NamespaceClient namespaceClient;
    
    /** Collection name. */
    protected String collectionName;
    
    /**
     * Full constructor.
     * 
     * @param docClient ApiDocumentClient
     * @param namespaceClient NamespaceClient
     * @param collectionName String
     */
    public CollectionClient(ApiDocumentClient docClient,  NamespaceClient namespaceClient,  String collectionName) {
        this.docClient     = docClient;
        this.namespaceClient = namespaceClient;
        this.collectionName  = collectionName;
    }
    
    /**
     * Move to document Resource
     * 
     * @param docId String
     * @return DocumentClient
     */
    public DocumentClient document(String docId) {
        return new DocumentClient(docClient, namespaceClient, this, docId);
    }
    
    /**
     * Get metadata of the collection. There is no dedicated resources we
     * use the list and filter with what we need.
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
     * @return boolean
     */
    public boolean exist() {
        return namespaceClient.collectionNames()
                .anyMatch(collectionName::equals);
    }
    
    public void create() {
        String createColEndpoint = docClient.getEndPointApiDocument() 
                + PATH_NAMESPACES  + "/" + namespaceClient.getNamespace() 
                + PATH_COLLECTIONS;
        HttpResponse<String> response;
        try {
            response = getHttpClient()
                    .send(startRequest(createColEndpoint, docClient.getToken())
                            .POST(BodyPublishers.ofString("{\"name\":\"" + collectionName + "\"}"))
                            .build(), BodyHandlers.ofString()); 
        } catch (Exception e) {
            throw new RuntimeException("Cannot create new collection " + collectionName, e);
        }
        handleError(response);
    }
    
    public void delete() {
        String delColEndpoint = docClient.getEndPointApiDocument() 
                + PATH_NAMESPACES  + "/" + namespaceClient.getNamespace() 
                + PATH_COLLECTIONS + "/" + collectionName;
        
        HttpResponse<String> response;
        try {
            response = getHttpClient().send(
                    startRequest(delColEndpoint, docClient.getToken()).DELETE().build(), 
                    BodyHandlers.ofString());
        } catch (Exception e) {
            throw new RuntimeException("Cannot delete collection " + collectionName, e);
        }
        if (HttpURLConnection.HTTP_NOT_FOUND == response.statusCode()) {
            throw new CollectionNotFoundException(collectionName);
        }
        handleError(response);
    }
    
    public void upgrade() {
        Assert.hasLength(collectionName, "collectionName");
        String updateColEndpoint = docClient.getEndPointApiDocument() 
                + PATH_NAMESPACES  + "/" + namespaceClient.getNamespace() 
                + PATH_COLLECTIONS + "/" + collectionName 
                + "/upgrade";
        HttpResponse<String> response;
        try {
            response = getHttpClient().send(
                    startRequest(updateColEndpoint, docClient.getToken())
                     .POST(BodyPublishers.noBody()).build(), 
                    BodyHandlers.ofString());
            
        } catch (Exception e) {
            throw new RuntimeException("Cannot update collection " + collectionName, e);
        }
        if (HttpURLConnection.HTTP_NOT_FOUND == response.statusCode()) {
            throw new CollectionNotFoundException(collectionName);
        }
        handleError(response);
    }
    
    /**
     * Create a new document from any serializable object
     * 
     * @param <DOC> DOC
     * @param doc DOC
     * @return DOC
     */
    public <DOC> String create(DOC doc) {
        Objects.requireNonNull(doc);
        String saveDocEndPoint = docClient.getEndPointApiDocument() 
                + PATH_NAMESPACES  + "/" + namespaceClient.getNamespace() 
                + PATH_COLLECTIONS + "/" + collectionName;
        HttpResponse<String> response;
        try {
            String reqBody =  getObjectMapper().writeValueAsString(doc);
            response = getHttpClient().send(
                    startRequest(saveDocEndPoint, docClient.getToken())
                     .POST(BodyPublishers.ofString(reqBody)).build(), 
                    BodyHandlers.ofString());
        } catch (Exception e) {
            throw new RuntimeException("Cannot save document ", e);
        }
        
        handleError(response);
        
        try {
            return (String) getObjectMapper()
                        .readValue(response.body(), Map.class)
                        .get(DOCUMENT_ID);
        } catch (Exception e) {
            throw new RuntimeException("Cannot marshall document id", e);
        }
    }
    
    /**
     * List all items of a collection without filters.
     * 
     * Result is (always) paged, default page sze is 50, API only allow 100 MAX.
     * Here we get first page as we do not provide paging state
     * 
     * @param <DOC> DOC
     * @param clazz DOC
     * @return DOC
     */
    public <DOC> DocumentResultPage<DOC> findFirstPage(Class<DOC> beanClass) {
        return findFirstPage(beanClass, SearchDocumentQuery.DEFAULT_PAGING_SIZE);
    }
    public <DOC> DocumentResultPage<DOC> findFirstPage(Class<DOC> beanClass, int pageSize) {
        return findPage(beanClass, pageSize, null);
    }
    public <DOC> DocumentResultPage<DOC> findPage(Class<DOC> beanClass, int pageSize, String pageState) {
        SearchDocumentQueryBuilder builder = SearchDocumentQuery.builder().withPageSize(pageSize);
        if (null != pageState) {
            builder.withPageState(pageState);
        }
        return search(builder.build(), beanClass);
    }
    
    /**
     * This function will retrieve all documents in the Collection.
     * 
     * <b>USE WITH CAUTION.</b> Default behaviour is using paging, here we are
     * fetching all pages until no more. 
     */
    public <DOC> Stream<ApiDocument<DOC>> findAll(Class<DOC> beanClass) {
        List<ApiDocument<DOC>> persons = new ArrayList<>();
        // Loop on pages up to no more pages (could be done)
        String pageState = null;
        do {
            DocumentResultPage<DOC> pageX = findPage(beanClass, SearchDocumentQuery.DEFAULT_PAGING_SIZE, pageState);
            if (pageX.getPageState().isPresent())  {
                pageState = pageX.getPageState().get();
            } else {
                pageState = null;
            }
            persons.addAll(pageX.getResults());
        } while(pageState != null);
        return persons.stream();
    }
    
    //https://docs.astra.datastax.com/reference#get_api-rest-v2-namespaces-namespace-id-collections-collection-id-1
    public <DOC> DocumentResultPage<DOC> search(SearchDocumentQuery query, Class<DOC> beanClass) {
        HttpResponse<String> response;
        try {
             // Invoke as JSON
            response = getHttpClient().send(startRequest(
                            buildQueryUrl(query), docClient.getToken()).GET().build(), 
                            BodyHandlers.ofString());
        } catch (Exception e) {
            throw new RuntimeException("Cannot search for documents ", e);
        }   
        
        handleError(response);
         
        try {
             // Marshalling (using LinkedHashMap DOC was a bit to much Generics for Jackson here)
             ApiResponse<Map<String, LinkedHashMap<?,?>>> result = getObjectMapper()
                     .readValue(response.body(), 
                             new TypeReference<ApiResponse<Map<String, LinkedHashMap<?,?>>>>(){});
           
            return new DocumentResultPage<DOC>(query.getPageSize(), result.getPageState(), result.getData()
                    .entrySet().stream()
                    .map(doc -> new ApiDocument<DOC>(doc.getKey(), getObjectMapper().convertValue(doc.getValue(), beanClass)))
                    .collect(Collectors.toList()));
        } catch (Exception e) {
            throw new RuntimeException("Cannot marshall document results", e);
        }
    }
    
    
    
    private String buildQueryUrl(SearchDocumentQuery query) {
        try {
            StringBuilder sbUrl = new StringBuilder(docClient.getEndPointApiDocument());
            // Navigate to Namespace
            sbUrl.append(NamespaceClient.PATH_NAMESPACES  + "/" + namespaceClient.getNamespace()); 
            // Navigate to collection
            sbUrl.append(NamespaceClient.PATH_COLLECTIONS + "/" + collectionName);
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
            if (query.getFieldsToRetrieve().isPresent() && !query.getFieldsToRetrieve().get().isEmpty()) {
                sbUrl.append("&fields=" + 
                        URLEncoder.encode(JsonUtils.collectionAsJson(query.getFieldsToRetrieve().get()), StandardCharsets.UTF_8.toString()));
            }
            return sbUrl.toString();
        } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException("Cannot enode URL", e);
        }
    }
    
    /**
     * Getter accessor for attribute 'collectionName'.
     *
     * @return
     *       current value of 'collectionName'
     */
    public String getCollectionName() {
        return collectionName;
    }
    
    
}
