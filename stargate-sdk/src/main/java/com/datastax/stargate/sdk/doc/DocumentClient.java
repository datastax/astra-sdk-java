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

import java.net.HttpURLConnection;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.Map;
import java.util.Optional;

import com.datastax.stargate.sdk.utils.Assert;

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
     * 
     * @param docClient ApiDocumentClient
     * @param namespaceClient NamespaceClient
     * @param collectionClient CollectionClient
     * @param docId String
     */
    public DocumentClient(
            ApiDocumentClient docClient, NamespaceClient namespaceClient, 
            CollectionClient collectionClient, String docId) {
        this.docClient         = docClient;
        this.namespaceClient   = namespaceClient;
        this.collectionClient  = collectionClient;
        this.docId             = docId;
    }
    
    /**
     * Build endpoint of the resource
     */
    private String getEndpoint() {
        return docClient.getEndPointApiDocument()
                + PATH_NAMESPACES  + "/" + namespaceClient.getNamespace() 
                + PATH_COLLECTIONS + "/" + collectionClient.getCollectionName()
                + "/" + docId;
    }
    
    /**
     * Leverage find() to check existence without eventual formatting issues. 
     * https://docs.datastax.com/en/astra/docs/_attachments/docv2.html#operation/getDocById
     * 
     * @return boolean
     */
    public boolean exist() {
        Assert.hasLength(docId, "documentId");
        try {
            return HttpURLConnection.HTTP_OK == getHttpClient().send(
                    startRequest(getEndpoint(), docClient.getToken())
                      .GET().build(), BodyHandlers.discarding()).statusCode();
        } catch (Exception e) {
            throw new RuntimeException("Cannot test document existence", e);
        }
    }
    
    /**
     * Replace a document. https://docs.datastax.com/en/astra/docs/_attachments/docv2.html#operation/replaceDoc
     * 
     * @param <DOC> working class
     * @param doc DOC
     * @return DOC
     */
    public <DOC> String upsert(DOC doc) {
        Assert.notNull(doc, "document");
        Assert.hasLength(docId, "Document identifier");
        HttpResponse<String> response;
        try {
            String reqBody = getObjectMapper().writeValueAsString(doc);
           response = getHttpClient().send(
                   startRequest(getEndpoint(), docClient.getToken())
                   .PUT(BodyPublishers.ofString(reqBody)).build(),
                   BodyHandlers.ofString());
        } catch (Exception e) {
            throw new RuntimeException("Cannot save document:", e);
        }    
        
        handleError(response);
        return marshallDocumentId(response.body());
    }
  
    /**
     * Update part of a document. https://docs.datastax.com/en/astra/docs/_attachments/docv2.html#operation/updatePartOfDoc
     * 
     * @param <DOC> working class
     * @param doc working class
     * @return DOC
     */
    public <DOC> String update(DOC doc) {
        Assert.notNull(doc, "document");
        Assert.hasLength(docId, "Document identifier");
        HttpResponse<String> response;
        try {
           String reqBody = getObjectMapper().writeValueAsString(doc);
           response = getHttpClient().send(
                   startRequest(getEndpoint(), docClient.getToken()).method("PATCH", BodyPublishers.ofString(reqBody)).build(),
                   BodyHandlers.ofString());
        } catch (Exception e) {
            throw new RuntimeException("Cannot save document:", e);
        }
        handleError(response);
        return marshallDocumentId(response.body());
    }
    
    
    
    /**
     * Get a document by {document-id}. https://docs.datastax.com/en/astra/docs/_attachments/docv2.html#operation/getDocById
     *
     * @param <DOC> working class
     * @param clazz working class
     * @return a document if exist
     */
    public <DOC> Optional<DOC> find(Class<DOC> clazz) {
        Assert.hasLength(docId, "documentId");
        Assert.notNull(clazz, "className");
        HttpResponse<String> response;
        try {
            response = getHttpClient().send(
                    startRequest(getEndpoint() + "?raw=true", docClient.getToken())
                        .GET().build(), BodyHandlers.ofString());
        } catch (Exception e) {
            throw new RuntimeException("Cannot invoke API to find document:", e);
        }
        handleError(response);
        if (HttpURLConnection.HTTP_OK == response.statusCode()) {
           return Optional.of(marshallDocument(response.body(), clazz));
        }
        return Optional.empty();
    }

    /**
     * Delete a document. https://docs.datastax.com/en/astra/docs/_attachments/docv2.html#operation/deleteDoc
     *           
     */
    public void delete() {
        Assert.hasLength(docId, "documentId");
        if (!exist()) {
            throw new RuntimeException("Document '"+ docId + "' has not been found");
        }
        HttpResponse<String> response;
        try {
            response = getHttpClient().send(
                    startRequest(getEndpoint(), docClient.getToken())
                     .DELETE().build(), BodyHandlers.ofString());
            if (HttpURLConnection.HTTP_NO_CONTENT == response.statusCode()) {
                return;
            }
        } catch (Exception e) {
            throw new RuntimeException("Cannot invoke API to delete a document:", e);
        }
        handleError(response);
    }
    
    /**
     * Get a sub document by {document-path}.
     * https://docs.datastax.com/en/astra/docs/_attachments/docv2.html#operation/GetSubDocByPath
     *
     * @param <SUBDOC> working class
     * @param path subpath in the doc
     * @param className  working class
     * @return SUBDOC
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
           response = getHttpClient().send(startRequest(getEndpoint() + path + "?raw=true", docClient.getToken())
                    .GET().build(), BodyHandlers.ofString());
        } catch (Exception e) {
            throw new RuntimeException("Cannot invoke API to find sub document:", e);
        }
        handleError(response);
        if (HttpURLConnection.HTTP_OK == response.statusCode()) {
            return Optional.of(marshallDocument(response.body(), className));
        }
        return Optional.empty();
    }
    
    
    /**
     * Replace a subpart of the document.
     * https://docs.datastax.com/en/astra/docs/_attachments/docv2.html#operation/replaceSubDoc
     * 
     * @param <SUBDOC> working class
     * @param path subpath in the doc
     * @param newValue object for the new value
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
                    startRequest(getEndpoint() + path, docClient.getToken())
                     .PUT(BodyPublishers.ofString(getObjectMapper().writeValueAsString(newValue))).build(), 
                    BodyHandlers.ofString());
        } catch (Exception e) {
            throw new RuntimeException("An error occured when updating sub documents", e);
        }
        handleError(response);
    }
    
    /**
     * Update part of a sub document
     * https://docs.datastax.com/en/astra/docs/_attachments/docv2.html#operation/updatePartOfSubDoc
     * 
     * @param <SUBDOC> working class
     * @param path subpath in the doc
     * @param newValue object for the new value
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
                    startRequest(getEndpoint() + path + "?raw=true", docClient.getToken())
                     .method("PATCH", BodyPublishers.ofString(getObjectMapper().writeValueAsString(newValue))).build(), 
                    BodyHandlers.ofString());
        } catch (Exception e) {
            throw new RuntimeException("An error occured when updating sub documents", e);
        }
        handleError(response);
    }
    
    
    /**
     * Delete a sub document.
     * https://docs.datastax.com/en/astra/docs/_attachments/docv2.html#operation/deleteSubDoc
     * 
     * @param path sub document path
     */
    public void deleteSubDocument(String path) {
        Assert.hasLength(path, "path");
        if (!path.startsWith("/")) {
            path = "/" + path;
        }
        HttpResponse<String> response;
        try {
            response = getHttpClient().send(
                        startRequest(getEndpoint() + path + "?raw=true", docClient.getToken())
                            .DELETE().build(), BodyHandlers.ofString());
            
        } catch (Exception e) {
            throw new RuntimeException("An error occured when deleting sub documents", e);
        }
        handleError(response);
    }
    
    /**
     * marshallDocumentId
     * 
     * @param body String
     * @return String
     */
    private String marshallDocumentId(String body) {
        try {
            return (String) getObjectMapper()
                    .readValue(body, Map.class)
                    .get(CollectionClient.DOCUMENT_ID);
        } catch (Exception e) {
            throw new RuntimeException("Cannot marshall document after 'upsert'", e);
        }
    }
    
    /**
     * marshallDocument
     * 
     * @param <DOC> DOC
     * @param body String
     * @param clazz DOC
     * @return DOC
     */
    private <SUBDOC> SUBDOC marshallDocument(String body,  Class<SUBDOC> clazz) {
        try {
            return getObjectMapper().readValue(body, clazz);
        } catch (Exception e) {
            throw new RuntimeException("Cannot marshal output '" + body + "' into class '"+ clazz +"'", e);
        }
    }
}
