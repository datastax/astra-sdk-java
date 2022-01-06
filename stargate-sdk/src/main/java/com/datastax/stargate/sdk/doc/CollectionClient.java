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
import static com.datastax.stargate.sdk.utils.JsonUtils.unmarshallType;

import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.everit.json.schema.Schema;
import org.everit.json.schema.loader.SchemaLoader;
import org.json.JSONObject;

import com.datastax.stargate.sdk.StargateClientNode;
import com.datastax.stargate.sdk.StargateHttpClient;
import com.datastax.stargate.sdk.core.ApiResponse;
import com.datastax.stargate.sdk.core.ApiResponseHttp;
import com.datastax.stargate.sdk.core.Page;
import com.datastax.stargate.sdk.doc.domain.CollectionDefinition;
import com.datastax.stargate.sdk.doc.domain.PageableQuery;
import com.datastax.stargate.sdk.doc.domain.Query;
import com.datastax.stargate.sdk.utils.Assert;
import com.datastax.stargate.sdk.utils.JsonUtils;
import com.datastax.stargate.sdk.utils.Utils;
import com.fasterxml.jackson.core.type.TypeReference;

/**
 * Work on a dedicated collection without using the Pojo className.
 * 
 * @author Cedrick LUNVEN (@clunven)
 */
public class CollectionClient {
    
    /** Read document id. */
    public static final String DOCUMENT_ID = "documentId";
    
    /** Read document id. */
    public static final String BATCH_ID_PATH = "id-path";
    
    /** Get Topology of the nodes. */
    private final StargateHttpClient stargateHttpClient;
    
    /** Namespace. */
    protected NamespaceClient namespaceClient;
    
    /** Collection name. */
    protected String collectionName;
    
    /* Mapping type. */
    private static TypeReference<ApiResponse<Map<String, LinkedHashMap<?,?>>>> RESPONSE_SEARCH =  
            new TypeReference<ApiResponse<Map<String, LinkedHashMap<?,?>>>>(){};

    /**
     * Full constructor.
     *
     * @param stargateHttpClient http client
     * @param namespaceClient NamespaceClient
     * @param collectionName String
     */
    public CollectionClient(StargateHttpClient stargateHttpClient, NamespaceClient namespaceClient,  String collectionName) {
        this.namespaceClient    = namespaceClient;
        this.collectionName     = collectionName;
        this.stargateHttpClient = stargateHttpClient;
    }
    
    /**
     * Get metadata of the collection. There is no dedicated resources we
     * use the list and filter with what we need.
     * 
     * @see <a href="https://stargate.io/docs/stargate/1.0/attachments/docv2.html#operation/getCollection">Reference Documentation</a>
     *
     * @return
     *      metadata of the collection if its exist or empty
     */
    public Optional<CollectionDefinition> find() {
        return namespaceClient.collections()
                .filter(c -> collectionName.equalsIgnoreCase(c.getName()))
                .findFirst();
    }
    
    /**
     * Check if the collection exist.
     * 
     * @return if the collection exists.
     */
    public boolean exist() {
        return namespaceClient.collectionNames()
                .anyMatch(collectionName::equals);
    }
    
    /**
     * Create the collection.
     * 
     * @see <a href="https://stargate.io/docs/stargate/1.0/attachments/docv2.html#operation/createCollection">Reference Documentation</a>
     */
    public void create() {
        stargateHttpClient.POST(collectionResource, 
             "{\"name\":\"" + collectionName + "\"}");
    }
    
    /**
     * Upgrade the collection.
     * This is not relevant in ASTRA.
     */
    public void upgradeSAI() {
        upgrade(CollectionUpgradeType.SAI_INDEX_UPGRADE);
    }
    
    /**
     * Update a collection to SAI.
     * 
     * @param index
     *      collection SAI
     */
    public void upgrade(CollectionUpgradeType index) {
        stargateHttpClient.POST(collectionUpgradeResource, 
                "{\"upgradeType\":\"" + index.name() + "\"}");
    }
    
    /**
     * Deleting the collection
     * 
     * @see <a href="https://stargate.io/docs/stargate/1.0/attachments/docv2.html#operation/deleteCollectionSchema">Reference Documentation</a>
     */
    public void delete() {
        stargateHttpClient.DELETE(collectionResource);
    }
    
