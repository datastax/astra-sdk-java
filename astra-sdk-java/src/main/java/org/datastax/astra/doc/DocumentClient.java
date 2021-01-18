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
import java.util.Optional;

import org.datastax.astra.AstraClient;
import org.datastax.astra.utils.Assert;

public class DocumentClient {
     
    /** Astra Client. */
    private final AstraClient astraClient;
    
    /** Collection name. */
    private final String collectionName;
    
    /** Namespace name. */
    private final String namespaceName;
    
    /** Unique document identifer. */
    private final String docId;
    
    /**
     * Full constructor.
     */
    public DocumentClient(AstraClient astraClient, String namespaceName, String collectionName, String docId) {
        this.astraClient     = astraClient;
        this.namespaceName   = namespaceName;
        this.collectionName  = collectionName;
        this.docId           = docId;
    }
    
    /**
     * Using current resource GET to evaluate if a document exists.
     *
     * - 200 means the document exists
     * - 204 means document does not exists
     */
    public boolean exist() {
        Assert.hasLength(docId, "documentId");
        try {
            // Create a GET REQUEST
            String uri = astraClient.getBaseUrl()
                    + NamespaceClient.PATH_NAMESPACES  + "/" + namespaceName 
                    + NamespaceClient.PATH_COLLECTIONS + "/" + collectionName
                    + "/" + docId;
            HttpRequest request = HttpRequest.newBuilder()
                    .header(AstraClient.HEADER_CASSANDRA, astraClient.getAuthenticationToken())
                    .uri(URI.create(uri))
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
    public <DOC extends Serializable> String save(DOC doc) {
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
                                       .get(CollectionClient.DOCUMENT_ID);
            
        } catch (Exception e) {
            throw new IllegalArgumentException("An error occured", e);
        }
    }
    
    public <DOC extends Serializable> Optional<DOC> find(Class<DOC> clazz) {
        Assert.hasLength(collectionName, "collectionName");
        Assert.hasLength(docId, "documentId");
        Assert.notNull(clazz, "className");
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .timeout(DEFAULT_TIMEOUT)
                    .header(HEADER_CONTENT_TYPE, DEFAULT_CONTENT_TYPE)
                    .header(AstraClient.HEADER_CASSANDRA, astraClient.getAuthenticationToken())
                    .uri(URI.create(astraClient.getBaseUrl()
                            + NamespaceClient.PATH_NAMESPACES  + "/" + namespaceName 
                            + NamespaceClient.PATH_COLLECTIONS + "/" + collectionName
                            + "/" + docId + "?raw=true"))
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

    /**
     * Leveraging resources DELETE to remove a document, it must exists.
     *
     * @param collectionName
     *          collectionName
     * @param docId
     *          documentId
     */
    public void delete() {
        Assert.hasLength(collectionName, "collectionName");
        Assert.hasLength(docId, "documentId");
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .timeout(DEFAULT_TIMEOUT)
                    .header(HEADER_CONTENT_TYPE, DEFAULT_CONTENT_TYPE)
                    .header(AstraClient.HEADER_CASSANDRA, astraClient.getAuthenticationToken())
                    .uri(URI.create(astraClient.getBaseUrl()
                            + NamespaceClient.PATH_NAMESPACES  + "/" + namespaceName 
                            + NamespaceClient.PATH_COLLECTIONS + "/" + collectionName
                            + "/" + docId))
                    .DELETE().build();
                    
            // Call
            HttpResponse<String> response = astraClient.getHttpClient().send(request, BodyHandlers.ofString());
            if (response.statusCode() != 204) {
                throw new IllegalArgumentException("An error occured: " + response.body());
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("An error occured", e);
        }
    }
    
    
    
}
