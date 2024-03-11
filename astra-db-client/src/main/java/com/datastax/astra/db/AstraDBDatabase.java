package com.datastax.astra.db;

import com.datastax.astra.devops.db.AstraDBDevopsClient;
import io.stargate.sdk.data.client.DataApiClient;
import io.stargate.sdk.data.client.DataApiClients;
import io.stargate.sdk.data.client.DataApiCollection;
import io.stargate.sdk.data.client.DataApiNamespace;
import io.stargate.sdk.data.client.model.CreateCollectionOptions;
import io.stargate.sdk.data.client.model.CreateNamespaceOptions;
import io.stargate.sdk.data.internal.model.ApiResponse;
import io.stargate.sdk.data.internal.model.CollectionInformation;
import io.stargate.sdk.data.internal.model.NamespaceInformation;
import io.stargate.sdk.http.LoadBalancedHttpClient;
import io.stargate.sdk.http.ServiceHttp;
import io.stargate.sdk.utils.Assert;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.function.Function;
import java.util.stream.Stream;

/**
 * Client for AstraDB at database level (crud for collections).
 */
@Slf4j @Getter
public class AstraDBDatabase implements DataApiNamespace, DataApiClient {

    /**
     * Url to access the API
     */
    private final AstraDBEndpoint astraDbEndpoint;

    /**
     * Top level resource for json api.
     */
    private final DataApiClient dataApiClient;

    /**
     * Namespace client
     */
    private final DataApiNamespace dataApiNamespace;

    /**
     * Devops Api Client.
     */
    private final AstraDBDevopsClient devopsApi;

    /**
     * Token used for the credentials
     */
    private final String token;

    /**
     * Name of the namespace in use.
     */
    private final String namespaceName;

    /**
     * Configuration of the
     */
    private final AstraDBOptions astraDbOptions;

