package com.dtsx.astra.sdk;

import io.stargate.sdk.core.domain.Page;
import io.stargate.sdk.json.CollectionClient;
import io.stargate.sdk.json.domain.DeleteQuery;
import io.stargate.sdk.json.domain.Filter;
import io.stargate.sdk.json.domain.JsonDocument;
import io.stargate.sdk.json.domain.JsonResult;
import io.stargate.sdk.json.domain.JsonResultUpdate;
import io.stargate.sdk.json.domain.SelectQuery;
import io.stargate.sdk.json.domain.SelectQueryBuilder;
import io.stargate.sdk.json.domain.UpdateQuery;
import io.stargate.sdk.json.domain.UpdateStatus;
import io.stargate.sdk.json.domain.odm.Document;
import io.stargate.sdk.json.domain.odm.Result;
import io.stargate.sdk.json.domain.odm.ResultMapper;
import lombok.NonNull;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Operation On a Collection.
 */
public class AstraDBCollection {

    /**
     * Stargate Client for a collection
     */
    CollectionClient collectionClient;

    /**
     * Default constructor.
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
     * Insert with a Json Document.
     *
     * @param bean
     *      current bean
     * @param <DOC>
     *      type of object in use
     * @return
     *      new id
     */
    public final <DOC> String insertOne(@NonNull Document<DOC> bean) {
        return collectionClient.insertOne(bean.toJsonDocument());
    }

    /**
     * Insert a new document for a vector collection
     *
     * @param jsonDocument
     *      json Document
     * @return
     *      identifier for the document
     */
    public String insertOne(JsonDocument jsonDocument) {
        return collectionClient.insertOne(jsonDocument);
    }

    /**
     * Upsert a document in the collection.
     *
     * @param jsonDocument
     *      current document
     * @return
     *      document id
     */
    public String upsert(@NonNull JsonDocument jsonDocument) {
        return collectionClient.upsert(jsonDocument);
    }

    // --------------------------
    // ---   Insert Many     ----
    // --------------------------

