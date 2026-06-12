package com.dtsx.astra.sdk.db;

import com.dtsx.astra.sdk.AbstractApiClient;
import com.dtsx.astra.sdk.db.domain.Database;
import com.dtsx.astra.sdk.db.domain.DatabaseSnapshot;
import com.dtsx.astra.sdk.db.domain.DatabaseSnapshotsResponse;
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
 * Delegate operation on snapshots
 */
public class DbSnapshotsClient extends AbstractApiClient {

    /** Get Available Regions. */
    public static final String PATH_SNAPSHOTS_1 = "/databases/";

    /** Get Available Regions. */
    public static final String PATH_SNAPSHOTS_2 = "/snapshots";

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
    public DbSnapshotsClient(String token, String databaseId) {
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
    public DbSnapshotsClient(String token, AstraEnvironment env, String databaseId) {
        super(token, env);
        Assert.hasLength(databaseId, "databaseId");
        this.db = new DbOpsClient(token, env, databaseId).get();
    }

    /** {@inheritDoc} */
    @Override
    public String getServiceName() {
        return "db.snapshots";
    }

    /**
     * Get snapshots with filters.
     *
     * @param from
     *         start time (ISO 8601 format)
     * @param to
     *         end time (ISO 8601 format)
     * @param region
     *         region name
     * @return list of snapshots.
     */
    public Stream<DatabaseSnapshot> find(String from, String to, String region) {
        // Build URL with query parameters
        StringBuilder url = new StringBuilder(getEndpointSnapshots());
        boolean hasParams = false;
        
        if (region != null) {
            url.append("?sourceRegion=").append(URLEncoder.encode(region, StandardCharsets.UTF_8));
            hasParams = true;
        }
        
        if (from != null) {
            url.append(hasParams ? "&" : "?").append("from=").append(URLEncoder.encode(from, StandardCharsets.UTF_8));
            hasParams = true;
        }
        
        if (to != null) {
            url.append(hasParams ? "&" : "?").append("to=").append(URLEncoder.encode(to, StandardCharsets.UTF_8));
        }
        
        // Invoke endpoint
        ApiResponseHttp res = GET(url.toString(), getOperationName("find"));
        
        // Handle response
        if (HttpURLConnection.HTTP_NOT_FOUND == res.getCode()) {
            return Stream.of();
        } else {
            DatabaseSnapshotsResponse response = JsonUtils.unmarshallBean(res.getBody(), DatabaseSnapshotsResponse.class);
            return response.getSnapshots() != null ? response.getSnapshots().stream() : Stream.of();
        }
    }

    /**
     * Get all snapshots.
     *
     * @return list of snapshots.
     */
    public Stream<DatabaseSnapshot> findAll() {
        return find(null, null, null);
    }

    // TODO: add other overloads here

    /**
     * Endpoint to access snapshots of a db
     *
     * @return database endpoint
     */
    private String getEndpointSnapshots() {
        return ApiLocator.getApiDevopsEndpoint(environment) + PATH_SNAPSHOTS_1 + db.getId() + PATH_SNAPSHOTS_2;
    }

}
