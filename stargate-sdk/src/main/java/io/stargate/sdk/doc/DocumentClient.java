package io.stargate.sdk.doc;

import static io.stargate.sdk.doc.NamespaceClient.PATH_COLLECTIONS;
import static io.stargate.sdk.doc.NamespaceClient.PATH_NAMESPACES;
import static io.stargate.sdk.utils.ApiSupport.getHttpClient;
import static io.stargate.sdk.utils.ApiSupport.getObjectMapper;
import static io.stargate.sdk.utils.ApiSupport.handleError;
import static io.stargate.sdk.utils.ApiSupport.startRequest;

import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.Map;
import java.util.Optional;

import io.stargate.sdk.utils.Assert;

/**
 * Part of the Document API in stargate wrapper for methods at the document level.
 *
 * @author Cedrick LUNVEN (@clunven)
 */
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
        this.docClient         = docClient;
        this.namespaceClient   = namespaceClient;
        this.collectionClient  = collectionClient;
        this.docId             = docId;
    }
    
    private String getCurrentDocEndpoint() {
        return docClient.getEndPointApiDocument()
                + PATH_NAMESPACES  + "/" + namespaceClient.getNamespace() 
                + PATH_COLLECTIONS + "/" + collectionClient.getCollectionName()
                + "/" + docId;
    }
    
    /**
     * Leverage find() to check existence without eventual formatting issues. 
     * 
     * @see https://docs.datastax.com/en/astra/docs/_attachments/docv2.html#operation/getDocById
     */
    public boolean exist() {
        Assert.hasLength(docId, "documentId");
        try {
            return HttpURLConnection.HTTP_OK == getHttpClient().send(
                    startRequest(getCurrentDocEndpoint(), docClient.getToken())
                      .GET().build(), BodyHandlers.discarding()).statusCode();
        } catch (Exception e) {
            throw new RuntimeException("Cannot test document existence", e);
        }
    }
    
    /**
     * Replace a document
     * 
     * @param <DOC>
     *      working class
     * @param clazz
     *      working class
     * 
     * @see https://docs.datastax.com/en/astra/docs/_attachments/docv2.html#operation/replaceDoc
     */
    public <DOC extends Serializable> String upsert(DOC doc) {
        Assert.notNull(doc, "document");
        Assert.hasLength(docId, "Document identifier");
        HttpResponse<String> response;
        try {
            String reqBody = getObjectMapper().writeValueAsString(doc);
           response = getHttpClient().send(
                   startRequest(getCurrentDocEndpoint(), docClient.getToken())
                   .PUT(BodyPublishers.ofString(reqBody)).build(),
                   BodyHandlers.ofString());
        } catch (Exception e) {
            throw new RuntimeException("Cannot save document:", e);
        }    
        
        handleError(response);
        
        try {
            return (String) getObjectMapper()
                    .readValue(response.body(), Map.class)
                    .get(CollectionClient.DOCUMENT_ID);
        } catch (Exception e) {
            throw new RuntimeException("Cannot retrieve document id when saving doc:", e);
        }
    }
    
    /**
     * Update part of a document
     * 
     * @param <DOC>
     *      working class
     * @param clazz
     *      working class
     * 
     * @see https://docs.datastax.com/en/astra/docs/_attachments/docv2.html#operation/updatePartOfDoc
     */
    public <DOC extends Serializable> String update(DOC doc) {
        Assert.notNull(doc, "document");
        Assert.hasLength(docId, "Document identifier");
        HttpResponse<String> response;
        try {
            String reqBody = getObjectMapper().writeValueAsString(doc);
           response = getHttpClient().send(
                   startRequest(getCurrentDocEndpoint(), docClient.getToken()).method("PATCH", BodyPublishers.ofString(reqBody)).build(),
                   BodyHandlers.ofString());
        } catch (Exception e) {
            throw new RuntimeException("Cannot save document:", e);
        }    
        
        handleError(response);
        
        try {
            return (String) getObjectMapper()
                    .readValue(response.body(), Map.class)
                    .get(CollectionClient.DOCUMENT_ID);
        } catch (Exception e) {
            throw new RuntimeException("Cannot retrieve document id when saving doc:", e);
        }
    }
    
    /**
     * Get a document by {document-id}.
     *
     * @param <DOC>
     *      working class
     * @param clazz
     *      working class
     * @return
     *      a document if exist
     *      
     * @see https://docs.datastax.com/en/astra/docs/_attachments/docv2.html#operation/getDocById
     */
    public <DOC extends Serializable> Optional<DOC> find(Class<DOC> clazz) {
        Assert.hasLength(docId, "documentId");
        Assert.notNull(clazz, "className");
        HttpResponse<String> response;
        try {
            response = getHttpClient().send(
                    startRequest(getCurrentDocEndpoint() + "?raw=true", docClient.getToken())
                        .GET().build(), BodyHandlers.ofString());
        } catch (Exception e) {
            throw new RuntimeException("Cannot invoke API to find document:", e);
        }
        
        handleError(response);
        
        try {
            if (HttpURLConnection.HTTP_OK == response.statusCode()) {
                return Optional.of(getObjectMapper().readValue(response.body(), clazz));
           } else if (HttpURLConnection.HTTP_NO_CONTENT == response.statusCode()) {
               return Optional.empty();
           }
        } catch (Exception e) {
            throw new RuntimeException("Cannot marshall response", e);
        }
        
        throw new RuntimeException("Cannot marshall responsecannot find doc, invalid response " + response);
    }

    /**
     * Delete a document.
     *          
     * @see https://docs.datastax.com/en/astra/docs/_attachments/docv2.html#operation/deleteDoc
     */
    public void delete() {
        Assert.hasLength(docId, "documentId");
        HttpResponse<String> response;
        try {
            response = getHttpClient().send(
                    startRequest(getCurrentDocEndpoint(), docClient.getToken())
                     .DELETE().build(), BodyHandlers.ofString());
            
        } catch (Exception e) {
            throw new RuntimeException("Cannot invoke API to delete a document:", e);
        }
        handleError(response);
        if (HttpURLConnection.HTTP_NO_CONTENT  != response.statusCode()) {
            throw new IllegalArgumentException("Invalid response from the API " + response.body());
        }
    }
    
    /**
     * Get a sub document by {document-path}.
     *
     * @param <SUBDOC>
     *      working class
     * @param className
     *      working class
     * @param path
     *      subpath in the doc/ 
     * 
     * @see https://docs.datastax.com/en/astra/docs/_attachments/docv2.html#operation/GetSubDocByPath
     * @return
     */
    public <SUBDOC> Optional<SUBDOC> findSubDocument(String path, Class<SUBDOC> className) {
        Assert.hasLength(docId, "documentId");
        Assert.hasLength(path, "hasLength");
        Assert.notNull(className, "expectedClass");
        if (!path.startsWith("/")) {
            path = "/" + path;
        }
        HttpResponse<String> response;
        try {
           response = getHttpClient().send(startRequest(getCurrentDocEndpoint() + path + "?raw=true", docClient.getToken())
                    .GET().build(), BodyHandlers.ofString());
        } catch (Exception e) {
            throw new RuntimeException("Cannot invoke API to find sub document:", e);
        }
        handleError(response);
        try {
            if (HttpURLConnection.HTTP_OK == response.statusCode()) {
                return Optional.of(getObjectMapper().readValue(response.body(), className));
            }
            return Optional.empty();
        } catch (Exception e) {
            throw new RuntimeException("An error occured", e);
        }
    }
    
    
    /**
     * Replace a subpart of the document.
     * 
     * @param <SUBDOC>
     *      working class
     * @param newValue
     *      object for the new value
     *      
     * @see https://docs.datastax.com/en/astra/docs/_attachments/docv2.html#operation/replaceSubDoc
     */
    public <SUBDOC> void replaceSubDocument(String path, SUBDOC newValue) {
        Assert.hasLength(path, "path");
        Assert.notNull(newValue, "newValue");
        if (!path.startsWith("/")) {
            path = "/" + path;
        }
        HttpResponse<String> response;
        try {
            response = getHttpClient().send(
                    startRequest(getCurrentDocEndpoint() + path, docClient.getToken())
                     .PUT(BodyPublishers.ofString(getObjectMapper().writeValueAsString(newValue))).build(), 
                    BodyHandlers.ofString());
        } catch (Exception e) {
            throw new RuntimeException("An error occured when updating sub documents", e);
        }
        handleError(response);
    }
    
    /**
     * Update part of a sub document
     * 
     * @param <SUBDOC>
     *      working class
     * @param newValue
     *      object for the new value
     *
     * @see https://docs.datastax.com/en/astra/docs/_attachments/docv2.html#operation/updatePartOfSubDoc
     */
    public <SUBDOC> void updateSubDocument(String path, SUBDOC newValue) {
        Assert.hasLength(path, "path");
        Assert.notNull(newValue, "newValue");
        if (!path.startsWith("/")) {
            path = "/" + path;
        }
        HttpResponse<String> response;
        try {
            response = getHttpClient().send(
                    startRequest(getCurrentDocEndpoint() + path + "?raw=true", docClient.getToken())
                     .method("PATCH", BodyPublishers.ofString(getObjectMapper().writeValueAsString(newValue))).build(), 
                    BodyHandlers.ofString());
        } catch (Exception e) {
            throw new RuntimeException("An error occured when updating sub documents", e);
        }
        handleError(response);
    }
    
    
    /**
     * Delete a sub document.
     * 
     * @param path
     *      sub document path
     *      
     * @path https://docs.datastax.com/en/astra/docs/_attachments/docv2.html#operation/deleteSubDoc
     */
    public void deleteSubDocument(String path) {
        Assert.hasLength(path, "path");
        if (!path.startsWith("/")) {
            path = "/" + path;
        }
        HttpResponse<String> response;
        try {
            response = getHttpClient().send(
                        startRequest(getCurrentDocEndpoint() + path + "?raw=true", docClient.getToken())
                            .DELETE().build(), BodyHandlers.ofString());
            
        } catch (Exception e) {
            throw new RuntimeException("An error occured when deleting sub documents", e);
        }
        handleError(response);
    }
    
    
    
}
