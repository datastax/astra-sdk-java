package com.dtsx.astra.sdk;

import com.dtsx.astra.sdk.db.AstraDBOpsClient;
import com.dtsx.astra.sdk.db.domain.Database;
import com.dtsx.astra.sdk.db.exception.DatabaseNotFoundException;
import com.dtsx.astra.sdk.utils.ApiLocator;
import com.dtsx.astra.sdk.utils.AstraEnvironment;
import io.stargate.sdk.ServiceDeployment;
import io.stargate.sdk.api.SimpleTokenProvider;
import io.stargate.sdk.data.DataApiClient;
import io.stargate.sdk.data.NamespaceClient;
import io.stargate.sdk.data.domain.CollectionDefinition;
import io.stargate.sdk.data.domain.SimilarityMetric;
import io.stargate.sdk.http.ServiceHttp;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;

/**
 * Client for AstraDB at database level (crud for collections).
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
    private final DataApiClient apiClient;

    /**
     * Namespace client
     */
    private NamespaceClient nsClient;

    /**
     * Url to access the API
     */
    private final String apiEndpoint;

    /**
     * Initialization with endpoint and apikey.
     *
     * @param token
     *      api token
     * @param apiEndpoint
     *      api endpoint
     */
    public AstraDB(String token, String apiEndpoint) {
        this(token, apiEndpoint, AstraDBAdmin.DEFAULT_KEYSPACE);
    }

    /**
     * Initialization with endpoint and apikey.
     *
     * @param token
     *      api token
     * @param apiEndpoint
     *      api endpoint
     * @param keyspace
     *      keyspace
     */
    public AstraDB(@NonNull String token, @NonNull String apiEndpoint, @NonNull String keyspace) {
        // Support for apiEndpoint with or without /api/json
        if (apiEndpoint.endsWith("com")) {
            apiEndpoint = apiEndpoint + "/api/json";
        }
        this.apiEndpoint = apiEndpoint;

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
        this.apiClient = new DataApiClient(jsonDeploy);
        this.nsClient = apiClient.namespace(keyspace);
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
        this(token, databaseId, null, AstraEnvironment.PROD, AstraDBAdmin.DEFAULT_KEYSPACE);
    }

    /**
     * Full constructor.
     *
     * @param token
     *      token
     * @param databaseId
     *      database identifier
     * @param keyspace
     *      database keyspace
     */
    public AstraDB(@NonNull String token, @NonNull UUID databaseId, @NonNull String keyspace) {
        this(token, databaseId, null, AstraEnvironment.PROD, keyspace);
    }

    /**
     * Full constructor.
     *
     * @param token
     *      token
     * @param databaseId
     *      database identifier
     * @param region
     *      database region
     * @param keyspace
     *      keyspace
     */
    public AstraDB(@NonNull String token, @NonNull UUID databaseId, @NonNull String region, @NonNull String keyspace) {
        this(token, databaseId, region, AstraEnvironment.PROD, keyspace);
    }

    /**
     * Accessing the database with id and region.
     *
     * @param token
     *      astra token
     * @param databaseId
     *      database id
     * @param region
     *      database region
     * @param env
     *      environment
     * @param keyspace
     *      destination keyspace
     */
    public AstraDB(@NonNull String token, @NonNull UUID databaseId, String region, @NonNull AstraEnvironment env, String keyspace) {
        this.env = env;
        Database db = new AstraDBOpsClient(token, env)
                .findById(databaseId.toString())
                .orElseThrow(() -> new DatabaseNotFoundException(databaseId.toString()));

        if (region == null) region = db.getInfo().getRegion();

        ServiceDeployment<ServiceHttp> jsonDeploy = new ServiceDeployment<>();
        jsonDeploy.addDatacenterTokenProvider("default", new SimpleTokenProvider(token));
        this.apiEndpoint = ApiLocator.getApiJsonEndpoint(env, databaseId.toString(), region);
        jsonDeploy.addDatacenterServices("default", new ServiceHttp("json", apiEndpoint, apiEndpoint));

        final String defaultRegion = region;
        db.getInfo()
          .getDatacenters().stream()
          .filter(dc->!dc.getRegion().equals(defaultRegion))
          .forEach(dc -> {
           String dcApiEndpoint = ApiLocator.getApiJsonEndpoint(env, databaseId.toString(), dc.getRegion());
           jsonDeploy.addDatacenterServices(dc.getName(), new ServiceHttp("json", dcApiEndpoint, dcApiEndpoint));
        });

        this.apiClient = new DataApiClient(jsonDeploy);
        if (keyspace == null) {
            keyspace = db.getInfo().getKeyspace();
        }
        this.nsClient = apiClient.namespace(keyspace);
    }

    // --------------------------
    // ---    Keyspace      ----
    // --------------------------

    /**
     * Validate a keyspace exists.
     *
     * @param keyspace
     *      current keyspace
     * @return
     *      true if the keyspace exists
     */
    public boolean isKeyspaceExists(String keyspace) {
        return apiClient.isNamespaceExists(keyspace);
    }

    /**
     * List all keyspace for this environment.
     *
     * @return
     *      list of keyspace names
     */
    public Stream<String> findAllKeyspaceNames() {
        return apiClient.findAllNamespaces();
    }

    /**
     * get current keyspace name
     *
     * @return
     *      list of keyspace names
     */
    public String getCurrentKeyspace() {
        return this.nsClient.getNamespace();
    }

    /**
     * Change the current keyspace.
     *
     * @param keyspace
     *      new keyspace
     */
    public void changeKeyspace(String keyspace) {
        this.nsClient = apiClient.namespace(keyspace);
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
     * @return
     *      collection definition
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
    public AstraDBCollection createCollection(String name) {
        return new AstraDBCollection(nsClient.createCollection(name));
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
    public <DOC> AstraDBRepository<DOC> createCollection(String name, Class<DOC> clazz) {
        return new AstraDBRepository<>(nsClient.createCollection(name, clazz));
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
    public AstraDBCollection createCollection(String name, int vectorDimension) {
        return new AstraDBCollection(nsClient.createCollection(name, vectorDimension));
    }

    /**
     * Create the minimal store.
     *
     * @param name
     *      store name
     * @param vectorDimension
     *      dimension
     * @param metric
     *      similarity metric for the vector
     * @return
     *      json vector store
     */
    public AstraDBCollection createCollection(String name, int vectorDimension, SimilarityMetric metric) {
        return new AstraDBCollection(nsClient.createCollection(CollectionDefinition
                .builder()
                .name(name)
                .vector(vectorDimension, metric)
                .build()));
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
    public <T> AstraDBRepository<T> createCollection(String name, int vectorDimension, Class<T> bean) {
        return new AstraDBRepository<>(nsClient.createCollection(CollectionDefinition.builder()
                .name(name)
                .vector(vectorDimension, SimilarityMetric.cosine)
                .build(), bean));
    }


    /**
     * Create the minimal store.
     *
     * @param def
     *      collection definition
     * @return
     *      json vector store
     */
    public AstraDBCollection createCollection(CollectionDefinition def) {
        return new AstraDBCollection(nsClient.createCollection(def));
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
    public <DOC> AstraDBRepository<DOC> createCollection(CollectionDefinition def, Class<DOC> clazz) {
        return new AstraDBRepository<>(nsClient.createCollection(def, clazz));
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
    public AstraDBCollection collection(@NonNull  String storeName) {
        return new AstraDBCollection(nsClient.collection(storeName));
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
    public <T> AstraDBRepository<T> collectionRepository(@NonNull  String storeName, Class<T> clazz) {
        return new AstraDBRepository<>(nsClient.collectionRepository(storeName, clazz));
    }

    /**
     * Access the low level Stargate Namespace resource operation.
     *
     * @return
     *      raw namespace client
     */
    public NamespaceClient getNamespaceClient() {
        return nsClient;
    }

}
