package com.dtsx.astra.sdk;

import io.stargate.sdk.core.domain.Page;
import io.stargate.sdk.data.CollectionClient;
import io.stargate.sdk.data.domain.DocumentMutationResult;
import io.stargate.sdk.data.domain.JsonDocument;
import io.stargate.sdk.data.domain.JsonDocumentMutationResult;
import io.stargate.sdk.data.domain.JsonDocumentResult;
import io.stargate.sdk.data.domain.JsonResultUpdate;
import io.stargate.sdk.data.domain.UpdateStatus;
import io.stargate.sdk.data.domain.odm.Document;
import io.stargate.sdk.data.domain.odm.DocumentResult;
import io.stargate.sdk.data.domain.odm.DocumentResultMapper;
import io.stargate.sdk.data.domain.query.DeleteQuery;
import io.stargate.sdk.data.domain.query.DeleteResult;
import io.stargate.sdk.data.domain.query.Filter;
import io.stargate.sdk.data.domain.query.SelectQuery;
import io.stargate.sdk.data.domain.query.SelectQueryBuilder;
import io.stargate.sdk.data.domain.query.UpdateQuery;
import lombok.Getter;
import lombok.NonNull;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

/**
 * Client for AstraDB at collection level (crud for documents).
 */
@Getter
public class AstraDBCollection {

    /**
     * Internal Stargate data api client.
     */
    CollectionClient collectionClient;

    /**
     * Constructor with the internal client
     *
     * @param collectionClient
     *      collection description
     */
    AstraDBCollection(CollectionClient collectionClient) {
        this.collectionClient = collectionClient;
    }

    // --------------------------
    // ---   Insert One      ----
    // --------------------------

    /**
     * Insert a single document from a Json String.
     *
     * @param json
     *      json String
     * @return
     *      mutation result with status and id
     */
    public final JsonDocumentMutationResult insertOne(String json) {
        return collectionClient.insertOne(json);
    }

    /**
     * Insert a single document from a Json String, asynchronously.
     *
     * @param json
     *      json Strings
     * @return
     *      mutation result with status and id
     */
    public final CompletableFuture<JsonDocumentMutationResult> insertOneAsync(String json) {
        return collectionClient.insertOneAsync(json);
    }

    /**
     * Insert a single document from a Json Document (schemaless).
     *
     * @param document
     *      current bean
     * @return
     *      mutation result with status and id
     */
    public final JsonDocumentMutationResult insertOne(JsonDocument document) {
        return collectionClient.insertOne(document);
    }

    /**
     * Insert a single document from a Json Document (schemaless), asynchronously.
     *
     * @param document
     *      current bean
     * @return
     *      mutation result with status and id
     */
    public final CompletableFuture<JsonDocumentMutationResult> insertOneASync(JsonDocument document) {
        return collectionClient.insertOneAsync(document);
    }

    /**
     * Insert a single document from a Document&lt;T&gt; object (SchemaFull)
     *
     * @param document
     *      current bean
     * @param <DOC>
     *     payload of document
     * @return
     *      mutation result with status and id
     */
    public final <DOC> DocumentMutationResult<DOC> insertOne(Document<DOC> document) {
        return collectionClient.insertOne(document);
    }

    /**
     * Insert a single document from a Document&lt;T&gt; object (SchemaFull), asynchronously.
     *
     * @param document
     *      current bean
     * @param <DOC>
     *     payload of document
     * @return
     *      mutation result with status and id
     */
    public final <DOC> CompletableFuture<DocumentMutationResult<DOC>> insertOneASync(Document<DOC> document) {
        return collectionClient.insertOneASync(document);
    }

    // --------------------------
    // ---   Upsert One      ----
    // --------------------------

    /**
     * Upsert a single document from a Json String.
     *
     * @param json
     *      json Strings
     * @return
     *      mutation result with status and id
     */
    public final JsonDocumentMutationResult upsertOne(String json) {
        return collectionClient.upsertOne(json);
    }

    /**
     * Upsert a single document from a Json String, asynchronously.
     *
     * @param json
     *      json Strings
     * @return
     *      mutation result with status and id
     */
    public final CompletableFuture<JsonDocumentMutationResult> upsertOneASync(String json) {
        return collectionClient.upsertOneAsync(json);
    }

