package com.dtsx.astra.sdk;

import com.datastax.astra.sdk.AstraClient;
import com.dtsx.astra.sdk.db.AstraDBOpsClient;
import com.dtsx.astra.sdk.db.domain.Database;
import com.dtsx.astra.sdk.db.exception.DatabaseNotFoundException;
import com.dtsx.astra.sdk.utils.AstraEnvironment;
import com.dtsx.astra.sdk.domain.LLMEmbedding;
import io.stargate.sdk.ServiceDeployment;
import io.stargate.sdk.api.SimpleTokenProvider;
import io.stargate.sdk.http.ServiceHttp;
import io.stargate.sdk.json.JsonApiClient;
import io.stargate.sdk.json.JsonCollectionClient;
import io.stargate.sdk.json.JsonNamespaceClient;
import io.stargate.sdk.json.domain.CollectionDefinition;
import io.stargate.sdk.json.vector.SimilarityMetric;
import io.stargate.sdk.json.vector.VectorCollectionRepository;
import io.stargate.sdk.json.vector.VectorCollectionRepositoryJson;
import lombok.NonNull;

import java.util.Objects;
import java.util.UUID;
import java.util.stream.Stream;

/**
 * Hiding top level Json Api and skip interaction with namespaces
 */
public class AstraDB {

    /**
     * Hold a reference to target Astra Environment.
     */
    protected final AstraEnvironment env;

    /**
     * Top level resource for json api.
     */
    private final JsonApiClient jsonClient;

    /**
     * Namespace client
     */
    private final JsonNamespaceClient nsClient;

    /**
     * Initialization with endpoint and apikey.
     *
     * @param apiKey
     *      api token
     * @param apiEndpoint
     *      api endpoint
     */
    public AstraDB(String apiKey, String apiEndpoint) {
        Objects.requireNonNull(apiKey, "apiKey");
        Objects.requireNonNull(apiEndpoint, "apiEndpoint");
        // Finding Environment based on apiEndpoint (looping to devops)
        if (apiEndpoint.contains(AstraEnvironment.PROD.getAppsSuffix())) {
            this.env = AstraEnvironment.PROD;
        } else if (apiEndpoint.contains(AstraEnvironment.TEST.getAppsSuffix())) {
            this.env = AstraEnvironment.TEST;
        } else if (apiEndpoint.contains(AstraEnvironment.DEV.getAppsSuffix())) {
            this.env = AstraEnvironment.DEV;
        } else {
            throw new IllegalArgumentException("Unable to detect environment from endpoint");
        }

        // deploy on a single url with a static token (token provider)
        ServiceDeployment<ServiceHttp> jsonDeploy = new ServiceDeployment<>();
        jsonDeploy.addDatacenterTokenProvider("default", new SimpleTokenProvider(apiKey));
        jsonDeploy.addDatacenterServices("default", new ServiceHttp("json", apiEndpoint, apiEndpoint));
        this.jsonClient = new JsonApiClient(jsonDeploy);

        this.nsClient = jsonClient.namespace("default_keyspace");
    }

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
    public AstraDB(@NonNull String token, @NonNull UUID databaseId, @NonNull AstraEnvironment env) {
        this.env = env;
        Database db = new AstraDBOpsClient(token, env)
                .findById(databaseId.toString())
                .orElseThrow(() -> new DatabaseNotFoundException(databaseId.toString()));

        this.jsonClient = AstraClient.builder()
                .env(env)
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
    public Stream<String> findAllCollections() {
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
    public boolean isCollectionExist(@NonNull  String store) {
        return nsClient.existCollection(store);
    }

    /**
     * Delete a store if it exists.
     *
     * @param name
     *      store name
     */
    public void deleteCollection(String name) {
        nsClient.deleteCollection(name);
    }

    /**
     * Create the minimal store.
     *
     * @param name
     *      store name
     * @param vectorDimension
     *      dimension
     * @return
     *      json vector store
     */
    public VectorCollectionRepositoryJson createCollection(String name, int vectorDimension) {
        nsClient.createCollection(name, vectorDimension);
        return nsClient.vectorStore(name);
    }

    /**
     * Create the minimal store.
     *
     * @param name
     *      store name
     * @param vectorDimension
     *      vector dimension
     * @param vectorMetric
     *      metric for the similarity
     * @return
     *      json vector store
     */
    public VectorCollectionRepositoryJson createCollection(String name, int vectorDimension, SimilarityMetric vectorMetric) {
        nsClient.createCollection(name, vectorDimension, vectorMetric);
        return nsClient.vectorStore(name);
    }

    /**
     * Create the minimal store.
     *
     * @param name
     *      store name
     * @param vectorDimension
     *      dimension
     * @param bean
     *      class of pojo
     * @return
     *      vector store instance
     * @param <T>
     *       object type
     */
    public <T> VectorCollectionRepository<T> createCollection(String name, int vectorDimension, Class<T> bean) {
        nsClient.createCollection(name, vectorDimension);
        return nsClient.vectorStore(name, bean);
    }

    /**
     * Create the minimal store.
     *
     * @param name
     *      store name
     * @param vectorDimension
     *      dimension
     * @param vectorMetric
     *      similarity metric
     * @param bean
     *      class of pojo
     * @return
     *      vector store instance
     * @param <T>
     *       object type
     */
    public <T> VectorCollectionRepository<T> createCollection(String name, int vectorDimension, SimilarityMetric vectorMetric, Class<T> bean) {
        nsClient.createCollection(name, vectorDimension, vectorMetric);
        return nsClient.vectorStore(name, bean);
    }

    /**
     * Create with the vectorize.
     *
     * @param name
     *      vector name
     * @param aiModel
     *      ai model to use
     * @return
     *      json vector store
     */
    public VectorCollectionRepositoryJson createCollection(@NonNull String name, @NonNull LLMEmbedding aiModel) {
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
    public VectorCollectionRepositoryJson collection(@NonNull  String storeName) {
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
    public <T> VectorCollectionRepository<T> collection(@NonNull  String storeName, Class<T> clazz) {
        return nsClient.vectorStore(storeName, clazz);
    }

    /**
     * Access json api client
     *
     * @return
     *      json client
     */
    public JsonApiClient getRawJsonApiClient() {
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
    public JsonCollectionClient getRawJsonCollectionClient(String collectionName) {
        return nsClient.collection(collectionName);
    }

}