    /**
     * Create a new document from any serializable object. 
     * The doc could be a the String value.
     * 
     * @see <a href="https://stargate.io/docs/stargate/1.0/attachments/docv2.html#operation/addDoc">Reference Documentation</a>
     * 
     * @param <DOC>
     *          working bean type
     * @param doc
     *          working bean instance
     * @return
     *          created document id
     */
    public <DOC> String create(DOC doc) {
        ApiResponseHttp res = stargateHttpClient.POST(collectionResource, marshall(doc));
        try {
            return (String) unmarshallBean(res.getBody(), Map.class).get(DOCUMENT_ID);
        } catch (Exception e) {
            throw new RuntimeException("Cannot marshall document id", e);
        }
    }
    
    /**
     * Use the resource batch to insert massively in the DB.
     * 
     * @param records
     *      list of records
     * @param idPath
     *      id path to enforced ids
     * @param <DOC>
     *      working document
     * @return
     *      list of inserted ids
     */
    @SuppressWarnings("unchecked")
    public <DOC> List<String> batchInsert(List<DOC> records, String idPath) {
        Assert.notNull(records, "Records should be provided");
        ApiResponseHttp res;
        if (Utils.hasLength(idPath)) {
            res = stargateHttpClient.POST(collectionBatchResource, marshall(records), 
                    "?" + BATCH_ID_PATH + "=" + idPath);
        } else {
            res = stargateHttpClient.POST(collectionBatchResource, marshall(records));
        }
        Map<String, Object> doc = unmarshallBean(res.getBody(), Map.class);
        if (doc.containsKey("documentIds")) {
            return (List<String>) doc.get("documentIds");
        }
        return new ArrayList<>();
    }
    
    /**
     * Insert multiple record with a single resource.
     * 
     * @param records
     *      list of records
     * @param <DOC>
     *      working document
     * @return
     *      list of inserted ids
     */
    public <DOC > List<String> batchInsert(List<DOC> records) {
        return batchInsert(records, null);
    }

    /**
     * Count items in a collection, it can be slow as we iterate over pages limitating
     * payload and marshalling as much as possible.
     * 
     * @return
     *      number of record
     */
    public long count() {
        // limit fields to be retrieved to limit payload and read count
        return findAll(Query.builder().select("field_not_exist").build()).count();
    }
    
    // --- Find all --- 
    
    /**
     * This function will retrieve all documents in the Collection without any mapping.
     * 
     * <b>USE WITH CAUTION.</b> Default behaviour is using paging, here we are
     * fetching all pages until no more. 
     * 
     * @return
     *      all items in the the collection
     */
    public Stream<Document<String>> findAll() {
        return findAll(Query.builder().build());
    }
    
    /**
     * This function will retrieve all documents in the Collection providing a custom mapping logic.
     * 
     * <b>USE WITH CAUTION.</b> Default behaviour is using paging, here we are
     * fetching all pages until no more. 
     * 
     * @param <DOC>
     *      generic for working bean
     * @param documentMapper
     *      mapper from a record to the document bean
     * @return
     *      all items in the the collection
     */
    public <DOC> Stream<Document<DOC>> findAll(DocumentMapper<DOC> documentMapper) {
        return findAll(Query.builder().build(), documentMapper);
    }
    
    /**
     * This function will retrieve all documents in the Collection with automatic marshalling (jackson).
     * 
     * <b>USE WITH CAUTION.</b> Default behaviour is using paging, here we are
     * fetching all pages until no more. 
     * 
     * @param <DOC>
     *      generic for working bean
     * @param beanClass
     *      class for working bean 
     * @return
     *      all items in the the collection
     */
    public <DOC> Stream<Document<DOC>> findAll(Class<DOC> beanClass) {
        return findAll(Query.builder().build(), beanClass);
    }
    
    /**
     * Find all document matching the query.
     * 
     * <b>USE WITH CAUTION.</b> Default behaviour is using paging, here we are
     * fetching all pages until no more. 
     * 
     * @param query
     *          list of filters
     * @return
     *          all items matchin criteria
     */
    public Stream<Document<String>> findAll(Query query) {
        return findAll(query, (PageSupplier<String>) (cc, q) -> cc.findPage(q));
    }
    
    /**
     * Find all document matching the query.
     * 
     *  <b>USE WITH CAUTION.</b> Default behaviour is using paging, here we are
     * fetching all pages until no more. 
     * 
     * @param <DOC>
     *       generic for working bean
     * @param query
     *          list of filters
     * @param documentMapper
     *          custom mapping implementation
     * @return
     *          all items matchin criteria
     */
    public <DOC> Stream<Document<DOC>> findAll(Query query, DocumentMapper<DOC> documentMapper) {
        return findAll(query, (PageSupplier<DOC>) (cc, q) -> cc.findPage(q, documentMapper));
    }
    
