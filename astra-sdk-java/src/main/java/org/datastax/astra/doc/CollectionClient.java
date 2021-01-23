package org.datastax.astra.doc;

import static org.datastax.astra.api.AbstractApiClient.CONTENT_TYPE_JSON;
import static org.datastax.astra.api.AbstractApiClient.HEADER_CASSANDRA;
import static org.datastax.astra.api.AbstractApiClient.HEADER_CONTENT_TYPE;
import static org.datastax.astra.api.AbstractApiClient.REQUEST_TIMOUT;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpRequest.Builder;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.datastax.astra.api.ApiResponse;
import org.datastax.astra.schemas.QueryDocument;
import org.datastax.astra.schemas.QueryDocument.QueryDocumentBuilder;
import org.datastax.astra.utils.Assert;
import org.datastax.astra.utils.JsonUtils;

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
        try {
            // Create a GET REQUEST
            HttpRequest request = HttpRequest.newBuilder()
                    .timeout(REQUEST_TIMOUT)
                    .header(HEADER_CONTENT_TYPE, CONTENT_TYPE_JSON)
                    .header(HEADER_CASSANDRA, docClient.getToken())
                    .uri(URI.create(docClient.getBaseUrl() 
                            + NamespaceClient.PATH_NAMESPACES  + "/" + namespaceClient.getNamespace() 
                            + NamespaceClient.PATH_COLLECTIONS))
                    .POST(BodyPublishers.ofString("{\"name\":\"" + collectionName + "\"}"))
                    .build();
            
            System.out.println(namespaceClient.getNamespace());
            HttpResponse<String> response = ApiDocumentClient.getHttpClient()
                    .send(request, BodyHandlers.ofString());
            
            docClient.handleError(response);
            
        } catch (Exception e) {
            throw new IllegalArgumentException("Cannot create a new collection", e);
        }
    }
    
    public void delete() {
        Assert.hasLength(collectionName, "collectionName");
        try {
            
            // Create a GET REQUEST
            HttpRequest request = HttpRequest.newBuilder()
                    .timeout(REQUEST_TIMOUT)
                    .header(HEADER_CONTENT_TYPE, CONTENT_TYPE_JSON)
                    .header(HEADER_CASSANDRA, docClient.getToken())
                    .uri(URI.create(docClient.getBaseUrl() 
                            + NamespaceClient.PATH_NAMESPACES  + "/" + namespaceClient.getNamespace() 
                            + NamespaceClient.PATH_COLLECTIONS + "/" + collectionName))
                    .DELETE().build();
            
            // Invoke
            HttpResponse<String> response = ApiDocumentClient.getHttpClient()
                    .send(request, BodyHandlers.ofString());
            
            docClient.handleError(response);
            
        } catch (Exception e) {
            throw new IllegalArgumentException("Cannot create a new collection", e);
        }
    }
    
    /**
     * Create a new document from any serializable object
     */
    public <DOC extends Serializable> String save(DOC doc) {
        Objects.requireNonNull(doc);
        try {
            Builder reqBuilder = HttpRequest.newBuilder()
                    .timeout(REQUEST_TIMOUT)
                    .header(HEADER_CONTENT_TYPE, CONTENT_TYPE_JSON)
                    .header(HEADER_CASSANDRA, docClient.getToken())
                    .uri(URI.create(docClient.getBaseUrl() 
                            + NamespaceClient.PATH_NAMESPACES  + "/" + namespaceClient.getNamespace() 
                            + NamespaceClient.PATH_COLLECTIONS + "/" + collectionName))
                    .POST(BodyPublishers.ofString(
                            ApiDocumentClient.getObjectMapper().writeValueAsString(doc)));
            
            // Call
            HttpResponse<String> response = ApiDocumentClient.getHttpClient()
                    .send(reqBuilder.build(), BodyHandlers.ofString());
            
            docClient.handleError(response);
            return (String) ApiDocumentClient
                    .getObjectMapper()
                    .readValue(response.body(), Map.class)
                    .get(DOCUMENT_ID);
            
        } catch (Exception e) {
            throw new IllegalArgumentException("An error occured", e);
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
    public <DOC> ResultListPage<DOC> findAll(Class<DOC> clazz, int pageSize, String pageState) {
        QueryDocumentBuilder builder = QueryDocument.builder().withPageSize(pageSize);
        if (null != pageState) {
            builder.withPageState(pageState);
        }
        return search(builder.build(), clazz);
    }
    
    private String buildQueryUrl(QueryDocument query) {
        try {
            StringBuilder sbUrl = new StringBuilder(docClient.getBaseUrl());
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
    
    //https://docs.astra.datastax.com/reference#get_api-rest-v2-namespaces-namespace-id-collections-collection-id-1
    public <DOC> ResultListPage<DOC> search(QueryDocument query, Class<DOC> clazz) {
        Objects.requireNonNull(clazz);
        try {
            
            System.out.println(buildQueryUrl(query));
            // Create Request
            HttpRequest req = HttpRequest.newBuilder()
                    .timeout(REQUEST_TIMOUT)
                    .header(HEADER_CONTENT_TYPE, CONTENT_TYPE_JSON)
                    .header(HEADER_CASSANDRA, docClient.getToken())
                    .uri(URI.create(buildQueryUrl(query)))
                    .GET().build();
            
             // Invoke as JSON
             HttpResponse<String> response = ApiDocumentClient.getHttpClient()
                    .send(req, BodyHandlers.ofString());
             
             // Error Processing
             docClient.handleError(response);
             
             // Marshalling (using LinkedHashMap DOC was a bit to much Generics for Jackson here)
             ApiResponse<Map<String, LinkedHashMap<?,?>>> result = 
                     ApiDocumentClient.getObjectMapper().readValue(
                            response.body(), new TypeReference<ApiResponse<Map<String, LinkedHashMap<?,?>>>>(){});
             
             // Mapping to ResultListPage
            return new ResultListPage<DOC>(query.getPageSize(), result.getPageState(), result.getData()
                    .entrySet().stream()
                    .map(doc -> new AstraDocument<DOC>(doc.getKey(), ApiDocumentClient
                            .getObjectMapper().convertValue(doc.getValue(), clazz)))
                    .collect(Collectors.toList()));
        } catch (Exception e) {
            throw new IllegalArgumentException("An error occured", e);
        }
    }
    
    // RENAME ? TODO ASK QUESTION
    //https://docs.astra.datastax.com/reference#put_api-rest-v2-schemas-namespaces-namespace-id-collections-collection-id-1
    public void update() {
        throw new UnsupportedOperationException("");
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