    /**
     * Upsert a single document from a Json Document (schemaless).
     *
     * @param document
     *      current document
     * @return
     *      document id
     */
    public final JsonDocumentMutationResult upsertOne(JsonDocument document) {
        return collectionClient.upsertOne(document);
    }

    /**
     * Upsert a single document from a Json Document (schemaless), asynchronously.
     *
     * @param document
     *      current document
     * @return
     *      document id
     */
    public final CompletableFuture<JsonDocumentMutationResult> upsertOneASync(JsonDocument document) {
        return CompletableFuture.supplyAsync(() -> collectionClient.upsertOne(document));
    }

    /**
     * Upsert a single document from a Document&lt;T&gt; object (SchemaFull)
     *
     * @param document
     *      current document
     * @param <DOC>
     *     payload of document
     * @return
     *      document id
     */
    public final <DOC> DocumentMutationResult<DOC> upsertOne(Document<DOC> document) {
        return collectionClient.upsertOne(document);
    }

    /**
     * Upsert a single document from a Document&lt;T&gt; object (SchemaFull), asynchronously
     *
     * @param document
     *      current document
     * @param <DOC>
     *     payload of document
     * @return
     *      document id
     */
    public final <DOC> CompletableFuture<DocumentMutationResult<DOC>> upsertOneASync(Document<DOC> document) {
        return collectionClient.upsertOneASync(document);
    }

    // --------------------------
    // ---   Insert Many     ----
    // --------------------------

    /**
     * Insert multiple documents (up to 20) as a Json String , for more document check <code>insertManyChunked</code>.
     *
     * @param json
     *      json String
     * @return
     *      for each document its id and insertion status
     */
    public final List<JsonDocumentMutationResult> insertMany(String json) {
        return collectionClient.insertMany(json);
    }

    /**
     * Insert multiple documents (up to 20) as a Json String , for more document check <code>insertManyChunked</code>, Asynchronously.
     *
     * @param json
     *      json String
     * @return
     *      for each document its id and insertion status
     */
    public final CompletableFuture<List<JsonDocumentMutationResult>> insertManyASync(String json) {
        return collectionClient.insertManyASync(json);
    }

    /**
     * Insert multiple documents (up to 20) as <code>JsonDocument</code> List , for more document check <code>insertManyChunked</code>.
     *
     * @param documents
     *      list of documents
     * @return
     *      for each document its id and insertion status
     */
    public final List<JsonDocumentMutationResult> insertManyJsonDocuments(List<JsonDocument> documents) {
        return collectionClient.insertManyJsonDocuments(documents);
    }

    /**
     * Insert multiple documents (up to 20) as <code>JsonDocument</code> List , for more document check <code>insertManyChunked</code>, asynchronously.
     *
     * @param documents
     *      list of documents
     * @return
     *      for each document its id and insertion status
     */
    public final CompletableFuture<List<JsonDocumentMutationResult>> insertManyJsonDocumentsASync(List<JsonDocument> documents) {
        return collectionClient.insertManyJsonDocumentsASync(documents);
    }

    /**
     * Insert multiple documents (up to 20) as generic <code>Document&lt;T&gt;</code> List , for more document check <code>insertManyChunked</code>.
     *
     * @param documents
     *      list of documents
     * @param <DOC>
     *     payload of document
     * @return
     *      list of ids
     */
    public final <DOC> List<DocumentMutationResult<DOC>> insertMany(List<Document<DOC>> documents) {
        return collectionClient.insertMany(documents);
    }

    /**
     * Insert multiple documents (up to 20) as generic <code>Document&lt;T&gt;</code> List , for more document check <code>insertManyChunked</code>, asynchronously.
     *
     * @param documents
     *      list of documents
     * @param <DOC>
     *     payload of document
     * @return
     *      list of ids
     */
    public final <DOC> CompletableFuture<List<DocumentMutationResult<DOC>>> insertManyASync(List<Document<DOC>> documents) {
        return collectionClient.insertManyASync(documents);
    }

    // -------------------------------
    // ---   Insert Many Chunked  ----
    // -------------------------------

