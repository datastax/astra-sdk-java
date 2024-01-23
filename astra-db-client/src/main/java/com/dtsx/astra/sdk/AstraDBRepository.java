package com.dtsx.astra.sdk;

import io.stargate.sdk.core.domain.Page;
import io.stargate.sdk.data.CollectionRepository;
import io.stargate.sdk.data.domain.DocumentMutationResult;
import io.stargate.sdk.data.domain.odm.Document;
import io.stargate.sdk.data.domain.odm.DocumentResult;
import io.stargate.sdk.data.domain.query.DeleteQuery;
import io.stargate.sdk.data.domain.query.Filter;
import io.stargate.sdk.data.domain.query.SelectQuery;
import lombok.Getter;
import lombok.NonNull;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Client for AstraDB collection using repository pattern.
 *
 * @param <DOC>
 *       working document
 */
@Getter
public class AstraDBRepository<DOC> {

    CollectionRepository<DOC> collectionRepository;

    /**
     * Default Constructor.
     *
     * @param collectionRepository
     *      current collection
     */
    AstraDBRepository(CollectionRepository<DOC> collectionRepository) {
        this.collectionRepository = collectionRepository;
    }

    /**
     * Check existence of a document from its id.
     * Projection to make it as light as possible.
     *
     * @param id
     *      document identifier
     * @return
     *      existence status
     */
    public boolean exists(String id) {
        return collectionRepository.exists(id);
    }

    // --------------------------
    // ---      Insert       ----
    // --------------------------

    /**
     * Save a NEW RECORD with a defined id.
     * @param bean
     *      current object
     * @return
     *      generated identifier
     */
    public DocumentMutationResult<DOC> insert(Document<DOC> bean) {
        return collectionRepository.insert(bean);
    }

    /**
     * Save a NEW RECORD with a defined id asynchronously
     *
     * @param bean
     *      current object
     * @return
     *      generated identifier
     */
    public CompletableFuture<DocumentMutationResult<DOC>> insertAsync(Document<DOC> bean) {
        return collectionRepository.insertASync(bean);
    }

    // --------------------------
    // ---    Insert All     ----
    // --------------------------

    /**
     * Low level insertion of multiple records, they should not exist, or it will fail.
     *
     * @param documents
     *      list of documents
     * @return
     *      list of ids
     */
    public final List<DocumentMutationResult<DOC>> insert(List<Document<DOC>> documents) {
        return collectionRepository.insert(documents);
    }

    /**
     * Insert a List asynchronously.
     *
     * @param documents
     *      list of documents
     * @return
     *      list of ids
     */
    public final CompletableFuture<List<DocumentMutationResult<DOC>>> insertASync(List<Document<DOC>> documents) {
        return collectionRepository.insertASync(documents);
    }

    /**
     * Low level insertion of multiple records, they should not exist, or it will fail.
     *
     * @param documents
     *      list of documents
     * @param chunkSize
     *      how many document per chunk
     * @param concurrency
     *      how many thread in parallel
     * @return
     *      list of ids
     */
    public final List<DocumentMutationResult<DOC>> insert(List<Document<DOC>> documents, int chunkSize, int concurrency) {
        return collectionRepository.insert(documents, chunkSize, concurrency);
    }

    /**
     * Insert a List asynchronously.
     *
     * @param documents
     *      list of documents
     * @param chunkSize
     *      split into chunks
     * @param concurrency
     *      number of thread to process the chunks
     * @return
     *      list of ids
     */
    public final CompletableFuture<List<DocumentMutationResult<DOC>>> insertASync(List<Document<DOC>> documents, int chunkSize, int concurrency) {
        return collectionRepository.insertASync(documents, chunkSize, concurrency);
    }

    // --------------------------
    // ---      Save         ----
    // --------------------------

    /**
     * Save by record.
     *
     * @param current
     *      object Mapping
     * @return
     *      an unique identifier for the document
     */
    public final DocumentMutationResult<DOC> save(@NonNull Document<DOC> current) {
        return collectionRepository.save(current);
    }

    /**
     * Save by record asynchronously.
     *
     * @param current
     *      object Mapping
     * @return
     *      an unique identifier for the document
     */
    public final CompletableFuture<DocumentMutationResult<DOC>> saveASync(@NonNull Document<DOC> current) {
        return collectionRepository.saveASync(current);
    }


    // --------------------------
    // ---    saveAll        ----
    // --------------------------

    /**
     * Create a new document a generating identifier.
     *
     * @param documentList
     *      object Mapping
     * @return
     *      an unique identifier for the document
     */
    public final List<DocumentMutationResult<DOC>> saveAll(List<Document<DOC>> documentList) {
        return collectionRepository.saveAll(documentList);
    }

    /**
     * Create a new document a generating identifier asynchronously
     *
     * @param documentList
     *      object Mapping
     * @return
     *      an unique identifier for the document
     */
    public final CompletableFuture<List<DocumentMutationResult<DOC>>> saveAllASync(List<Document<DOC>> documentList) {
        return collectionRepository.saveAllASync(documentList);
    }

