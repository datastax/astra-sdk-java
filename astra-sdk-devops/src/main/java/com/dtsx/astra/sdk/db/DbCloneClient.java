package com.dtsx.astra.sdk.db;

import com.dtsx.astra.sdk.AbstractApiClient;
import com.dtsx.astra.sdk.db.domain.Database;
import com.dtsx.astra.sdk.db.domain.DatabaseCloneStatus;
import com.dtsx.astra.sdk.utils.ApiLocator;
import com.dtsx.astra.sdk.utils.ApiResponseHttp;
import com.dtsx.astra.sdk.utils.Assert;
import com.dtsx.astra.sdk.utils.AstraEnvironment;
import com.dtsx.astra.sdk.utils.JsonUtils;

import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.stream.Stream;

/**
 * Delegate operation on DB cloning
 */
public class DbCloneClient extends AbstractApiClient {

    /** Clone path component, part I. */
    public static final String PATH_CLONE_1 = "/databases/";

    /** Clone path component, part II. */
    public static final String PATH_CLONE_2 = "/cloneFrom/";

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
    public DbCloneClient(String token, String databaseId) {
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
    public DbCloneClient(String token, AstraEnvironment env, String databaseId) {
        super(token, env);
        Assert.hasLength(databaseId, "databaseId");
        this.db = new DbOpsClient(token, env, databaseId).get();
    }

    /** {@inheritDoc} */
    @Override
    public String getServiceName() {
        return "db.clone";
    }

    /**
     * Clone database from a source database using a snapshot.
     *
     * @param sourceDbId
     *         ID of the source database
     * @param databaseSnapshotId
     *         ID of the snapshot to clone from
     * @return clone status
     */
    public DatabaseCloneStatus cloneFrom(String sourceDbId, String databaseSnapshotId) {
        return cloneFrom(sourceDbId, databaseSnapshotId, null);
    }

    /**
     * Clone database from a source database using a snapshot.
     *
     * @param sourceDbId
     *         ID of the source database
     * @param databaseSnapshotId
     *         ID of the snapshot to clone from
     * @param sourceRegion
     *         Source region (optional)
     * @return clone status
     */
    public DatabaseCloneStatus cloneFrom(String sourceDbId, String databaseSnapshotId, String sourceRegion) {
        Assert.hasLength(sourceDbId, "sourceDbId");
        Assert.hasLength(databaseSnapshotId, "databaseSnapshotId");
        
        // Build URL with query parameters
        String url = getEndpointCloneFrom(sourceDbId) +
                     "?snapshotID=" + URLEncoder.encode(databaseSnapshotId, StandardCharsets.UTF_8);
        
        if (sourceRegion != null && !sourceRegion.isEmpty()) {
            url += "&sourceRegion=" + URLEncoder.encode(sourceRegion, StandardCharsets.UTF_8);
        }
        
        // Fire POST request
        ApiResponseHttp res = POST(url, getOperationName("cloneFrom"));
        
        // Parse and return response
        return JsonUtils.unmarshallBean(res.getBody(), DatabaseCloneStatus.class);
    }

    /**
     * Endpoint to initiate DB clone
     *
     * @param sourceDbId
     *         ID of the source database
     * @return database endpoint
     */
    private String getEndpointCloneFrom(String sourceDbId) {
        return ApiLocator.getApiDevopsEndpoint(environment) + PATH_CLONE_1 + db.getId() + PATH_CLONE_2 + sourceDbId;
    }


}