    /**
     * Insert large number of documents as generic <code>Document&lt;T&gt;</code> List. Insertions a redistributed in chunks and chunk processing are parallelized.
     *
     * @param documents
     *      list of documents
     * @param chunkSize
     *      number of documents in one chunk (up to 20)
     * @param concurrency
     *      number of chunks processed in parallel (depends on document size)
     * @param <DOC>
     *     payload for the documents
     * @return
     *      list of ids
     */
    public final <DOC> List<DocumentMutationResult<DOC>> insertManyChunked(List<Document<DOC>> documents, int chunkSize, int concurrency) {
        return collectionClient.insertManyChunked(documents, chunkSize, concurrency);
    }

    /**
     * Insert large number of documents as generic <code>Document&lt;T&gt;</code> List. Insertions a redistributed in chunks and chunk processing are parallelized., Asynchronously
     *
     * @param documents
     *      list of documents
     * @param chunkSize
     *      size of the block
     * @param concurrency
     *      number of blocks in parallel
     * @param <DOC>
     *     represent the pojo, payload of document
     * @return
     *      list of ids
     */
    public final <DOC> CompletableFuture<List<DocumentMutationResult<DOC>>> insertManyChunkedASync(List<Document<DOC>> documents, int chunkSize, int concurrency) {
        return CompletableFuture.supplyAsync(() -> insertManyChunked(documents, chunkSize, concurrency));
    }

    /**
     * Insert large number of documents as <code>JsonDocument</code> List. Insertions a redistributed in chunks and chunk processing are parallelized.,
     *
     * @param documents
     *      list of documents
     * @param chunkSize
     *      size of the block
     * @param concurrency
     *      number of blocks in parallel
     * @return
     *      list of mutation status
     */
    public final List<JsonDocumentMutationResult> insertManyChunkedJsonDocuments(List<JsonDocument> documents, int chunkSize, int concurrency) {
        return collectionClient.insertManyJsonDocumentsChunked(documents, chunkSize, concurrency);
    }

    /**
     * Insert large number of documents as <code>JsonDocument</code> List. Insertions a redistributed in chunks and chunk processing are parallelized., Asynchronously
     *
     * @param documents
     *      list of documents
     * @param chunkSize
     *      size of the block
     * @param concurrency
     *      number of blocks in parallel
     * @return
     *      list of mutation status
     */
    public final CompletableFuture<List<JsonDocumentMutationResult>> insertManyChunkedJsonDocumentsAsync(List<JsonDocument> documents, int chunkSize, int concurrency) {
        return CompletableFuture.supplyAsync(() -> insertManyChunkedJsonDocuments(documents, chunkSize, concurrency));
    }

    // ------------------------------
    // ---      Upsert Many      ----
    // ------------------------------

    /**
     * Upsert any items in the collection.
     *
     * @param json
     *      json String
     * @return
     *      list of statuses
     */
    public final List<JsonDocumentMutationResult> upsertMany(String json) {
        return collectionClient.upsertMany(json);
    }

    /**
     * Upsert any items in the collection.
     *
     * @param json
     *      json String
     * @return
     *      list of statuses
     */
    public final CompletableFuture<List<JsonDocumentMutationResult>> upsertManyASync(String json) {
        return collectionClient.upsertManyASync(json);
    }

    /**
     * Upsert any items in the collection.
     *
     * @param documents
     *      current collection list
     * @return
     *      list of statuses
     */
    public final List<JsonDocumentMutationResult> upsertManyJsonDocuments(List<JsonDocument> documents) {
        return collectionClient.upsertManyJsonDocuments(documents);
    }

    /**
     * Upsert any items in the collection.
     *
     * @param documents
     *      current collection list
     * @return
     *      list of statuses
     */
    public final CompletableFuture<List<JsonDocumentMutationResult>> upsertManyJsonDocumentsASync(List<JsonDocument> documents) {
        return collectionClient.upsertManyJsonDocumentsASync(documents);
    }

    /**
     * Upsert any items in the collection.
     * @param documents
     *      current collection list
     * @return
     *      list of statuses
     * @param <DOC>
     *     represent the pojo, payload of document
     */
    public final <DOC> List<DocumentMutationResult<DOC>> upsertMany(List<Document<DOC>> documents) {
        return collectionClient.upsertMany(documents);
    }