    /**
     * Create a new document a generating identifier.
     *
     * @param documentList
     *      object Mapping
     * @param chunkSize
     *      size of the chunk to process items
     * @param concurrency
     *      concurrency to process items
     * @return
     *      an unique identifier for the document
     */
    public final List<DocumentMutationResult<DOC>> saveAll(List<Document<DOC>> documentList, int chunkSize, int concurrency) {
        return collectionRepository.saveAll(documentList, chunkSize, concurrency);
    }

    /**
     * Create a new document a generating identifier asynchronously
     *
     * @param documentList
     *      object Mapping
     * @param chunkSize
     *      size of the chunk to process items
     * @param concurrency
     *      concurrency to process items
     * @return
     *      an unique identifier for the document
     */
    public final CompletableFuture<List<DocumentMutationResult<DOC>>> saveAllASync(List<Document<DOC>> documentList, int chunkSize, int concurrency) {
        return collectionRepository.saveAllASync(documentList, chunkSize, concurrency);
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
    public final int count() {
        return count(null);
    }

    /**
     * Count Document request.
     *
     * @param jsonFilter
     *      a filter for the count
     * @return
     *      number of document.
     */
    public final int count(Filter jsonFilter) {
        return collectionRepository.count(jsonFilter);
    }

    // --------------------------
    // ---      Find         ----
    // --------------------------

    /**
     * Find by id.
     *
     * @param id
     *      identifier
     * @return
     *      object if presents
     */
    public Optional<DocumentResult<DOC>> findById(@NonNull String id) {
        return collectionRepository.findById(id);
    }

    /**
     * Find all item in the collection.
     *
     * @return
     *      retrieve all items
     */
    public Stream<DocumentResult<DOC>> findAll() {
       return collectionRepository.search();
    }

    /**
     * Find all item in the collection.
     *
     * @param query
     *      search with a query
     * @return
     *      retrieve all items
     */
    public Stream<DocumentResult<DOC>> find(@NonNull SelectQuery query) {
        return collectionRepository.search(query);
    }

    /**
     * Find a page in the collection.
     *
     * @param query
     *      current query
     * @return
     *      page of records
     */
    public Page<DocumentResult<DOC>> searchPage(SelectQuery query) {
        return collectionRepository.searchPage(query);
    }

    // --------------------------
    // ---     Delete        ----
    // --------------------------

    /**
     * Delete a document from id or vector.
     *
     * @param document
     *      document
     * @return
     *      if document has been deleted.
     */
    public boolean delete(Document<DOC> document) {
        return collectionRepository.delete(document);
    }

    /**
     * Delete all documents
     *
     * @return
     *     number of document deleted
     */
    public int deleteAll() {
        return collectionRepository.deleteAll();
    }

    /**
     * Use parallelism and async to delete all records.
     *
     * @param documents
     *      list of records
     * @return
     *      number of records deleted
     */
    public int deleteAll(List<Document<DOC>> documents) {
        List<CompletableFuture<Integer>> futures = documents.stream()
                .map(record -> CompletableFuture.supplyAsync(() -> delete(record) ? 1 : 0))
                .collect(Collectors.toList());
        return futures.stream()
                .map(CompletableFuture::join) // This will wait for the result of each future
                .mapToInt(Integer::intValue)
                .sum();
    }

    /**
     * Delete item through a query.
     *
     * @param deleteQuery
     *      delete query
     * @return
     *       number of records deleted
     */
    public int deleteAll(DeleteQuery deleteQuery) {
        return collectionRepository.deleteAll(deleteQuery);
    }

    // ------------------------------
    // --- OPERATIONS VECTOR     ----
    // ------------------------------

    /**
     * Find by vector
     *
     * @param vector
     *      vector
     * @return
     *      object if presents
     */
    public Optional<DocumentResult<DOC>> findByVector(float[] vector) {
        return collectionRepository.findByVector(vector);
    }

    /**
     * Delete by vector
     *
     * @param vector
     *      vector
     * @return
     *      if object deleted
     */
    public boolean deleteByVector(float[] vector) {
        return collectionRepository.deleteByVector(vector);
    }


    /**
     * Delete by vector
     *
     * @param id
     *      id
     * @return
     *      if object deleted
     */
    public boolean deleteById(String id) {
        return collectionRepository.deleteById(id);
    }

    // ------------------------------
    // ---  Similarity Search    ----
    // ------------------------------

    /**
     * Search similarity from the vector and a limit, if a limit / no paging
     *
     * @param vector
     *      vector
     * @param metadataFilter
     *      metadata filtering
     * @return
     *      page of results
     */
    public Page<DocumentResult<DOC>> findVector(float[] vector, Filter metadataFilter) {
        return collectionRepository.findVector(vector, metadataFilter);
    }

    /**
     * Search similarity from the vector and a limit, if a limit / no paging
     *
     * @param vector
     *      vector
     * @param limit
     *      return count
     * @return
     *      page of results
     */
    public List<DocumentResult<DOC>> findVector(float[] vector, Integer limit) {
        return collectionRepository.findVector(vector, limit);
    }
    /**
     * Search similarity from the vector and a limit, if a limit / no paging
     *
     * @param vector
     *      vector
     * @param limit
     *      return count
     * @param metadataFilter
     *      metadata filtering
     * @return
     *      page of results
     */
    public List<DocumentResult<DOC>> findVector(float[] vector, Filter metadataFilter, Integer limit) {
        return collectionRepository.findVector(vector, metadataFilter, limit);
    }
}