    /**
     * Find all document matching the query.
     * 
     *  <b>USE WITH CAUTION.</b> Default behaviour is using paging, here we are
     * fetching all pages until no more. 
     * 
     * @param <DOC>
     *       generic for working bean
     * @param query
     *          list of filters
     * @param beanClass
     *          class for working bean  
     * @return
     *          all items matchin criteria
     */
    public <DOC> Stream<Document<DOC>> findAll(Query query, Class<DOC> beanClass) {
        return findAll(query, (PageSupplier<DOC>) (cc, q) -> cc.findPage(q, beanClass));
    }
    
    /**
     * Mutualization of searching on multiple pages to do findAll().
     * 
     * @param <DOC>
     *      current working class
     * @param query
     *      current query
     * @param pageLoader
     *      single page retrieve
     * @return
     */
    private <DOC> Stream<Document<DOC>> findAll(Query query, PageSupplier<DOC> pageLoader) {
        List<Document<DOC>> documents = new ArrayList<>();
        PageableQuery pageQuery = new PageableQuery(query);
        // Loop on pages up to no more pages (could be done)
        String pageState = null;
        do {
            Page<Document<DOC>> pageX = pageLoader.findPage(this, pageQuery);
            if (pageX.getPageState().isPresent())  {
                pageState = pageX.getPageState().get();
            } else {
                pageState = null;
            }
            documents.addAll(pageX.getResults());
            // Reuissing query for next page
            pageQuery.setPageState(pageState);
        } while(pageState != null);
        return documents.stream();
    }
    
    /**
     * find next page during a findAll
     *
     * @author Cedrick LUNVEN (@clunven)
     *
     * @param <DOC>
     *          list items.
     */
    public interface PageSupplier<DOC> {
        
        /**
         * Get a page
         * @param cc
         *      collection client
         * @param q
         *      query
         * @return
         *      page of results
         */
        Page<Document<DOC>> findPage(CollectionClient cc, PageableQuery q);
    }
    
    // --- Find Page --- 
    
    /**
     * Search for a page (without marshalling).
     * There is no filter either.
     *
     * @return
     *      a page of results
     */
    public Page<Document<String>> findPage() {
        return findPage(PageableQuery.builder().build());
    }
    
    /**
     * Find a page given some search (without marshalling the documents).
     * 
     * @param query
     *      query
     * @return
     *      output
     */
    public Page<Document<String>> findPage(PageableQuery query) {
        ApiResponse<Map<String, LinkedHashMap<?,?>>> searchResults = httpGetFindPage(query);
        if (null != searchResults && null != searchResults.getData()) {
            return new Page<Document<String>>(query.getPageSize(), 
                    searchResults.getPageState(), searchResults.getData()
                    .entrySet().stream()
                    .map(doc -> new Document<String>(doc.getKey(), JsonUtils.marshall(doc.getValue())))
                    .collect(Collectors.toList()));
        }
        // no data
        return new Page<Document<String>>();
    }
    
    
    /**
     * Default query, find first page.
     *
     * @param <DOC>
     *      working class
     * @param beanClass
     *      working bean
     * @return
     *      the page of records
     */
    public <DOC> Page<Document<DOC>> findPage(Class<DOC> beanClass) {
        return findPage(PageableQuery.builder().build(), beanClass);
    }
    
    /**
     * Find a page given some search.
     *
     * @param <DOC>
     *      generic for working bean
     * @param query
     *      filters for the query
     * @param beanClass
     *      class for working bean 
     * @return
     *      a page of results
     */
    public <DOC> Page<Document<DOC>> findPage(PageableQuery query, Class<DOC> beanClass) {
        ApiResponse<Map<String, LinkedHashMap<?,?>>> searchResults = httpGetFindPage(query);
        if (null != searchResults && null != searchResults.getData()) {
            return new Page<Document<DOC>>(query.getPageSize(), 
                    searchResults.getPageState(), searchResults.getData()
                    .entrySet().stream()
                    .map(doc -> new Document<DOC>(doc.getKey(), JsonUtils.getObjectMapper().convertValue(doc.getValue(), beanClass)))
                    .collect(Collectors.toList()));
        }
        // no data
        return new Page<Document<DOC>>();
    }
   