    /**
     * Upsert any items in the collection asynchronously.
     *
     * @param documents
     *      current collection list
     * @return
     *      list of statuses
     * @param <DOC>
     *     represent the pojo, payload of document
     */
    public final <DOC> CompletableFuture<List<DocumentMutationResult<DOC>>> upsertManyASync(List<Document<DOC>> documents) {
        return collectionClient.upsertManyASync(documents);
    }

    // --------------------------------
    // ---   Upsert Many  Chunked  ----
    // --------------------------------

    /**
     * Low level insertion of multiple records
     *
     * @param documents
     *      list of documents
     * @param chunkSize
     *      size of the block
     * @param concurrency
     *      concurrency
     * @param <DOC>
     *     represent the pojo, payload of document
     * @return
     *      list of ids
     */
    public final <DOC> List<DocumentMutationResult<DOC>> upsertManyChunked(List<Document<DOC>> documents, int chunkSize, int concurrency) {
        return collectionClient.upsertManyChunked(documents, chunkSize, concurrency);
    }

    /**
     * Low level insertion of multiple records
     *
     * @param documents
     *      list of documents
     * @param chunkSize
     *      size of the block
     * @param concurrency
     *      concurrency
     * @param <DOC>
     *     represent the pojo, payload of document
     * @return
     *      list of ids
     */
    public final <DOC> CompletableFuture<List<DocumentMutationResult<DOC>>> upsertManyChunkedASync(List<Document<DOC>> documents, int chunkSize, int concurrency) {
        return collectionClient.upsertManyChunkedASync(documents, chunkSize, concurrency);
    }

    /**
     * Low level insertion of multiple records
     *
     * @param documents
     *      list of documents
     * @param chunkSize
     *      size of the block
     * @param concurrency
     *      concurrency
     * @return
     *      list of ids
     */
    public final List<JsonDocumentMutationResult> upsertManyJsonDocumentsChunked(List<JsonDocument> documents, int chunkSize, int concurrency) {
        return collectionClient.upsertManyJsonDocumentsChunked(documents, chunkSize, concurrency);
    }

    /**
     * Low level insertion of multiple records
     *
     * @param documents
     *      list of documents
     * @param chunkSize
     *      size of the block
     * @param concurrency
     *      concurrency
     * @return
     *      list of ids
     */
    public final CompletableFuture<List<JsonDocumentMutationResult>> upsertManyJsonDocumentsChunkedAsync(List<JsonDocument> documents, int chunkSize, int concurrency) {
        return collectionClient.upsertManyJsonDocumentsChunkedASync(documents, chunkSize, concurrency);
    }

    // --------------------------
    // ---      Count        ----
    // --------------------------

    /**
     * Count Document request.
     *
     * @return
     *      number of document.
     */
    public Integer countDocuments() {
        return collectionClient.countDocuments();
    }

    /**
     * Count Document request.
     *
     * @param jsonFilter
     *      request to filter for count
     * @return
     *      number of document.
     */
    public Integer countDocuments(Filter jsonFilter) {
        return collectionClient.countDocuments(jsonFilter);
    }

    // --------------------------
    // ---     Find One      ----
    // --------------------------

    /**
     * Check existence of a document from its id.
     * Projection to make it as light as possible.
     *
     * @param id
     *      document identifier
     * @return
     *      existence status
     */
    public boolean isDocumentExists(String id) {
        return collectionClient.isDocumentExists(id);
    }

    /**
     * Find one document matching the query.
     *
     * @param rawJsonQuery
     *      query documents and vector
     * @return
     *      result if exists
     */
    public Optional<JsonDocumentResult> findOne(String rawJsonQuery) {
        return collectionClient.findOne(rawJsonQuery);
    }

    /**
     * Find one document matching the query.
     *
     * @param query
     *      query documents and vector
     * @return
     *      result if exists
     */
    public Optional<JsonDocumentResult> findOne(SelectQuery query) {
        return collectionClient.findOne(query);
    }

    /**
     * Find one document matching the query.
     *
     * @param query
     *      query documents and vector
     * @param clazz
     *     class of the document
     * @return
     *      result if exists
     * @param <DOC>
     *       class to be marshalled
     */
    public <DOC> Optional<DocumentResult<DOC>> findOne(SelectQuery query, Class<DOC> clazz) {
        return findOne(query).map(r -> new DocumentResult<>(r, clazz));
    }

