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

import com.datastax.stargate.sdk.StargateClientNode;
import com.datastax.stargate.sdk.ServiceClient;
import com.datastax.stargate.sdk.http.auth.domain.ApiResponseHttp;
import com.datastax.stargate.sdk.utils.Assert;
import com.datastax.stargate.sdk.utils.JsonUtils;

import java.net.HttpURLConnection;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import static com.datastax.stargate.sdk.utils.JsonUtils.marshall;
import static com.datastax.stargate.sdk.utils.JsonUtils.unmarshallBean;

/**
 * Part of the Document API in stargate wrapper for methods at the document level.
 *
 * @author Cedrick LUNVEN (@clunven)
 */
public class DocumentClient {
    
    /** Get Topology of the nodes. */
    private final ServiceClient stargateHttpClient;
    
    /** Namespace. */
    private CollectionClient collectionClient;
    
    /** Unique document identifer. */
    private String docId;
    
    /**
     * Full constructor.
     * 
     * @param stargateHttpClient stargateHttpClient
     * @param collectionClient CollectionClient
     * @param docId String
     */
    public DocumentClient(ServiceClient stargateHttpClient, CollectionClient collectionClient, String docId) {
        this.collectionClient   = collectionClient;
        this.docId              = docId;
        this.stargateHttpClient = stargateHttpClient;
    }
    
    /**
     * Leverage find() to check existence without eventual formatting issues. 
     * https://docs.datastax.com/en/astra/docs/_attachments/docv2.html#operation/getDocById
     * 
     * @return boolean
     */
    public boolean exist() {
        return HttpURLConnection.HTTP_OK == stargateHttpClient.GET(documentResource).getCode();
    }
    
    /**
     * Replace a document. https://docs.datastax.com/en/astra/docs/_attachments/docv2.html#operation/replaceDoc
     * 
     * @param <DOC> 
     *       working class
     * @param doc 
     *       object to be updated
     * @return
     *       the unique document identifier
     */
    public <DOC> String upsert(DOC doc) {
        Assert.notNull(doc, "document");
        ApiResponseHttp res = stargateHttpClient.PUT(documentResource, marshall(doc));
        return marshallDocumentId(res.getBody());
    }
    
    /**
     * Replace a document providing only the Json.
     *
     * @param json
     *      jon file.
     * @return
     *     the unique document identifier
     */
    public String upsert(String json) {
        Assert.hasLength(json, "Json document should not be null");
        ApiResponseHttp res = stargateHttpClient.PUT(documentResource, json);
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
        ApiResponseHttp res = stargateHttpClient.PATCH(documentResource, marshall(doc));
        return marshallDocumentId(res.getBody());
    }
    
    /**
     * Update a document providing only the Json.
     *
     * @param json
     *      jon file.
     * @return
     *     the unique document identifier
     */
    public String update(String json) {
        Assert.hasLength(json, "Json document should not be null");
        ApiResponseHttp res = stargateHttpClient.PATCH(documentResource, json);
        return marshallDocumentId(res.getBody());
    }
    
    /**
     * Get a document by id.
     *
     * @return
     *      the json payload if document exists.
     */
    public Optional<String> find() {
        ApiResponseHttp res = stargateHttpClient.GET(documentResource, "?raw=true");
        if (HttpURLConnection.HTTP_OK == res.getCode()) {
            return Optional.of(res.getBody());
         }
         return Optional.empty();
    }
    
    /**
     * Get a document by id.
     *
     * @param <DOC>
     *      nea
     * @param docm 
     *      document mapper
     * @return a document if exist     
     */
    public <DOC> Optional<DOC> find(DocumentMapper<DOC> docm) {
        Assert.notNull(docm, "documentMapper");
        Optional<String> f = find();
        if (f.isPresent()) {
            return f.map(docm::map);
        }
        return Optional.empty();
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
        Optional<String> f = find();
        if (f.isPresent()) {
            return f.map(b -> marshallDocument(b, clazz));
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
        stargateHttpClient.DELETE(documentResource);
    }
    
    /**
     * Add '/' if needed.
     * 
     * @param path
     *      current path
     * @return
     *      path with '/'
     */
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
        Assert.hasLength(path, "path");
        ApiResponseHttp res = stargateHttpClient.GET(documentResource, formatPath(path) + "?raw=true");
        if (HttpURLConnection.HTTP_OK == res.getCode()) {
           return Optional.of(marshallDocument(res.getBody(), className));
        }
        if (HttpURLConnection.HTTP_NOT_FOUND == res.getCode()) {
            return Optional.empty();
        }
        return Optional.empty();
    }
    
    /**
     * Retrieve a sub document with no marshalling
     * 
     * @param path
     *      path of the document
     * @return
     *      value as a String
     */
    public Optional<String> findSubDocument(String path) {
        Assert.hasLength(path, "path");
        ApiResponseHttp res = stargateHttpClient.GET(documentResource, formatPath(path) + "?raw=true");
        if (HttpURLConnection.HTTP_OK == res.getCode()) {
           return Optional.of(res.getBody());
        }
        if (HttpURLConnection.HTTP_NOT_FOUND == res.getCode()) {
            return Optional.empty();
        }
        return Optional.empty();
    }
    
    /**
     * Execute a function on a document.
     * 
     * @param path
     *      current document sub path
     * @param function
     *      function executed.
     * @param value
     *      value for attribute to update
     * @return
     *      attribute updated
     */
    @SuppressWarnings("unchecked")
    public String executefunction(String path, String function, Object value) {
        if (value instanceof String) { 
            value = "\"" + value + "\"";
        }
        ApiResponseHttp res = stargateHttpClient.POST(documentResource, 
                "{ \"operation\":\"" + function + "\",\"value\":" + JsonUtils.marshall(value) + "}", 
                formatPath(path) + "/function");
        // Parse result
        Map<String, Object> doc = unmarshallBean(res.getBody(), Map.class);
        if (doc.containsKey("data")) {
            return marshall(doc.get("data"));
        }
        return res.getBody();
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
        stargateHttpClient.PUT(documentResource, marshall(newValue), formatPath(path));
    }
    
    /**
     * Replace a subpart of the document.
     * 
     * @param path
     *      sub path
     * @param newValue
     *      new value for the path
     */
    public void replaceSubDocument(String path, String newValue) {
        Assert.hasLength(path, "path");
        Assert.hasLength(newValue, "newValue");
        stargateHttpClient.PUT(documentResource, newValue, formatPath(path));
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
        stargateHttpClient.PATCH(documentResource, marshall(newValue), formatPath(path));
    }
    
    /**
     * Update a subpart of the document.
     * 
     * @param path
     *      sub path
     * @param newValue
     *      new value for the path
     */
    public void updateSubDocument(String path, String newValue) {
        Assert.hasLength(path, "path");
        Assert.hasLength(newValue, "newValue");
        stargateHttpClient.PATCH(documentResource, newValue, formatPath(path));
    }
    
    /**
     * Delete a sub document.
     * https://docs.datastax.com/en/astra/docs/_attachments/docv2.html#operation/deleteSubDoc
     * 
     * @param path sub document path
     */
    public void deleteSubDocument(String path) {
        stargateHttpClient.DELETE(documentResource, formatPath(path) + "?raw=true");
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
    
    // ---------------------------------
    // ----       Resources         ----
    // ---------------------------------    
    
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
     * /v2/schemas/namespaces/{namespace}/collections/{collection}/{docId} 
     */
    public Function<StargateClientNode, String> documentResource = 
            (node) -> collectionClient.collectionResource.apply(node) +  "/" + docId;
       
}
