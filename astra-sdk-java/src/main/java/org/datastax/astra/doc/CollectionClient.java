package org.datastax.astra.doc;

import static org.datastax.astra.AstraClient.DEFAULT_CONTENT_TYPE;
import static org.datastax.astra.AstraClient.DEFAULT_TIMEOUT;
import static org.datastax.astra.AstraClient.HEADER_CONTENT_TYPE;

import java.io.Serializable;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpRequest.Builder;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.datastax.astra.AstraClient;
import org.datastax.astra.utils.Assert;

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
     * Check if the collection exist.
     */
    public boolean exist() {
        Assert.hasLength(collectionName, "collectionName");
        return namespaceClient.collectionNames()
                              .anyMatch(collectionName::equals);
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

    /**
     * Using current resource GET to evaluate if a document exists.
     *
     * - 200 means the document exists
     * - otherwise it does not. As of now, the API return 204 if not found (it should be 404)
     */
    public boolean exist(String docId) {
        Assert.hasLength(docId, "documentId");
        try {
            // Create a GET REQUEST
            HttpRequest request = HttpRequest.newBuilder()
                    .timeout(DEFAULT_TIMEOUT)
                    .header(HEADER_CONTENT_TYPE, DEFAULT_CONTENT_TYPE)
                    .header(AstraClient.HEADER_CASSANDRA, astraClient.getAuthenticationToken())
                    .uri(URI.create(astraClient.getBaseUrl()
                            + NamespaceClient.PATH_NAMESPACES  + "/" + namespaceName 
                            + NamespaceClient.PATH_COLLECTIONS + "/" + collectionName
                            + "/" + docId))
                    .GET().build();
            HttpResponse<Void> ss = astraClient.getHttpClient()
                    .send(request, BodyHandlers.discarding());
            return ss.statusCode() == 200;
        } catch (Exception e) {
            throw new IllegalArgumentException("An error occured", e);
        }
    }
    
    /**
     * Updating an existing document or enforce the id
     */
    public <DOC extends Serializable> String save(DOC doc, String docId) {
        Assert.notNull(doc, "document");
        Assert.hasLength(docId, "Document identifier");
        try {
            Builder reqBuilder = HttpRequest.newBuilder()
                    .timeout(DEFAULT_TIMEOUT)
                    .header(HEADER_CONTENT_TYPE, DEFAULT_CONTENT_TYPE)
                    .header(AstraClient.HEADER_CASSANDRA, astraClient.getAuthenticationToken())
                    .uri(URI.create(astraClient.getBaseUrl()
                            + NamespaceClient.PATH_NAMESPACES  + "/" + namespaceName 
                            + NamespaceClient.PATH_COLLECTIONS + "/" + collectionName
                            + "/" + docId))
                    .PUT(BodyPublishers.ofString(
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
    
    public <DOC extends Serializable> Optional<DOC> findById(String docId, Class<DOC> clazz) {
        Assert.hasLength(collectionName, "collectionName");
        Assert.hasLength(docId, "documentId");
        Assert.notNull(clazz, "className");
        try {
            
            String uri = astraClient.getBaseUrl()
                    + "/v2/namespaces/" + namespaceName 
                    + "/collections/"   + collectionName
                    + "/" + docId;
            
            // Creating Req
            HttpRequest request = HttpRequest.newBuilder()
                    .timeout(DEFAULT_TIMEOUT)
                    .header(HEADER_CONTENT_TYPE, DEFAULT_CONTENT_TYPE)
                    .header(AstraClient.HEADER_CASSANDRA, astraClient.getAuthenticationToken())
                    .uri(URI.create(uri))
                    .GET().build();
                    
            // Call
            HttpResponse<String> response = astraClient.getHttpClient().send(request, BodyHandlers.ofString());
            
            if (null !=response && response.statusCode() == 200) {
                return Optional.of(astraClient.getObjectMapper().readValue(response.body(), clazz));
            } else if (204 == response.statusCode()) {
                return Optional.empty();
            } else {
                throw new IllegalArgumentException("An error occured: " + response.body());
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("An error occured", e);
        }
    }
    
}