    /**
     * Find one document matching the query.
     *
     * @param query
     *      query documents and vector
     * @param clazz
     *     class of the document
     * @return
     *      result if exists
     * @param <DOC>
     *       class to be marshalled
     */
    public <DOC> Optional<DocumentResult<DOC>> findOne(String query, Class<DOC> clazz) {
        return findOne(query).map(r -> new DocumentResult<>(r, clazz));
    }

    /**
     * Find one document matching the query.
     *
     * @param query
     *      query documents and vector
     * @param mapper
     *      convert a json into expected pojo
     * @return
     *      result if exists
     * @param <DOC>
     *       class to be marshalled
     */
    public <DOC> Optional<DocumentResult<DOC>> findOne(SelectQuery query, DocumentResultMapper<DOC> mapper) {
        return findOne(query).map(mapper::map);
    }


    /**
     * Find one document matching the query.
     *
     * @param query
     *      query documents and vector
     * @param mapper
     *      convert a json into expected pojo
     * @return
     *      result if exists
     * @param <DOC>
     *       class to be marshalled
     */
    public <DOC> Optional<DocumentResult<DOC>> findOne(String query, DocumentResultMapper<DOC> mapper) {
        return findOne(query).map(mapper::map);
    }

    // --------------------------
    // ---    Find By Id     ----
    // --------------------------

    /**
     * Find document from its id.
     *
     * @param id
     *      document identifier
     * @return
     *      document
     */
    public Optional<JsonDocumentResult> findById(String id) {
        return findOne(SelectQuery.findById(id));
    }

    /**
     * Find document from its id.
     *
     * @param id
     *      document identifier
     * @param clazz
     *      class for target pojo
     * @return
     *      document
     * @param <DOC>
     *       class to be marshalled
     */
    public <DOC> Optional<DocumentResult<DOC>> findById(@NonNull String id, Class<DOC> clazz) {
        return findById(id).map(r -> new DocumentResult<>(r, clazz));
    }

    /**
     * Find document from its id.
     *
     * @param id
     *      document identifier
     * @param mapper
     *      convert a json into expected pojo
     * @return
     *      document
     * @param <DOC>
     *       class to be marshalled
     */
    public <DOC> Optional<DocumentResult<DOC>> findById(@NonNull String id, DocumentResultMapper<DOC> mapper) {
        return findById(id).map(mapper::map);
    }

    // --------------------------
    // --- Find By Vector    ----
    // --------------------------

    /**
     * Find document from its vector.
     *
     * @param vector
     *      document vector
     * @return
     *      document
     */
    public Optional<JsonDocumentResult> findOneByVector(float[] vector) {
        return findOne(SelectQuery.findByVector(vector));
    }

    /**
     * Find document from its vector.
     *
     * @param vector
     *      document vector
     * @param clazz
     *      class for target pojo
     * @return
     *      document
     * @param <DOC>
     *       class to be marshalled
     */
    public <DOC> Optional<DocumentResult<DOC>> findOneByVector(float[] vector, Class<DOC> clazz) {
        return findOneByVector(vector).map(r -> new DocumentResult<>(r, clazz));
    }

    /**
     * Find document from its vector.
     *
     * @param vector
     *      document vector
     * @param mapper
     *      convert a json into expected pojo
     * @return
     *      document
     * @param <DOC>
     *       class to be marshalled
     */
    public <DOC> Optional<DocumentResult<DOC>> findOneByVector(float[] vector, DocumentResultMapper<DOC> mapper) {
        return findOneByVector(vector).map(mapper::map);
    }

    // --------------------------
    // ---       Find        ----
    // --------------------------

    /**
     * Search records with a filter
     *
     * @param query
     *      filter
     * @return
     *      all items
     */
    public Stream<JsonDocumentResult> find(SelectQuery query) {
        return collectionClient.find(query);
    }

    /**
     * Find documents matching the pagedQuery.
     *
     * @param pagedQuery
     *      current pagedQuery
     * @return
     *      page of results
     */
    public Page<JsonDocumentResult> findPage(SelectQuery pagedQuery) {
        return collectionClient.findPage(pagedQuery);
    }

