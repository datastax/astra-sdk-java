package com.datastax.astradb.client.v2;

import com.datastax.astradb.client.AstraDBAdmin;
import com.dtsx.astra.sdk.db.AstraDBOpsClient;
import com.dtsx.astra.sdk.db.domain.Database;
import com.dtsx.astra.sdk.db.exception.DatabaseNotFoundException;
import com.dtsx.astra.sdk.utils.ApiLocator;
import com.dtsx.astra.sdk.utils.AstraEnvironment;
import io.stargate.data_api.client.DataApiClient;
import io.stargate.data_api.client.DataApiClients;
import io.stargate.data_api.client.DataApiCollection;
import io.stargate.data_api.client.DataApiNamespace;
import io.stargate.data_api.client.model.CreateCollectionOptions;
import io.stargate.data_api.client.model.CreateNamespaceOptions;
import io.stargate.data_api.internal.model.CreateCollectionRequest;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.UUID;
import java.util.stream.Stream;

/**
 * Client for AstraDB at database level (crud for collections).
 */
@Slf4j @Getter
public class AstraDB implements DataApiNamespace, DataApiClient {

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
    private DataApiNamespace nsClient;

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
    public AstraDB(String apiEndpoint, String token) {
        this(apiEndpoint, token, AstraDBAdmin.DEFAULT_KEYSPACE);
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
    public AstraDB(@NonNull String apiEndpoint, @NonNull String token, @NonNull String keyspace) {
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

        this.apiClient = DataApiClients.create(apiEndpoint, token);
        this.nsClient  = apiClient.getNamespace(keyspace);
        String version =  AstraDB.class.getPackage().getImplementationVersion();
        AstraDBAdmin.setCallerName(AstraDBAdmin.USER_AGENT, (null != version) ? version :  "dev");
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

        // If no region is provided, we use the default region of the DB
        if (region == null) {
            region = db.getInfo().getRegion();
        }
        if (keyspace == null) {
            keyspace = db.getInfo().getKeyspace();
        }
        this.apiEndpoint = ApiLocator.getApiJsonEndpoint(env, databaseId.toString(), region);
        this.apiClient   = DataApiClients.create(apiEndpoint, token);
        this.nsClient    = apiClient.getNamespace(keyspace);
    }


    @Override
    public DataApiNamespace getNamespace(String namespaceName) {
        return apiClient.getNamespace(namespaceName);
    }

    @Override
    public Stream<String> listNamespaceNames() {
        return apiClient.listNamespaceNames();
    }

    @Override
    public boolean isNamespaceExists(String namespace) {
        return apiClient.isNamespaceExists(namespace);
    }

    @Override
    public DataApiNamespace createNamespace(String namespace) {
        // use devops API
        return null;
    }

    @Override
    public void dropNamespace(String namespace) {
        // use devops API

    }

    @Override
    public DataApiNamespace createNamespace(String namespace, CreateNamespaceOptions options) {
        return null;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public Stream<String> listCollectionNames() {
        return null;
    }

    @Override
    public Stream<CreateCollectionRequest> listCollections() {
        return null;
    }

    @Override
    public <DOC> DataApiCollection<DOC> getCollection(String collectionName, Class<DOC> documentClass) {
        return null;
    }

    @Override
    public void drop() {

    }

    @Override
    public <DOC> DataApiCollection<DOC> createCollection(String collectionName, CreateCollectionOptions createCollectionOptions, Class<DOC> documentClass) {
        return null;
    }

    @Override
    public void dropCollection(String collectionName) {

    }

    @Override
    public void close() throws IOException {
    }
}