    /**
     * Find a page and marshall using a mapper.
     * @param <DOC>
     *      current bean
     * @param documentMapper
     *      document mapper
     * @return
     *      page of results
     */
    public <DOC> Page<Document<DOC>> findPage(DocumentMapper<DOC> documentMapper) {
        return findPage(PageableQuery.builder().build(), documentMapper);
    }
    
    /**
     * Find a page and marshall using a mapper.
     * @param <DOC>
     *      current bean
     * @param query
     *      current query
     * @param documentMapper
     *      document mapper
     * @return
     *      page of results
     */
    public <DOC> Page<Document<DOC>> findPage(PageableQuery query, DocumentMapper<DOC> documentMapper) {
        Page<Document<String>> raw = findPage(query);
        return new Page<Document<DOC>> (
                raw.getPageSize(), 
                raw.getPageState().orElse(null),
                raw.getResults().stream()
                   .map(doc -> new Document<DOC>(doc.getDocument(), documentMapper.map(doc.getDocument())))
                   .collect(Collectors.toList()));
    }
    
    /**
     * Technical invocation of the HTTP resources
     * @param query
     *      current query
     * @return
     *      http response
     */
    private ApiResponse<Map<String, LinkedHashMap<?,?>>> httpGetFindPage(PageableQuery query) {
        try {
            return unmarshallType(
                    stargateHttpClient
                        .GET(collectionResource, buildSuffixQueryUrl(query))
                        .getBody(), RESPONSE_SEARCH);
        } catch (Exception e) {
            throw new RuntimeException("Cannot marshall document results", e);
        }
    }
    
    /**
     * Return the JSON Schema if present.
     * 
     * @return
     *      the json schema if assign to the bean
     */
    public Optional<Schema> getSchema() {
        ApiResponseHttp res = stargateHttpClient.GET(collectionJsonSchemaResource);
        if (HttpURLConnection.HTTP_NOT_FOUND == res.getCode()) {
            return Optional.empty();
        }
        return Optional.ofNullable(SchemaLoader
                .builder()
                .schemaJson(new JSONObject(res.getBody())).build()
                .load().build());
    }
    
    /**
     * Assign a json Schema to a collection.
     *
     * @param jsonSchema
     *      target Schema as a json String
     */
    public void setSchema(String jsonSchema) {
       Assert.hasLength(jsonSchema, "jsonSchema");
       // Is a valid json schema ?
       SchemaLoader.builder()
                   .schemaJson(new JSONObject(jsonSchema))
                   .build().load().build();
       // Put the schema to 
       stargateHttpClient.PUT(collectionJsonSchemaResource, jsonSchema);
    }
    
    /**
     * Build the filters based on values in the query.
     *
     * @param query
     *      current query
     * @return
     *      the URL
     */
    private String buildSuffixQueryUrl(PageableQuery query) {
        try {
            StringBuilder sbUrl = new StringBuilder();
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
    
    // ---------------------------------
    // ----    Sub Resources        ----
    // ---------------------------------
    
    /**
     * Move to document Resource
     * 
     * @param docId String
     * @return DocumentClient
     */
    public DocumentClient document(String docId) {
        return new DocumentClient(stargateHttpClient, this, docId);
    }
    
    // ---------------------------------
    // ----       Resources         ----
    // ---------------------------------
    
    /**
     * Update Types.
     */
    public enum CollectionUpgradeType {
        
        /**
         * Move from SASI, secondary index to SAI.
         */
        SAI_INDEX_UPGRADE
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
    
    /** 
     * /v2/schemas/namespaces/{namespace}/collections/{collection} 
     */
    public Function<StargateClientNode, String> collectionResource = 
            (node) -> namespaceClient.collectionsResource.apply(node) +  "/" + collectionName;
    /** 
      * /v2/schemas/namespaces/{namespace}/collections/{collection}/upgrade?raw=true
      */
    public Function<StargateClientNode, String> collectionUpgradeResource = 
            (node) -> collectionResource.apply(node) +  "/upgrade?raw=true";

     /** 
      * /v2/schemas/namespaces/{namespace}/collections/{collection}/batch
      */
     public Function<StargateClientNode, String> collectionBatchResource = 
            (node) -> collectionResource.apply(node) +  "/batch";
                   
    /** 
      * /v2/schemas/namespaces/{namespace}/collections/{collection}/json-schema
      */
    public Function<StargateClientNode, String> collectionJsonSchemaResource = 
            (node) -> collectionResource.apply(node) +  "/json-schema";
            
}