    /**
     * Low level insertion of multiple records
     *
     * @param documents
     *      list of documents
     * @param <DOC>
     *      object T in use.
     * @return
     *      list of ids
     */
    public final <DOC> List<String> insertMany(List<DOC> documents) {
        return collectionClient.insertMany(documents);
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
    public Optional<JsonResult> findOne(String rawJsonQuery) {
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
    public Optional<JsonResult> findOne(SelectQuery query) {
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
    public <DOC> Optional<Result<DOC>> findOne(SelectQuery query, Class<DOC> clazz) {
        return findOne(query).map(r -> new Result<>(r, clazz));
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
    public <DOC> Optional<Result<DOC>> findOne(String query, Class<DOC> clazz) {
        return findOne(query).map(r -> new Result<>(r, clazz));
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
    public <DOC> Optional<Result<DOC>> findOne(SelectQuery query, ResultMapper<DOC> mapper) {
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
    public <DOC> Optional<Result<DOC>> findOne(String query, ResultMapper<DOC> mapper) {
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
    public Optional<JsonResult> findById(String id) {
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
    public <DOC> Optional<Result<DOC>> findById(@NonNull String id, Class<DOC> clazz) {
        return findById(id).map(r -> new Result<>(r, clazz));
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
    public <DOC> Optional<Result<DOC>> findById(@NonNull String id, ResultMapper<DOC> mapper) {
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
    public Optional<JsonResult> findOneByVector(float[] vector) {
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
    public <DOC> Optional<Result<DOC>> findOneByVector(float[] vector, Class<DOC> clazz) {
        return findOneByVector(vector).map(r -> new Result<>(r, clazz));
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
    public <DOC> Optional<Result<DOC>> findOneByVector(float[] vector, ResultMapper<DOC> mapper) {
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
    public Stream<JsonResult> find(SelectQuery query) {
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
    public Page<JsonResult> findPage(SelectQuery pagedQuery) {
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
    public Page<JsonResult> findPage(String pagedQuery) {
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
    public  <DOC> Stream<Result<DOC>> find(SelectQuery query, Class<DOC> clazz) {
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
    public  <DOC> Stream<Result<DOC>> find(SelectQuery query, ResultMapper<DOC> mapper) {
        return collectionClient.find(query, mapper);
    }

    /**
     * Get all items in a collection.
     *
     * @return
     *      all items
     */
    public Stream<JsonResult> findAll() {
        return collectionClient.findAll();
    }

    /**
     * Find All with Object Mapping.
     *
     * @param clazz
     *      class to be used
     * @return
     *      stream of results
     * @param <DOC>
     *       class to be marshalled
     */
    public <DOC> Stream<Result<DOC>> findAll(Class<DOC> clazz) {
        return collectionClient.findAll(clazz);
    }

    /**
     * Find All with Object Mapping.
     *
     * @param mapper
     *      convert a json into expected pojo
     * @return
     *      stream of results
     * @param <DOC>
     *       class to be marshalled
     */
    public <DOC> Stream<Result<DOC>> findAll(ResultMapper<DOC> mapper) {
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
    public <T> Page<Result<T>> findPage(SelectQuery query, Class<T> clazz) {
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
    public int deleteOne(DeleteQuery deleteQuery) {
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
    public int deleteById(String id) {
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
    public int deleteByVector(float[] vector) {
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
    public int deleteMany(DeleteQuery deleteQuery) {
        return collectionClient.deleteMany(deleteQuery);
    }

    /**
     * Clear the collection.
     *
     * @return
     *      number of items deleted
     */
    public int deleteAll() {
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
    public Stream<JsonResult> findVector(float[] vector, Integer limit) {
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
    public Stream<JsonResult> findVector(float[] vector, Filter filter, Integer limit) {
        return find(SelectQuery.builder()
                .withFilter(filter)
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
     *      page page of results
     */
    public Page<JsonResult> findVectorPage(SelectQuery query) {
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
    public Page<JsonResult> findVectorPage(float[] vector, Filter filter, Integer limit, String pagingState) {
        return findVectorPage(SelectQuery.builder()
                .withFilter(filter)
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
    public <DOC> Page<Result<DOC>> findVectorPage(float[] vector, Filter filter, Integer limit, String pagingState, Class<DOC> clazz) {
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
    public <DOC> Page<Result<DOC>> findVectorPage(float[] vector, Filter filter, Integer limit, String pagingState, ResultMapper<DOC> mapper) {
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
    public Stream<JsonResult> findVector(float[] vector) {
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
    public Stream<JsonResult> findVector(float[] vector, Filter filter, int recordCount) {
        return findVector(SelectQuery.builder()
                .includeSimilarity()
                .withFilter(filter)
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
     */public <T> Stream<Result<T>> findVector(float[] vector, Class<T> clazz) {
        return findVector(vector).map(r -> new Result<>(r, clazz));
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
     *     expected type
     */
    public <T> Stream<Result<T>> findVector(float[] vector, ResultMapper<T> mapper) {
        return findVector(vector).map(mapper::map);
    }

    // Find Vector with a filter and conditions

    /**
     * Full fledge search with a filter and conditions.
     *
     * @param query
     *      vector query
     * @return
     *     stream of results
     */
    public Stream<JsonResult> findVector(SelectQuery query) {
        return find(query);
    }

    /**
     * Full fledge search with a filter and conditions.
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
    public <T> Stream<Result<T>> findVector(SelectQuery query, Class<T> clazz) {
        return findVector(query).map(r -> new Result<>(r, clazz));
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
    public <T> Stream<Result<T>> findVector(SelectQuery query, ResultMapper<T> mapper) {
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
     * @param includeSimilarity
     *      include similarity
     * @return
     *      result page
     */
    public Stream<JsonResult> findVector(float[] vector, Filter filter, Integer limit, boolean includeSimilarity) {
        SelectQueryBuilder builder = SelectQuery
                .builder()
                .withFilter(filter)
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
    public Page<JsonResult> findPageVector(SelectQuery query) {
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

}
