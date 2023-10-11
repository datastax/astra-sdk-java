package com.dtsx.astra.sdk.vector;

import com.datastax.astra.sdk.AstraClient;
import com.dtsx.astra.sdk.db.AstraDbClient;
import com.dtsx.astra.sdk.db.domain.Database;
import com.dtsx.astra.sdk.db.exception.DatabaseNotFoundException;
import com.dtsx.astra.sdk.utils.AstraEnvironment;
import com.dtsx.astra.sdk.vector.domain.LLMEmbedding;
import io.stargate.sdk.core.domain.ObjectMap;
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
public class AstraVectorDatabaseClient {

    /**
     * Hold a reference to target Astra Environment.
     */
    protected final AstraEnvironment env;

    /**
     * Namespace client
     */
    private final JsonNamespaceClient nsClient;

    public AstraVectorDatabaseClient(@NonNull String token, @NonNull UUID databaseId, @NonNull AstraEnvironment env) {
        this.env   = env;

        Database db = new AstraDbClient(token, env)
                .findById(databaseId.toString())
                .orElseThrow(() -> new DatabaseNotFoundException(databaseId.toString()));

        JsonApiClient jsonClient = AstraClient.builder()
                .withDatabaseRegion(db.getInfo().getRegion())
                .withDatabaseId(databaseId.toString())
                .disableCrossRegionFailOver()
                .build()
                .apiStargateJson();

        this.nsClient = jsonClient.namespace(db.getInfo().getKeyspace());
    }

    /**
     * List all vector Stores for this environment.
     *
     * @return
     *      name of all vector store.
     */
    public Stream<String> findAllStores() {
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
    public boolean isStoreExist(@NonNull  String store) {
        return nsClient.existCollection(store);
    }

    /**
     * Delete a store if it exists.
     *
     * @param name
     *      store name
     */
    public void deleteStore(String name) {
        nsClient.deleteCollection(name);
    }

    /**
     * Create the minimal store.
     *
     * @param name
     *      store name
     * param dimension
     *      vector dimension
     * @param metric
     *      metric for the similarity
     */
    public void createVectorStore(String name, int dimension, SimilarityMetric metric) {
        nsClient.createCollection(name, dimension, metric);
    }

    /**
     * Create the minimal store.
     *
     * @param name
     *      store name
     * param dimension
     *      dimension
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
     * param dimension
     *      dimension
     */
    public <T> VectorStore<T> createVectorStore(String name, int dimension, Class<T> bean) {
        nsClient.createCollection(name, dimension);
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
    public void createVectorStore(@NonNull String name, @NonNull LLMEmbedding aiModel) {
        nsClient.createCollection(CollectionDefinition.builder()
                .name(name)
                .vectorDimension(aiModel.getDimension())
                .similarityMetric(SimilarityMetric.cosine)
                .llmProvider(aiModel.getLlmprovider().name())
                .llmModel(aiModel.getName())
                .build());
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
    public VectorStore<ObjectMap> vectorStore(@NonNull  String storeName) {
        return nsClient.vectorStore(storeName);
    }

    /**
     * Access the database functions.
     *
     * @param storeName
     *      store identifier
     * @return
     *      storeName client
     */
    public <T> VectorStore<T> vectorStore(@NonNull  String storeName, Class<T> clazz) {
        return nsClient.vectorStore(storeName, clazz);
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