    /**
     * Find documents matching the pagedQuery.
     *
     * @param pagedQuery
     *      current pagedQuery
     * @return
     *      page of results
     */
    public Page<JsonDocumentResult> findPage(String pagedQuery) {
        return collectionClient.findPage(pagedQuery);
    }

    /**
     * Search records with a filter
     *
     * @param query
     *      filter
     * @param clazz
     *      class for target pojo
     * @return
     *      all items
     * @param <DOC>
     *       class to be marshalled
     */
    public  <DOC> Stream<DocumentResult<DOC>> find(SelectQuery query, Class<DOC> clazz) {
        return collectionClient.find(query, clazz);
    }

    /**
     * Search records with a filter
     *
     * @param query
     *      filter
     * @param mapper
     *      convert a json into expected pojo
     * @return
     *      all items
     * @param <DOC>
     *       class to be marshalled
     */
    public  <DOC> Stream<DocumentResult<DOC>> find(SelectQuery query, DocumentResultMapper<DOC> mapper) {
        return collectionClient.find(query, mapper);
    }

    /**
     * Get all items in a collection.
     *
     * @return
     *      all items
     */
    public Stream<JsonDocumentResult> findAll() {
        return collectionClient.findAll();
    }

    /**
     * Find All with Object Mapping.
     *
     * @param clazz
     *      class to be used
     * @param <DOC>
     *       class to be marshalled
     * @return
     *      stream of results
     */
    public <DOC> Stream<DocumentResult<DOC>> findAll(Class<DOC> clazz) {
        return collectionClient.findAll(clazz);
    }

    /**
     * Find All with Object Mapping.
     *
     * @param mapper
     *      convert a json into expected pojo
     * @param <DOC>
     *       class to be marshalled
     * @return
     *      stream of results
     */
    public <DOC> Stream<DocumentResult<DOC>> findAll(DocumentResultMapper<DOC> mapper) {
        return collectionClient.findAll(mapper);
    }

    /**
     * Find documents matching the query.
     *
     * @param query
     *      current query
     * @param clazz
     *      class for target pojo
     * @return
     *      page of results
     * @param <T>
     *     class to be marshalled
     */
    public <T> Page<DocumentResult<T>> findPage(SelectQuery query, Class<T> clazz) {
        return collectionClient.findPage(query, clazz);
    }

    // --------------------------
    // ---     Delete One    ----
    // --------------------------

    /**
     * Delete single record from a request.
     *
     * @param deleteQuery
     *      delete query
     * @return
     *      number of deleted records
     */
    public DeleteResult deleteOne(DeleteQuery deleteQuery) {
        return collectionClient.deleteOne(deleteQuery);
    }

    /**
     * Delete single record from its id.
     *
     * @param id
     *      id
     * @return
     *      number of deleted records
     */
    public DeleteResult deleteById(String id) {
        return deleteOne(DeleteQuery.deleteById(id));
    }

    /**
     * Delete single record from its vector.
     *
     * @param vector
     *      vector
     * @return
     *      number of deleted records
     */
    public DeleteResult deleteByVector(float[] vector) {
        return deleteOne(DeleteQuery.deleteByVector(vector));
    }

    // --------------------------
    // ---     Delete Many   ----
    // --------------------------

    /**
     * Delete multiple records from a request.
     *
     * @param deleteQuery
     *      delete query
     * @return
     *      number of deleted records
     */
    public DeleteResult deleteMany(DeleteQuery deleteQuery) {
        return collectionClient.deleteMany(deleteQuery);
    }

    /**
     * Delete multiple records from a request.
     *
     * @param deleteQuery
     *      delete query
     * @return
     *      number of deleted records
     */
    public DeleteResult deleteManyPaged(DeleteQuery deleteQuery) {
        return collectionClient.deleteManyPaged(deleteQuery);
    }

    /**
     * Delete multiple records from a request.
     *
     * @param deleteQuery
     *      delete query
     * @param concurrency
     *      how many threads in parallel
     * @return
     *      number of deleted records
     */
    public DeleteResult deleteManyChunked(DeleteQuery deleteQuery, int concurrency) {
        return collectionClient.deleteManyChunked(deleteQuery, concurrency);
    }

