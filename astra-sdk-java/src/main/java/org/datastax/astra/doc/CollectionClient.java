package org.datastax.astra.doc;

import static org.datastax.astra.AstraClient.DEFAULT_CONTENT_TYPE;
import static org.datastax.astra.AstraClient.DEFAULT_TIMEOUT;
import static org.datastax.astra.AstraClient.HEADER_CONTENT_TYPE;

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

import org.datastax.astra.AstraClient;
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
    private final AstraClient astraClient;
    
    /** Namespace. */
    private final NamespaceClient namespaceClient;
    
    /** Collection name. */
    private final String collectionName;
    
    /** Namespace name. */
    private final String namespaceName;
    
    /**
     * Full constructor.
     */
    public CollectionClient(AstraClient astraClient,  NamespaceClient namespaceClient, String namespaceName, String collectionName) {
        this.astraClient     = astraClient;
        this.namespaceClient = namespaceClient;
        this.namespaceName   = namespaceName;
        this.collectionName  = collectionName;
    }
    
    /**
     * Move to document Resource
     */
    public DocumentClient document(String docId) {
        return new DocumentClient(astraClient, namespaceName, collectionName, docId);
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
                    .timeout(DEFAULT_TIMEOUT)
                    .header(HEADER_CONTENT_TYPE, DEFAULT_CONTENT_TYPE)
                    .header(AstraClient.HEADER_CASSANDRA, astraClient.getAuthenticationToken())
                    .uri(URI.create(astraClient.getBaseUrl() 
                            + NamespaceClient.PATH_NAMESPACES  + "/" + namespaceName 
                            + NamespaceClient.PATH_COLLECTIONS))
                    .POST(BodyPublishers.ofString("{\"name\":\"" + collectionName + "\"}"))
                    .build();
            
            HttpResponse<String> response = astraClient.getHttpClient()
                    .send(request, BodyHandlers.ofString());
            
            AstraClient.handleError(response);
            
        } catch (Exception e) {
            throw new IllegalArgumentException("Cannot create a new collection", e);
        }
    }
    
    public void delete() {
        Assert.hasLength(collectionName, "collectionName");
        try {
            
            // Create a GET REQUEST
            HttpRequest request = HttpRequest.newBuilder()
                    .timeout(DEFAULT_TIMEOUT)
                    .header(HEADER_CONTENT_TYPE, DEFAULT_CONTENT_TYPE)
                    .header(AstraClient.HEADER_CASSANDRA, astraClient.getAuthenticationToken())
                    .uri(URI.create(astraClient.getBaseUrl() 
                            + NamespaceClient.PATH_NAMESPACES  + "/" + namespaceName 
                            + NamespaceClient.PATH_COLLECTIONS + "/" + collectionName))
                    .DELETE().build();
            
            // Invoke
            HttpResponse<String> response = astraClient.getHttpClient()
                    .send(request, BodyHandlers.ofString());
            
            AstraClient.handleError(response);
            
        } catch (Exception e) {
            throw new IllegalArgumentException("Cannot create a new collection", e);
        }
    }
    
    public <DOC extends Serializable> String saveAsync(DOC doc) {
        return null;
    }
    
    /**
     * Create a new document from any serializable object
     */
    public <DOC extends Serializable> String save(DOC doc) {
        Objects.requireNonNull(doc);
        try {
            Builder reqBuilder = HttpRequest.newBuilder()
                    .timeout(DEFAULT_TIMEOUT)
                    .header(HEADER_CONTENT_TYPE, DEFAULT_CONTENT_TYPE)
                    .header(AstraClient.HEADER_CASSANDRA, astraClient.getAuthenticationToken())
                    .uri(URI.create(astraClient.getBaseUrl()
                            + NamespaceClient.PATH_NAMESPACES  + "/" + namespaceName 
                            + NamespaceClient.PATH_COLLECTIONS + "/" + collectionName))
                    .POST(BodyPublishers.ofString(
                            astraClient.getObjectMapper().writeValueAsString(doc)));
            
            // Call
            HttpResponse<String> response = astraClient.getHttpClient()
                    .send(reqBuilder.build(), BodyHandlers.ofString());
            
            AstraClient.handleError(response);
            return (String) astraClient.getObjectMapper().readValue(response.body(), Map.class)
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
            StringBuilder sbUrl = new StringBuilder(astraClient.getBaseUrl());
            // Navigate to Namespace
            sbUrl.append(NamespaceClient.PATH_NAMESPACES  + "/" + namespaceName); 
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
    
    public <DOC> ResultListPage<DOC> search(QueryDocument query, Class<DOC> clazz) {
        Objects.requireNonNull(clazz);
        try {
            
            System.out.println(buildQueryUrl(query));
            // Create Request
            HttpRequest req = HttpRequest.newBuilder()
                    .timeout(DEFAULT_TIMEOUT)
                    .header(HEADER_CONTENT_TYPE, DEFAULT_CONTENT_TYPE)
                    .header(AstraClient.HEADER_CASSANDRA, astraClient.getAuthenticationToken())
                    .uri(URI.create(buildQueryUrl(query)))
                    .GET().build();
            
             // Invoke as JSON
             HttpResponse<String> response = astraClient.getHttpClient()
                    .send(req, BodyHandlers.ofString());
             
             // Error Processing
             AstraClient.handleError(response);
             
             // Marshalling (using LinkedHashMap DOC was a bit to much Generics for Jackson here)
             ApiResponse<Map<String, LinkedHashMap<?,?>>> result = 
                     astraClient.getObjectMapper().readValue(
                            response.body(), new TypeReference<ApiResponse<Map<String, LinkedHashMap<?,?>>>>(){});
             
             // Mapping to ResultListPage
            return new ResultListPage<DOC>(query.getPageSize(), result.getPageState(), result.getData()
                    .entrySet().stream()
                    .map(doc -> new AstraDocument<DOC>(doc.getKey(), astraClient
                            .getObjectMapper().convertValue(doc.getValue(), clazz)))
                    .collect(Collectors.toList()));
        } catch (Exception e) {
            throw new IllegalArgumentException("An error occured", e);
        }
    }

    /**
     * Those are first level fields you CANNOT use level1.level2 as fieldName.
     * You do not marshall but get a map of fields only
     *
    @SuppressWarnings("rawtypes")
    public ResultListPage<Map> findAll(String...fieldsToRetrieve) {
        return findAll(QueryDocument.DEFAULT_PAGING_SIZE, fieldsToRetrieve);
    }
    @SuppressWarnings("rawtypes")
    public ResultListPage<Map> findAll(int pageSize, String...fieldsToRetrieve) {
        return findAll(null, pageSize, fieldsToRetrieve);
    }
    @SuppressWarnings("rawtypes")
    public ResultListPage<Map> findAll(String pageState, int pageSize, String...fieldsToRetrieve) {
        return search(QueryDocument.builder()
                    .withPageSize(pageSize)
                    .withPageState(pageState)
                    .withReturnedFields(fieldsToRetrieve)
                    .build());
    }
    
    @SuppressWarnings("rawtypes")
    public ResultListPage<Map> search(QueryDocument query) {
        Objects.requireNonNull(query);
        return null;
    }
    */
    
    
}
