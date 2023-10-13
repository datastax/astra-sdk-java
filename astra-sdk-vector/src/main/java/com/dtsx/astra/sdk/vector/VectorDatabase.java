package com.dtsx.astra.sdk.vector;

import com.datastax.astra.sdk.AstraClient;
import com.dtsx.astra.sdk.db.AstraDbClient;
import com.dtsx.astra.sdk.db.domain.Database;
import com.dtsx.astra.sdk.db.exception.DatabaseNotFoundException;
import com.dtsx.astra.sdk.utils.AstraEnvironment;
import com.dtsx.astra.sdk.vector.domain.LLMEmbedding;
import io.stargate.sdk.json.JsonApiClient;
import io.stargate.sdk.json.JsonCollectionClient;
import io.stargate.sdk.json.JsonNamespaceClient;
import io.stargate.sdk.json.domain.CollectionDefinition;
import io.stargate.sdk.json.vector.JsonVectorStore;
import io.stargate.sdk.json.vector.SimilarityMetric;
import io.stargate.sdk.json.vector.VectorStore;
import lombok.NonNull;

import java.util.UUID;
import java.util.stream.Stream;

/**
 * Client to work with a database.
 */
public class VectorDatabase {

    /**
     * Hold a reference to target Astra Environment.
     */
    protected final AstraEnvironment env;

    private final JsonApiClient jsonClient;
    /**
     * Namespace client
     */
    private final JsonNamespaceClient nsClient;

    /**
     * Full constructor.
     *
     * @param token
     *      token
     * @param databaseId
     *      datbase identifier
     * @param env
     *      environment
     */
    public VectorDatabase(@NonNull String token, @NonNull UUID databaseId, @NonNull AstraEnvironment env) {
        this.env   = env;

        Database db = new AstraDbClient(token, env)
                .findById(databaseId.toString())
                .orElseThrow(() -> new DatabaseNotFoundException(databaseId.toString()));

        this.jsonClient = AstraClient.builder()
                .withDatabaseRegion(db.getInfo().getRegion())
                .withDatabaseId(databaseId.toString())
                .disableCrossRegionFailOver()
                .build()
                .apiStargateJson();

        // will inherit 'default_keyspace' from the database
        this.nsClient = jsonClient.namespace(db.getInfo().getKeyspace());
    }

    /**
     * List all vector Stores for this environment.
     *
     * @return
     *      name of all vector store.
     */
    public Stream<String> findAllVectorStores() {
        return nsClient.findCollections();
    }

    /**
     * Check if a store exists.
     *
     * @param store
     *      collection name
     * @return
     *      of the store already exist
     */
    public boolean isVectorStoreExist(@NonNull  String store) {
        return nsClient.existCollection(store);
    }

    /**
     * Delete a store if it exists.
     *
     * @param name
     *      store name
     */
    public void deleteVectorStore(String name) {
        nsClient.deleteCollection(name);
    }

    /**
     * Create the minimal store.
     *
     * @param name
     *      store name
     * @param dimension
     *      dimension
     * @return
     *      json vector store
     */
    public JsonVectorStore createVectorStore(String name, int dimension) {
        nsClient.createCollection(name, dimension);
        return nsClient.vectorStore(name);
    }

    /**
     * Create the minimal store.
     *
     * @param name
     *      store name
     * @param dimension
     *      vector dimension
     * @param metric
     *      metric for the similarity
     */
    public JsonVectorStore createVectorStore(String name, int dimension, SimilarityMetric metric) {
        nsClient.createCollection(name, dimension, metric);
        return nsClient.vectorStore(name);
    }

    /**
     * Create the minimal store.
     *
     * @param name
     *      store name
     * @param dimension
     *      dimension
     * @param bean
     *      class of pojo
     * @return
     *      vector store instance
     * @param <T>
     *       object type
     */
    public <T> VectorStore<T> createVectorStore(String name, int dimension, Class<T> bean) {
        nsClient.createCollection(name, dimension);
        return nsClient.vectorStore(name, bean);
    }

    /**
     * Create the minimal store.
     *
     * @param name
     *      store name
     * @param dimension
     *      dimension
     * @param metric
     *      similarity metric
     * @param bean
     *      class of pojo
     * @return
     *      vector store instance
     * @param <T>
     *       object type
     */
    public <T> VectorStore<T> createVectorStore(String name, int dimension, SimilarityMetric metric, Class<T> bean) {
        nsClient.createCollection(name, dimension, metric);
        return nsClient.vectorStore(name, bean);
    }

    /**
     * Create with the vectorize.
     *
     * @param name
     *      vector name
     * @param aiModel
     *      ai model to use
     */
    public JsonVectorStore createVectorStore(@NonNull String name, @NonNull LLMEmbedding aiModel) {
        nsClient.createCollection(CollectionDefinition.builder()
                .name(name)
                .vectorDimension(aiModel.getDimension())
                .similarityMetric(SimilarityMetric.cosine)
                .llmProvider(aiModel.getLlmprovider())
                .llmModel(aiModel.getName())
                .build());
        return nsClient.vectorStore(name);
    }

    // --------------------
    // == Sub resources  ==
    // --------------------

    /**
     * Access the database functions.
     *
     * @param storeName
     *      store identifier
     * @return
     *      storeName client
     */
    public JsonVectorStore vectorStore(@NonNull  String storeName) {
        return nsClient.vectorStore(storeName);
    }

    /**
     * Access the database functions.
     *
     * @param storeName
     *      store identifier
     * @param clazz
     *      type of object used
     * @return
     *      storeName client
     * @param <T>
     *      type of the bean in use
     */
    public <T> VectorStore<T> vectorStore(@NonNull  String storeName, Class<T> clazz) {
        return nsClient.vectorStore(storeName, clazz);
    }

    /**
     * Access json api client
     *
     * @return
     *      json client
     */
    public JsonApiClient getJsonApiClient() {
        return jsonClient;
    }

    /**
     * Access to low level object.
     *
     * @param collectionName
     *      collection name.
     * @return
     *      collection client
     */
    public JsonCollectionClient rawCollectionClient(String collectionName) {
        return nsClient.collection(collectionName);
    }

}
