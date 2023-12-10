package com.dtsx.astra.sdk;

import io.stargate.sdk.core.domain.Page;
import io.stargate.sdk.json.CollectionRepository;
import io.stargate.sdk.json.domain.DeleteQuery;
import io.stargate.sdk.json.domain.Filter;
import io.stargate.sdk.json.domain.JsonResult;
import io.stargate.sdk.json.domain.SelectQuery;
import io.stargate.sdk.json.domain.SelectQueryBuilder;
import io.stargate.sdk.json.domain.odm.Document;
import io.stargate.sdk.json.domain.odm.Result;
import io.stargate.sdk.json.domain.odm.ResultMapper;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Operation on Collection With object Mapping for Astra.
 *
 * @param <DOC>
 *       working document
 */
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
    public String insert(Document<DOC> bean) {
        return collectionRepository.insert(bean);
    }

    // --------------------------
    // ---      SaveOne      ----
    // --------------------------

    /**
     * Save by record.
     *
     * @param current
     *      object Mapping
     * @return
     *      an unique identifier for the document
     */
    public final String save(@NonNull Document<DOC> current) {
        return collectionRepository.save(current);
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
    public final List<String> saveAll(@NonNull List<Document<DOC>> documentList) {
        if (documentList.isEmpty()) return new ArrayList<>();
        return documentList.stream().map(this::save).collect(Collectors.toList());
    }

    /**
     * Low level insertion of multiple records
     *
     * @param documents
     *      list of documents
     * @return
     *      list of ids
     */
    public final List<String> insertAll(List<Document<DOC>> documents) {
        return collectionRepository.insertAll(documents);
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
     * Find by Id.
     *
     * @param id
     *      identifier
     * @return
     *      object if presents
     */
    public Optional<Result<DOC>> findById(@NonNull String id) {
        return collectionRepository.findById(id);
    }

    /**
     * Find all item in the collection.
     *
     * @return
     *      retrieve all items
     */
    public Stream<Result<DOC>> findAll() {
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
    public Stream<Result<DOC>> find(@NonNull SelectQuery query) {
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
    public Page<Result<DOC>> searchPage(SelectQuery query) {
        return collectionRepository.searchPage(query);
    }

    // --------------------------
    // ---     Delete        ----
    // --------------------------

    /**
     * Delete a document from id or vector
     * .
     * @param document
     *      document
     * @return
     *      if document has been deleted.
     */
    public boolean delete(@NonNull Document<DOC> document) {
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
     *      delete queru
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
    public Optional<Result<DOC>> findByVector(@NonNull float[] vector) {
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
    public Page<Result<DOC>> findVector(float[] vector, Filter metadataFilter) {
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
    public List<Result<DOC>> findVector(float[] vector, Integer limit) {
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
    public List<Result<DOC>> findVector(float[] vector, Filter metadataFilter, Integer limit) {
        return collectionRepository.findVector(vector, metadataFilter, limit);
    }

    /**
     * Access Stargate Collection Repository.
     *
     * @return
     *      stargate collection repository.
     */
    public CollectionRepository<DOC> getRawCollectionRepository() {
        return collectionRepository;
    }

}