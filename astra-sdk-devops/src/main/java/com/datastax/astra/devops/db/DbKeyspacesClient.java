package com.datastax.astra.devops.db;

import com.datastax.astra.devops.db.exception.KeyspaceAlreadyExistException;
import com.datastax.astra.devops.db.exception.KeyspaceNotFoundException;
import com.datastax.astra.devops.utils.Assert;
import com.datastax.astra.devops.AbstractApiClient;
import com.datastax.astra.devops.db.domain.Database;
import com.datastax.astra.devops.utils.ApiLocator;
import com.datastax.astra.devops.utils.AstraEnvironment;

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
     * As immutable object use builder to initiate the object.
     *
     * @param token
     *      authenticated token
     * @param databaseId
     *      database identifier
     */
    public DbKeyspacesClient(String token, String databaseId) {
        this(token, AstraEnvironment.PROD, databaseId);
    }

    /**
     * As immutable object use builder to initiate the object.
     *
     * @param env
     *      define target environment to be used
     * @param token
     *      authenticated token
     * @param databaseId
     *      database identifier
     */
    public DbKeyspacesClient(String token, AstraEnvironment env, String databaseId) {
        super(token, env);
        Assert.hasLength(databaseId, "databaseId");
        this.db = new DbOpsClient(token, env, databaseId).get();
    }

    /**
     * Find all keyspace in current DB.
     *
     * @return
     *      all keyspace names
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
        return ApiLocator.getApiDevopsEndpoint(environment) + "/databases/" + db.getId() + "/keyspaces/" + keyspaceName;
    }

}
