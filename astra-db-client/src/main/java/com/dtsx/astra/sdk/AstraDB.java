package com.dtsx.astra.sdk;

import com.datastax.astra.sdk.AstraClient;
import com.datastax.oss.driver.api.core.CqlSession;
import com.dtsx.astra.sdk.db.AstraDBOpsClient;
import com.dtsx.astra.sdk.db.domain.Database;
import com.dtsx.astra.sdk.db.exception.DatabaseNotFoundException;
import com.dtsx.astra.sdk.utils.AstraEnvironment;
import io.stargate.sdk.ServiceDeployment;
import io.stargate.sdk.api.SimpleTokenProvider;
import io.stargate.sdk.http.ServiceHttp;
import io.stargate.sdk.json.ApiClient;
import io.stargate.sdk.json.CollectionClient;
import io.stargate.sdk.json.CollectionRepository;
import io.stargate.sdk.json.NamespaceClient;
import io.stargate.sdk.json.domain.CollectionDefinition;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

/**
 * Hiding top level Json Api and skip interaction with namespaces
 */
@Slf4j @Getter
public class AstraDB {

    /**
     * Hold a reference to target Astra Environment.
     */
    protected final AstraEnvironment env;

    /**
     * Top level resource for json api.
     */
    private final ApiClient apiClient;

    /**
     * Namespace client
     */
    private final NamespaceClient nsClient;


    /**
     * Initialization with endpoint and apikey.
     *
     * @param token
     *      api token
     * @param apiEndpoint
     *      api endpoint
     */
    public AstraDB(String token, String apiEndpoint) {
        Objects.requireNonNull(token, "token");
        Objects.requireNonNull(apiEndpoint, "apiEndpoint");
        // Fixing api
        if (apiEndpoint.endsWith("com")) {
            apiEndpoint = apiEndpoint + "/api/json";
        }
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
        jsonDeploy.addDatacenterTokenProvider("default", new SimpleTokenProvider(token));
        jsonDeploy.addDatacenterServices("default", new ServiceHttp("json", apiEndpoint, apiEndpoint));
        this.apiClient = new ApiClient(jsonDeploy);
        this.nsClient = apiClient.namespace(AstraDBClient.DEFAULT_KEYSPACE);
    }

    /**
     * Full constructor.
     *
     * @param token
     *      token
     * @param databaseId
     *      database identifier
     */
    public AstraDB(@NonNull String token, @NonNull UUID databaseId) {
        this(token, databaseId, null, AstraEnvironment.PROD);
    }

    /**
     * Full constructor.
     *
     * @param token
     *      token
     * @param databaseId
     *      database identifier
     * @param env
     *      environment
     */
    public AstraDB(@NonNull String token, @NonNull UUID databaseId, @NonNull AstraEnvironment env) {
        this(token, databaseId, null, env);
    }

    /**
     * Accessing the database with id an region.
     * @param token
     *      astra token
     * @param databaseId
     *      databsae id
     * @param region
     *      database region
     * @param env
     *      environment
     */
    public AstraDB(@NonNull String token, @NonNull UUID databaseId, String region, @NonNull AstraEnvironment env) {
        this.env = env;
        Database db = new AstraDBOpsClient(token, env)
                .findById(databaseId.toString())
                .orElseThrow(() -> new DatabaseNotFoundException(databaseId.toString()));

        this.apiClient = AstraClient.builder()
                .env(env)
                .withDatabaseRegion(region == null ? region : db.getInfo().getRegion())
                .withDatabaseId(databaseId.toString())
                .disableCrossRegionFailOver()
                .build()
                .apiStargateJson();

        // will inherit 'default_keyspace' from the database
        this.nsClient = apiClient.namespace(db.getInfo().getKeyspace());
    }

    // --------------------------
    // ---  Find, FindAll    ----
    // --------------------------

    /**
     * Check if a store exists.
     *
     * @param store
     *      collection name
     * @return
     *      of the store already exist
     */
    public boolean isCollectionExists(@NonNull  String store) {
        return nsClient.isCollectionExists(store);
    }

    /**
     * List all vector Stores for this environment.
     *
     * @return
     *      name of all vector store.
     */
    public Stream<CollectionDefinition> findAllCollections() {
        return nsClient.findCollections();
    }

    /**
     * Return the collection definition if its exists.
     *
     * @param name
     *      collection name
     */
    public Optional<CollectionDefinition> findCollection(String name) {
        return nsClient.findCollectionByName(name);
    }

    // --------------------------
    // ---       Delete      ----
    // --------------------------

    /**
     * Delete a store if it exists.
     *
     * @param name
     *      store name
     */
    public void deleteCollection(String name) {
        nsClient.deleteCollection(name);
    }

    // --------------------------
    // ---      Create       ----
    // --------------------------

    /**
     * Create the minimal store.
     *
     * @param name
     *      store name
     * @return
     *      json vector store
     */
    public CollectionClient createCollection(String name) {
        return nsClient.createCollection(name);
    }

    /**
     * Create the minimal store.
     *
     * @param name
     *      store name
     * @param clazz
     *      bean type
     * @param <DOC>
     *      type of document in used
     * @return
     *      json vector store
     */
    public <DOC> CollectionRepository<DOC> createCollection(String name, Class<DOC> clazz) {
        return nsClient.createCollection(name, clazz);
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
    public CollectionClient createCollection(String name, int vectorDimension) {
        return nsClient.createCollection(name, vectorDimension);
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
    public <T> CollectionRepository<T> createCollection(String name, int vectorDimension, Class<T> bean) {
        return nsClient.createCollection(name, vectorDimension, bean);
    }


    /**
     * Create the minimal store.
     *
     * @param def
     *      collection definition
     * @return
     *      json vector store
     */
    public CollectionClient createCollection(CollectionDefinition def) {
        return nsClient.createCollection(def);
    }

    /**
     * Create the minimal store.
     *
     * @param def
     *      collection definition
     * @param clazz
     *      bean type
     * @param <DOC>
     *      type of document in used
     * @return
     *      json vector store
     */
    public <DOC> CollectionRepository<DOC> createCollection(CollectionDefinition def, Class<DOC> clazz) {
        return nsClient.createCollection(def, clazz);
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
    public CollectionClient collection(@NonNull  String storeName) {
        return nsClient.collection(storeName);
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
    public <T> CollectionRepository<T> collectionRepository(@NonNull  String storeName, Class<T> clazz) {
        return nsClient.collectionRepository(storeName, clazz);
    }

}
