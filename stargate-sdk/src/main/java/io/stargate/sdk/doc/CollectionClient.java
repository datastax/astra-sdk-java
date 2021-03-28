package io.stargate.sdk.doc;

import static io.stargate.sdk.doc.NamespaceClient.PATH_COLLECTIONS;
import static io.stargate.sdk.doc.NamespaceClient.PATH_NAMESPACES;
import static io.stargate.sdk.utils.ApiSupport.getHttpClient;
import static io.stargate.sdk.utils.ApiSupport.getObjectMapper;
import static io.stargate.sdk.utils.ApiSupport.handleError;
import static io.stargate.sdk.utils.ApiSupport.startRequest;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.type.TypeReference;

import io.stargate.sdk.doc.QueryDocument.QueryDocumentBuilder;
import io.stargate.sdk.exception.CollectionNotFoundException;
import io.stargate.sdk.utils.ApiResponse;
import io.stargate.sdk.utils.Assert;
import io.stargate.sdk.utils.JsonUtils;

/**
 * Work on a dedicated collection without using the Pojo className.
 * 
 * @author Cedrick LUNVEN (@clunven)
 */
public class CollectionClient {
    
    /** Read document id. */
    public static final String DOCUMENT_ID = "documentId";
    
    /** Astra Client. */
    private final ApiDocumentClient docClient;
    
    /** Namespace. */
    private final NamespaceClient namespaceClient;
    
    /** Collection name. */
    private final String collectionName;
    
    /**
     * Full constructor.
     */
    public CollectionClient(ApiDocumentClient docClient,  NamespaceClient namespaceClient,  String collectionName) {
        this.docClient     = docClient;
        this.namespaceClient = namespaceClient;
        this.collectionName  = collectionName;
    }
    
    /**
     * Move to document Resource
     */
    public DocumentClient document(String docId) {
        return new DocumentClient(docClient, namespaceClient, this, docId);
    }
    
    /**
     * Check if the collection exist.
     */
    public boolean exist() {
        Assert.hasLength(collectionName, "collectionName");
        return namespaceClient.collectionNames().anyMatch(collectionName::equals);
    }
    
    public void create() {
        Assert.hasLength(collectionName, "collectionName");
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
        Assert.hasLength(collectionName, "collectionName");
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
     */
    public <DOC extends Serializable> String createNewDocument(DOC doc) {
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
     */
    public <DOC> ResultListPage<DOC> findAll(Class<DOC> clazz) {
        return findAll(clazz, QueryDocument.DEFAULT_PAGING_SIZE);
    }
    public <DOC> ResultListPage<DOC> findAll(Class<DOC> clazz, int pageSize) {
        return findAll(clazz, pageSize, null);
    }
    public <DOC> ResultListPage<DOC> findAll(Class<DOC> clazz, String pageState) {
        return findAll(clazz, QueryDocument.DEFAULT_PAGING_SIZE, pageState);
    }
    public <DOC> ResultListPage<DOC> findAll(Class<DOC> clazz, int pageSize, String pageState) {
        QueryDocumentBuilder builder = QueryDocument.builder().withPageSize(pageSize);
        if (null != pageState) {
            builder.withPageState(pageState);
        }
        return search(builder.build(), clazz);
    }
    
    //https://docs.astra.datastax.com/reference#get_api-rest-v2-namespaces-namespace-id-collections-collection-id-1
    public <DOC> ResultListPage<DOC> search(QueryDocument query, Class<DOC> clazz) {
        Objects.requireNonNull(clazz);
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
             
            return new ResultListPage<DOC>(query.getPageSize(), result.getPageState(), result.getData()
                    .entrySet().stream()
                    .map(doc -> new ApiDocument<DOC>(doc.getKey(), getObjectMapper().convertValue(doc.getValue(), clazz)))
                    .collect(Collectors.toList()));
        } catch (Exception e) {
            throw new RuntimeException("Cannot marshall document results", e);
        }
    }
    
    private String buildQueryUrl(QueryDocument query) {
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