    /**
     * Clear the collection.
     *
     * @return
     *      number of items deleted
     */
    public DeleteResult deleteAll() {
        return deleteMany(null);
    }

    // --------------------------
    // ---  Update           ----
    // --------------------------

    /**
     * Find ana update a record based on a query,
     *
     * @param query
     *      query to find the record
     * @return
     *      result of the update
     */
    public JsonResultUpdate findOneAndUpdate(UpdateQuery query) {
        return collectionClient.findOneAndDelete(query);
    }

    /**
     * Find ana replace a record based on a query,
     *
     * @param query
     *      query to find the record
     * @return
     *      result of the update
     */
    public JsonResultUpdate findOneAndReplace(UpdateQuery query) {
        return collectionClient.findOneAndReplace(query);
    }

    /**
     * Find ana delete a record based on a query.
     *
     * @param query
     *      query to find the record
     * @return
     *      result of the update
     */
    public JsonResultUpdate findOneAndDelete(UpdateQuery query) {
        return collectionClient.findOneAndDelete(query);
    }

    // --------------------------
    // ---  UpdateOne        ----
    // --------------------------

    /**
     * Update a single record.
     *
     * @param query
     *      query to find the record
     * @return
     *      update status
     */
    public UpdateStatus updateOne(UpdateQuery query) {
        return collectionClient.updateOne(query);
    }

    // --------------------------
    // ---    UpdateMany     ----
    // --------------------------

    /**
     * Update many records.
     *
     * @param query
     *      query to find the record
     * @return
     *      update status
     */
    public UpdateStatus updateMany(UpdateQuery query) {
        return collectionClient.updateMany(query);
    }

    // ------------------------------
    // ---  Semantic Search      ----
    // ------------------------------

    /**
     * Query builder.
     *
     * @param vector
     *      vector embeddings
     * @param limit
     *      limit for output
     * @return
     *      result page
     */
    public Stream<JsonDocumentResult> findVector(float[] vector, Integer limit) {
        return findVector(vector, null, limit);
    }

    /**
     * Query builder.
     *
     * @param vector
     *      vector embeddings
     * @param filter
     *      metadata filter
     * @param limit
     *      limit for output
     * @return
     *      result page
     */
    public Stream<JsonDocumentResult> findVector(float[] vector, Filter filter, Integer limit) {
        return find(SelectQuery.builder()
                .filter(filter)
                .orderByAnn(vector)
                .withLimit(limit)
                .includeSimilarity()
                .build());
    }


    /**
     * find Page.
     *
     * @param query
     *      return query Page
     * @return
     *      page of results
     */
    public Page<JsonDocumentResult> findVectorPage(SelectQuery query) {
        return findPage(query);
    }

    /**
     * Query builder.
     *
     * @param vector
     *      vector embeddings
     * @param filter
     *      metadata filter
     * @param limit
     *      limit
     * @param pagingState
     *      paging state
     * @return
     *      result page
     */
    public Page<JsonDocumentResult> findVectorPage(float[] vector, Filter filter, Integer limit, String pagingState) {
        return findVectorPage(SelectQuery.builder()
                .filter(filter)
                .orderByAnn(vector)
                .withLimit(limit)
                .withPagingState(pagingState)
                .includeSimilarity()
                .build());
    }

    /**
     * Search similarity from the vector (page by 20)
     *
     * @param vector
     *      vector embeddings
     * @param filter
     *      metadata filter
     * @param limit
     *      limit
     * @param pagingState
     *      paging state
     * @param clazz
     *      current class.
     * @param <DOC>
     *       type of document
     * @return
     *      page of results
     */
    public <DOC> Page<DocumentResult<DOC>> findVectorPage(float[] vector, Filter filter, Integer limit, String pagingState, Class<DOC> clazz) {
        return collectionClient.findVectorPage(vector, filter, limit, pagingState, clazz);
    }

    /**
     * Search similarity from the vector (page by 20)
     *
     * @param vector
     *      vector embeddings
     * @param filter
     *      metadata filter
     * @param limit
     *      limit
     * @param pagingState
     *      paging state
     * @param mapper
     *      result mapper
     * @param <DOC>
     *       type of document
     * @return
     *      page of results
     */
    public <DOC> Page<DocumentResult<DOC>> findVectorPage(float[] vector, Filter filter, Integer limit, String pagingState, DocumentResultMapper<DOC> mapper) {
        return collectionClient.findVectorPage(vector, filter, limit, pagingState, mapper);
    }

