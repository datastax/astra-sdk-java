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

import java.net.HttpURLConnection;
import java.util.Map;
import java.util.Optional;

import com.datastax.stargate.sdk.core.ApiResponseHttp;
import com.datastax.stargate.sdk.utils.Assert;
import com.datastax.stargate.sdk.utils.HttpApisClient;

/**
 * Part of the Document API in stargate wrapper for methods at the document level.
 *
 * @author Cedrick LUNVEN (@clunven)
 */
public class DocumentClient {
    
    /** Wrapper handling header and error management as a singleton. */
    private final HttpApisClient http;
    
    /** Namespace. */
    private final CollectionClient collectionClient;
    
    /** Unique document identifer. */
    private final String docId;
    
    /**
     * Full constructor.
     * 
     * @param collectionClient CollectionClient
     * @param docId String
     */
    public DocumentClient(CollectionClient collectionClient, String docId) {
        this.collectionClient  = collectionClient;
        this.docId             = docId;
        this.http = HttpApisClient.getInstance();
    }
    
    /**
     * Leverage find() to check existence without eventual formatting issues. 
     * https://docs.datastax.com/en/astra/docs/_attachments/docv2.html#operation/getDocById
     * 
     * @return boolean
     */
    public boolean exist() {
        return HttpURLConnection.HTTP_OK == 
                http.GET(getEndPointDocument()).getCode();
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
        ApiResponseHttp res = http.PUT(getEndPointDocument(), marshall(doc));
        return marshallDocumentId(res.getBody());
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
        ApiResponseHttp res = http.PATCH(getEndPointDocument(), marshall(doc));
        return marshallDocumentId(res.getBody());
    }
    
    /**
     * Get a document by {document-id}. https://docs.datastax.com/en/astra/docs/_attachments/docv2.html#operation/getDocById
     *
     * @param <DOC> working class
     * @param clazz working class
     * @return a document if exist
     */
    public <DOC> Optional<DOC> find(Class<DOC> clazz) {
        Assert.notNull(clazz, "className");
        ApiResponseHttp res = http.GET(getEndPointDocument() + "?raw=true");
        if (HttpURLConnection.HTTP_OK == res.getCode()) {
           return Optional.of(marshallDocument(res.getBody(), clazz));
        }
        if (HttpURLConnection.HTTP_NOT_FOUND == res.getCode()) {
            return Optional.empty();
        }
        return Optional.empty();
    }

    /**
     * Delete a document. https://docs.datastax.com/en/astra/docs/_attachments/docv2.html#operation/deleteDoc
     */
    public void delete() {
        if (!exist()) {
            throw new IllegalArgumentException("Cannot delete " + docId + ", it does not exists");
        }
        http.DELETE(getEndPointDocument());
    }
    
    private String formatPath(String path) {
        Assert.hasLength(path, "hasLength");
        if (!path.startsWith("/")) {
            path = "/" + path;
        }
        return path;
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
        Assert.notNull(className, "expectedClass");
        ApiResponseHttp res = http.GET(getEndPointDocument() + formatPath(path) + "?raw=true");
        if (HttpURLConnection.HTTP_OK == res.getCode()) {
           return Optional.of(marshallDocument(res.getBody(), className));
        }
        if (HttpURLConnection.HTTP_NOT_FOUND == res.getCode()) {
            return Optional.empty();
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
        Assert.notNull(newValue, "newValue");
        http.PUT(getEndPointDocument() + formatPath(path), marshall(newValue));
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
        Assert.notNull(newValue, "newValue");
        http.PATCH(getEndPointDocument() + formatPath(path), marshall(newValue));
    }
    
    /**
     * Delete a sub document.
     * https://docs.datastax.com/en/astra/docs/_attachments/docv2.html#operation/deleteSubDoc
     * 
     * @param path sub document path
     */
    public void deleteSubDocument(String path) {
        http.DELETE(getEndPointDocument() + formatPath(path) + "?raw=true");
    }
    
    /**
     * marshallDocumentId
     * 
     * @param body String
     * @return String
     */
    private String marshallDocumentId(String body) {
        try {
            return (String) unmarshallBean(body, Map.class).get(CollectionClient.DOCUMENT_ID);
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
    @SuppressWarnings("unchecked")
    private <SUBDOC> SUBDOC marshallDocument(String body,  Class<SUBDOC> clazz) {
        try {
            if (clazz.equals(String.class)) {
               return (SUBDOC) body;
            }
            return unmarshallBean(body, clazz);
        } catch (Exception e) {
            throw new RuntimeException("Cannot marshal output '" + body + "' into class '"+ clazz +"'", e);
        }
    }
    
    /**
     * Build endpoint of the resource
     */
    private String getEndPointDocument() {
        return collectionClient.getEndPointCollection() + "/" + docId;
    }
    
}
