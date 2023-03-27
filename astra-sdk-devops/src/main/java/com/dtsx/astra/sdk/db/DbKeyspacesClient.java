package com.dtsx.astra.sdk.db;

import com.dtsx.astra.sdk.AbstractApiClient;
import com.dtsx.astra.sdk.db.domain.Database;
import com.dtsx.astra.sdk.db.exception.KeyspaceAlreadyExistException;
import com.dtsx.astra.sdk.db.exception.KeyspaceNotFoundException;
import com.dtsx.astra.sdk.utils.Assert;

import java.util.Set;

/**
 * Delegate Operation to work on Keyspaces
 */
public class DbKeyspacesClient extends AbstractApiClient  {

    /**
     * unique db identifier.
     */
    private final Database db;

    /**
     * Constructor.
     *
     * @param token
     *      token
     * @param databaseId
     *      databaseId
     */
    public DbKeyspacesClient(String token, String databaseId) {
        super(token);
        this.db = new DatabaseClient(token, databaseId).get();
    }

    /**
     * Find all keyspace in current DB.
     *
     * @return
     */
    public Set<String> findAll() {
        return db.getInfo().getKeyspaces();
    }

    /**
     * Evaluate if a keyspace exists.
     *
     * @param keyspace
     *      keyspace identifier
     * @return
     *      if keyspace exists
     */
    public boolean exist(String keyspace) {
        return findAll().contains(keyspace);
    }

    /**
     * Create a new keyspace in a DB.
     *
     * @param keyspace
     *         keyspace name to create
     */
    public void create(String keyspace) {
        Assert.hasLength(keyspace, "keyspace");
        if (db.getInfo().getKeyspaces().contains(keyspace)) {
            throw new KeyspaceAlreadyExistException(keyspace, db.getInfo().getName());
        }
        getHttpClient().POST(getEndpointKeyspace(keyspace), getToken());
    }

    /**
     * Delete a keyspace from db.
     *
     * @param keyspace
     *      current keyspace
     */
    public void delete(String keyspace) {
        Assert.hasLength(keyspace, "keyspace");
        if (!db.getInfo().getKeyspaces().contains(keyspace)) {
            throw new KeyspaceNotFoundException(db.getInfo().getName(), keyspace);
        }
        getHttpClient().DELETE(getEndpointKeyspace(keyspace), getToken());
    }

    /**
     * Endpoint to access keyspace. (static).
     *
     * @param keyspaceName
     *      name of keyspace
     * @return endpoint
     */
    public String getEndpointKeyspace(String keyspaceName) {
        return DatabaseClient.getEndpointDatabase(db.getId()) + "/keyspaces/" + keyspaceName;
    }

}