    /**
     * Retrieve documents from a vector.
     *
     * @param vector
     *      vector list
     * @return
     *      the list of results
     */
    public Stream<JsonDocumentResult> findVector(float[] vector) {
        return find(SelectQuery.findByVector(vector));
    }

    /**
     * Retrieve documents from a vector.
     *
     * @param vector
     *      vector list
     * @param recordCount
     *      record count
     * @param filter
     *      metadata filter
     * @return
     *      the list of results
     */
    public Stream<JsonDocumentResult> findVector(float[] vector, Filter filter, int recordCount) {
        return findVector(SelectQuery.builder()
                .includeSimilarity()
                .filter(filter)
                .withLimit(recordCount)
                .orderByAnn(vector)
                .build());
    }

    /**
     * Retrieve documents from a vector.
     *
     * @param vector
     *      vector list
     * @param clazz
     *      expected output class
     * @param <T>
     *       expected type
     * @return
     *      the list of results
     */
    public <T> Stream<DocumentResult<T>> findVector(float[] vector, Class<T> clazz) {
        return findVector(vector).map(r -> new DocumentResult<>(r, clazz));
    }

    /**
     * Retrieve documents from a vector.
     *
     * @param vector
     *      vector list
     * @param mapper
     *      mapper for results
     * @return
     *      the list of results
     * @param <T>
     *    expected type
     */
    public <T> Stream<DocumentResult<T>> findVector(float[] vector, DocumentResultMapper<T> mapper) {
        return findVector(vector).map(mapper::map);
    }

    // Find Vector with a filter and conditions

    /**
     * Full-fledged search with a filter and conditions.
     *
     * @param query
     *      vector query
     * @return
     *     stream of results
     */
    public Stream<JsonDocumentResult> findVector(SelectQuery query) {
        return find(query);
    }

    /**
     * Full-fledged search with a filter and conditions.
     *
     * @param query
     *      vector query
     * @param clazz
     *      clazz for returned type
     * @return
     *     stream of results
     * @param <T>
     *     expected type
     */
    public <T> Stream<DocumentResult<T>> findVector(SelectQuery query, Class<T> clazz) {
        return findVector(query).map(r -> new DocumentResult<>(r, clazz));
    }

    /**
     * Full-fledged search with a filter and conditions.
     *
     * @param query
     *      vector query
     * @param mapper
     *      mapper for results
     * @return
     *     stream of results
     * @param <T>
     *     expected type
     */
    public <T> Stream<DocumentResult<T>> findVector(SelectQuery query, DocumentResultMapper<T> mapper) {
        return findVector(query).map(mapper::map);
    }

    // Find Vector with a filter and conditions

    /**
     * Query builder.
     *
     * @param vector
     *      vector embeddings
     * @param filter
     *      metadata filter
     * @param limit
     *      how many items to be retrieved at most
     * @param includeSimilarity
     *      include similarity
     * @return
     *      result page
     */
    public Stream<JsonDocumentResult> findVector(float[] vector, Filter filter, Integer limit, boolean includeSimilarity) {
        SelectQueryBuilder builder = SelectQuery
                .builder()
                .filter(filter)
                .withLimit(limit)
                .orderByAnn(vector);
        if (includeSimilarity) builder.includeSimilarity();
        return findVector(builder.build());
    }

    /**
     * Query builder.
     *
     * @param query
     *   query
     * @return
     *   result page
     */
    public Page<JsonDocumentResult> findPageVector(SelectQuery query) {
        return findPage(query);
    }

    /**
     * Internal Client for a collection.
     *
     * @return
     *      collection client
     */
    public CollectionClient getRawCollectionClient() {
        return collectionClient;
    }

    /**
     * Set this options during insertMany: {"ordered": true}.
     */
    public void enableOrderingWhenInsert() {
        collectionClient.setInsertManyOrdered(true);
    }

    /**
     * Set this options during insertMany: {"ordered": false}.
     */
    public void disableOrderingWhenInsert() {
        collectionClient.setInsertManyOrdered(false);
    }


}
