package com.dstx.astra.sdk.document;

import static com.dstx.astra.sdk.utils.ApiSupport.CONTENT_TYPE_JSON;
import static com.dstx.astra.sdk.utils.ApiSupport.HEADER_CASSANDRA;
import static com.dstx.astra.sdk.utils.ApiSupport.HEADER_CONTENT_TYPE;
import static com.dstx.astra.sdk.utils.ApiSupport.REQUEST_TIMOUT;

import java.io.Serializable;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpRequest.Builder;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.Map;
import java.util.Optional;

import com.dstx.astra.sdk.utils.Assert;

public class DocumentClient {
     
    /** Astra Client. */
    private final ApiDocumentClient docClient;
    
    /** Namespace. */
    private final NamespaceClient namespaceClient;
    
    /** Namespace. */
    private final CollectionClient collectionClient;
    
    /** Unique document identifer. */
    private final String docId;
    
    /**
     * Full constructor.
     */
    public DocumentClient(
            ApiDocumentClient docClient, NamespaceClient namespaceClient, 
            CollectionClient collectionClient, String docId) {
        this.docClient     = docClient;
        this.namespaceClient   = namespaceClient;
        this.collectionClient  = collectionClient;
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
            String uri = docClient.getBaseUrl()
                    + NamespaceClient.PATH_NAMESPACES  + "/" + namespaceClient.getNamespace() 
                    + NamespaceClient.PATH_COLLECTIONS + "/" + collectionClient.getCollectionName()
                    + "/" + docId;
            HttpRequest request = HttpRequest.newBuilder()
                    .timeout(REQUEST_TIMOUT)
                    .header(HEADER_CONTENT_TYPE, CONTENT_TYPE_JSON)
                    .header(HEADER_CASSANDRA, docClient.getToken())
                    .uri(URI.create(uri))
                    .GET().build();
            HttpResponse<Void> ss = ApiDocumentClient.getHttpClient()
                    .send(request, BodyHandlers.discarding());
            
            return ss.statusCode() == 200;
        } catch (Exception e) {
            throw new IllegalArgumentException("An error occured", e);
        }
    }
    
    /**
     * Updating an existing document or enforce the id.
     * 
     * Partial updates with documentPath 
     */
    public <DOC extends Serializable> String save(DOC doc) {
        Assert.notNull(doc, "document");
        Assert.hasLength(docId, "Document identifier");
        try {
            Builder reqBuilder = HttpRequest.newBuilder()
                    .timeout(REQUEST_TIMOUT)
                    .header(HEADER_CONTENT_TYPE, CONTENT_TYPE_JSON)
                    .header(HEADER_CASSANDRA, docClient.getToken())
                    .uri(URI.create(docClient.getBaseUrl()
                            + NamespaceClient.PATH_NAMESPACES  + "/" + namespaceClient.getNamespace() 
                            + NamespaceClient.PATH_COLLECTIONS + "/" + collectionClient.getCollectionName()
                            + "/" + docId))
                    .PUT(BodyPublishers.ofString(
                            ApiDocumentClient.getObjectMapper().writeValueAsString(doc)));
            
            // Call
            HttpResponse<String> response = ApiDocumentClient.getHttpClient()
                    .send(reqBuilder.build(), BodyHandlers.ofString());
            
            docClient.handleError(response);
            return (String) ApiDocumentClient.getObjectMapper().readValue(response.body(), Map.class)
                                       .get(CollectionClient.DOCUMENT_ID);
            
        } catch (Exception e) {
            throw new IllegalArgumentException("An error occured", e);
        }
    }
    
    public <DOC extends Serializable> Optional<DOC> find(Class<DOC> clazz) {
        Assert.hasLength(docId, "documentId");
        Assert.notNull(clazz, "className");
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .timeout(REQUEST_TIMOUT)
                    .header(HEADER_CONTENT_TYPE, CONTENT_TYPE_JSON)
                    .header(HEADER_CASSANDRA, docClient.getToken())
                    .uri(URI.create(docClient.getBaseUrl()
                            + NamespaceClient.PATH_NAMESPACES  + "/" + namespaceClient.getNamespace()
                            + NamespaceClient.PATH_COLLECTIONS + "/" + collectionClient.getCollectionName()
                            + "/" + docId + "?raw=true"))
                    .GET().build();
                    
            // Call
            HttpResponse<String> response = ApiDocumentClient.getHttpClient().send(request, BodyHandlers.ofString());
            if (null !=response && response.statusCode() == 200) {
                
                return Optional.of(ApiDocumentClient.getObjectMapper().readValue(response.body(), clazz));
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
        Assert.hasLength(docId, "documentId");
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .timeout(REQUEST_TIMOUT)
                    .header(HEADER_CONTENT_TYPE, CONTENT_TYPE_JSON)
                    .header(HEADER_CASSANDRA, docClient.getToken())
                    .uri(URI.create(docClient.getBaseUrl()
                            + NamespaceClient.PATH_NAMESPACES  + "/" + namespaceClient.getNamespace()
                            + NamespaceClient.PATH_COLLECTIONS + "/" + collectionClient.getCollectionName()
                            + "/" + docId))
                    .DELETE().build();
                    
            // Call
            HttpResponse<String> response = ApiDocumentClient.getHttpClient().send(request, BodyHandlers.ofString());
            if (response.statusCode() != 204) {
                throw new IllegalArgumentException("An error occured: " + response.body());
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("An error occured", e);
        }
    }
    
    
    
}