    /**
     * Initialization with endpoint and apikey.
     *
     * @param token
     *      api token
     * @param apiEndpoint
     *      api endpoint
     */
    public AstraDBDatabase(String apiEndpoint, String token) {
        this(apiEndpoint, token, AstraDBClient.DEFAULT_KEYSPACE);
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
    public AstraDBDatabase(String apiEndpoint, String token, String keyspace) {
        this(apiEndpoint, token, keyspace, new AstraDBOptions());
    }

    /**
     * Initialization with endpoint and apikey.
     *
     * @param token
     *      api token
     * @param apiEndpoint
     *      api endpoint
     * @param astraDbOptions
     *      setup of the clients with options
     */
    public AstraDBDatabase(String apiEndpoint, String token, AstraDBOptions astraDbOptions) {
        this(apiEndpoint, token, AstraDBClient.DEFAULT_KEYSPACE, astraDbOptions);
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
     * @param astraDbOptions
     *      setup of the clients with options
     */
    public AstraDBDatabase(String apiEndpoint, String token, String keyspace, AstraDBOptions astraDbOptions) {
        this(AstraDBEndpoint.parse(apiEndpoint), token, keyspace, astraDbOptions);
    }

    /**
     * Initialization with endpoint and apikey.
     *
     * @param apiEndpoint
     *      api endpoint
     *  @param token
     *      api token
     * @param namespaceName
     *      namespaceName
     * @param astraDbOptions
     *      setup of the clients with options
     */
    public AstraDBDatabase(AstraDBEndpoint apiEndpoint, String token, String namespaceName, AstraDBOptions astraDbOptions) {
        Assert.notNull(apiEndpoint, "endpoint");
        Assert.hasLength(token, "token");
        Assert.hasLength(namespaceName, "namespaceName");
        this.token            = token;
        this.astraDbEndpoint  = apiEndpoint;
        this.astraDbOptions   = astraDbOptions;
        this.namespaceName    = namespaceName;
        this.dataApiClient    = DataApiClients.create(astraDbEndpoint.getApiEndPoint(), token, astraDbOptions.getHttpClientOptions());
        this.dataApiNamespace = dataApiClient.getNamespace(namespaceName);
        this.devopsApi        = new AstraDBDevopsClient(token);
        String version        = AstraDBDatabase.class.getPackage().getImplementationVersion();
    }

    // ------------------------------------------
    // ----  Crud on Namespaces (dataApi)    ----
    // ------------------------------------------

    /** {@inheritDoc} */
    @Override
    public DataApiNamespace getNamespace(String namespaceName) {
        return dataApiClient.getNamespace(namespaceName);
    }

    /** {@inheritDoc} */
    @Override
    public Stream<String> listNamespaceNames() {
        return dataApiClient.listNamespaceNames();
    }

    /** {@inheritDoc} */
    @Override
    public Stream<NamespaceInformation> listNamespaces() {
        return dataApiClient.listNamespaces();
    }

    /** {@inheritDoc} */
    @Override
    public boolean isNamespaceExists(String namespace) {
        return dataApiClient.isNamespaceExists(namespace);
    }

    /** {@inheritDoc} */
    @Override
    public DataApiNamespace createNamespace(String namespace) {
        // We will NOT use the Data API methods in the Astra context but some devops API
        devopsApi.database(astraDbEndpoint.getDatabaseId().toString()).keyspaces().create(namespace);
        return new AstraDBDatabase(astraDbEndpoint, token, namespace, astraDbOptions);
    }

    /** {@inheritDoc} */
    @Override
    public void dropNamespace(String namespace) {
        devopsApi.database(astraDbEndpoint.getDatabaseId().toString()).keyspaces().delete(namespace);

    }

    /** {@inheritDoc} */
    @Override
    public DataApiNamespace createNamespace(String namespace, CreateNamespaceOptions options) {
        throw new UnsupportedOperationException("Astra enforce the replication factor for you.");
    }

    // --------------------------------------------
    // ---- Information on current Namespace   ----
    // --------------------------------------------

    /** {@inheritDoc} */
    @Override
    public String getName() {
        return this.namespaceName;
    }

    /** {@inheritDoc} */
    @Override
    public DataApiClient getClient() {
        return dataApiClient;
    }

    // --------------------------------------------
    // ----  Crud on Collection (namespace)    ----
    // --------------------------------------------

    /** {@inheritDoc} */
    @Override
    public Stream<String> listCollectionNames() {
        return dataApiNamespace.listCollectionNames();
    }

    /** {@inheritDoc} */
    @Override
    public Stream<CollectionInformation> listCollections() {
        return dataApiNamespace.listCollections();
    }

    /** {@inheritDoc} */
    @Override
    public <DOC> DataApiCollection<DOC> getCollection(String collectionName, Class<DOC> documentClass) {
        return dataApiNamespace.getCollection(collectionName, documentClass);
    }

    /** {@inheritDoc} */
    @Override
    public void drop() {
        dataApiNamespace.drop();
    }

    /** {@inheritDoc} */
    @Override
    public <DOC> DataApiCollection<DOC> createCollection(String collectionName, CreateCollectionOptions createCollectionOptions, Class<DOC> documentClass) {
        return dataApiNamespace.createCollection(collectionName, createCollectionOptions, documentClass);
    }

    /** {@inheritDoc} */
    @Override
    public void dropCollection(String collectionName) {
        dataApiNamespace.dropCollection(collectionName);
    }

    /** {@inheritDoc} */
    @Override
    public ApiResponse runCommand(String jsonCommand) {
        return dataApiNamespace.runCommand(jsonCommand);
    }

    /** {@inheritDoc} */
    @Override
    public <DOC> DOC runCommand(String jsonCommand, Class<DOC> documentClass) {
        return dataApiNamespace.runCommand(jsonCommand, documentClass);
    }

    /** {@inheritDoc} */
    @Override
    public Function<ServiceHttp, String> lookup() {
        return dataApiNamespace.lookup();
    }

    /** {@inheritDoc} */
    @Override
    public LoadBalancedHttpClient getHttpClient() {
        return dataApiNamespace.getHttpClient();
    }

}
