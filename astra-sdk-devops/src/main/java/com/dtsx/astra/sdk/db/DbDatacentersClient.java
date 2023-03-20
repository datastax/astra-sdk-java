package com.dtsx.astra.sdk.db;

import com.dtsx.astra.sdk.AbstractApiClient;
import com.dtsx.astra.sdk.db.domain.CloudProviderType;
import com.dtsx.astra.sdk.db.domain.Database;
import com.dtsx.astra.sdk.db.domain.DatabaseRegionCreationRequest;
import com.dtsx.astra.sdk.db.domain.Datacenter;
import com.dtsx.astra.sdk.db.exception.RegionAlreadyExistException;
import com.dtsx.astra.sdk.db.exception.RegionNotFoundException;
import com.dtsx.astra.sdk.utils.ApiResponseHttp;
import com.dtsx.astra.sdk.utils.Assert;
import com.dtsx.astra.sdk.utils.JsonUtils;
import com.fasterxml.jackson.core.type.TypeReference;

import java.net.HttpURLConnection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Delegate operation on region/datacenters
 */
public class DbDatacentersClient extends AbstractApiClient {

    /**
     * Returned type.
     */
    private static final TypeReference<List<Datacenter>> DATACENTER_LIST =
            new TypeReference<List<Datacenter>>() {};

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
    public DbDatacentersClient(String token, String databaseId) {
        super(token);
        Assert.hasLength(databaseId, "databaseId");
        // Test Db exists
        this.db = new DatabaseClient(token, databaseId).get();
    }

    /**
     * Get Datacenters details for a region
     *
     * @return list of datacenters.
     */
    public Stream<Datacenter> findAll() {
        ApiResponseHttp res = GET(getEndpointRegions());
        if (HttpURLConnection.HTTP_NOT_FOUND == res.getCode()) {
            return Stream.of();
        } else {
            return JsonUtils.unmarshallType(res.getBody(), DATACENTER_LIST).stream();
        }
    }

    /**
     * Get a region from its name.
     *
     * @param regionName
     *         region name
     * @return datacenter if exists
     * i
     */
    public Optional<Datacenter> findByRegionName(String regionName) {
        Assert.hasLength(regionName, "regionName");
        return findAll().filter(dc -> regionName.equals(dc.getRegion())).findFirst();
    }

    /**
     * Evaluate if a database exists using the findById method.
     *
     * @param regionName
     *         region name
     */
    public boolean exist(String regionName) {
        return findByRegionName(regionName).isPresent();
    }

    /**
     * Create a Region.
     *
     * @param tier
     *         tier for the db
     * @param cloudProvider
     *         Cloud provider to add a region
     * @param regionName
     *         name of the region
     *         <p>
     *         https://docs.datastax.com/en/astra/docs/_attachments/devopsv2.html#operation/addDatacenters
     */
    public void create(String tier, CloudProviderType cloudProvider, String regionName) {
        Assert.hasLength(tier, "tier");
        Assert.notNull(cloudProvider, "cloudProvider");
        Assert.hasLength(regionName, "regionName");
        if (findByRegionName(regionName).isPresent()) {
            throw new RegionAlreadyExistException(db.getId(), regionName);
        }
        DatabaseRegionCreationRequest req = new DatabaseRegionCreationRequest(tier, cloudProvider.getCode(), regionName);
        String body = JsonUtils.marshall(Collections.singletonList(req));
        ApiResponseHttp res = POST(getEndpointRegions(), body);
        if (res.getCode() != HttpURLConnection.HTTP_CREATED) {
            throw new IllegalStateException("Cannot Add Region: " + res.getBody());
        }
    }

    /**
     * Delete a region from its name.
     *
     * @param regionName
     *         name of the region
     *         <p>
     *         https://docs.datastax.com/en/astra/docs/_attachments/devopsv2.html#operation/terminateDatacenter
     */
    public void delete(String regionName) {
        Optional<Datacenter> optDc = findByRegionName(regionName);
        if (!optDc.isPresent()) {
            throw new RegionNotFoundException(db.getId(), regionName);
        }
        // Invoke Http endpoint
        ApiResponseHttp res = POST(getEndpointRegions() + "/" + optDc.get().getId() + "/terminate");
        // Check response code
        assertHttpCodeAccepted(res, "deleteRegion", db.getId());
    }

    /**
     * Endpoint to access datacenters of a db
     *
     * @return database endpoint
     */
    private String getEndpointRegions() {
        return DatabaseClient.getEndpointDatabase(db.getId()) + "/datacenters";
    }

}
