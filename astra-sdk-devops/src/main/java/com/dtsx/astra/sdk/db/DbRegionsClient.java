package com.dtsx.astra.sdk.db;

import com.dtsx.astra.sdk.AbstractApiClient;
import com.dtsx.astra.sdk.utils.HttpClientWrapper;
import com.dtsx.astra.sdk.db.domain.CloudProviderType;
import com.dtsx.astra.sdk.db.domain.DatabaseRegion;
import com.dtsx.astra.sdk.db.domain.DatabaseRegionServerless;
import com.dtsx.astra.sdk.utils.ApiLocator;
import com.dtsx.astra.sdk.utils.ApiResponseHttp;
import com.dtsx.astra.sdk.utils.JsonUtils;
import com.fasterxml.jackson.core.type.TypeReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/**
 * Group operation to list db regions
 */
public class DbRegionsClient extends AbstractApiClient {

    /** Get Available Regions. */
    public static final String PATH_REGIONS = "/availableRegions";

    /** Get Available Regions. */
    public static final String PATH_REGIONS_SERVERLESS = "/regions/serverless";

    /** List of regions. */
    public static final TypeReference<List<DatabaseRegion>> TYPE_LIST_REGION =
            new TypeReference<List<DatabaseRegion>>(){};

    /**
     * As immutable object use builder to initiate the object.
     *
     * @param token
     *      authenticated token
     */
    public DbRegionsClient(String token) {
        this(token, ApiLocator.AstraEnvironment.PROD);
    }

    /**
     * As immutable object use builder to initiate the object.
     *
     * @param env
     *      define target environment to be used
     * @param token
     *      authenticated token
     */
    public DbRegionsClient(String token, ApiLocator.AstraEnvironment env) {
        super(token, env);
    }

    /**
     * Returns supported regions and availability for a given user and organization
     *
     * @return
     *      supported regions and availability
     */
    public Stream<DatabaseRegion> findAll() {
        // Invoke endpoint
        ApiResponseHttp res = HttpClientWrapper
                .getInstance()
                .GET(ApiLocator.getApiDevopsEndpoint(environment) + PATH_REGIONS, token);
        // Marshall response
        return JsonUtils.unmarshallType(res.getBody(), TYPE_LIST_REGION).stream();
    }

    /**
     * List serverless regions.
     *
     * @return
     *      serverless region
     */
    public Stream<DatabaseRegionServerless> findAllServerless() {
        // Invoke endpoint
        ApiResponseHttp res = HttpClientWrapper
                .getInstance().GET(ApiLocator.getApiDevopsEndpoint(environment) + PATH_REGIONS_SERVERLESS, token);
        // Marshall response
        return JsonUtils.unmarshallType(res.getBody(), new TypeReference<List<DatabaseRegionServerless>>(){}).stream();
    }

    /**
     * Map regions from plain list to Tier/Cloud/Region Structure.
     *
     * @return
     *      regions organized by cloud providers
     */
    public Map <String, Map<CloudProviderType,List<DatabaseRegion>>> findAllAsMap() {
        Map<String, Map<CloudProviderType,List<DatabaseRegion>>> m = new HashMap<>();
        findAll().forEach(dar -> {
            if (!m.containsKey(dar.getTier())) {
                m.put(dar.getTier(), new HashMap<>());
            }
            if (!m.get(dar.getTier()).containsKey(dar.getCloudProvider())) {
                m.get(dar.getTier()).put(dar.getCloudProvider(), new ArrayList<>());
            }
            m.get(dar.getTier()).get(dar.getCloudProvider()).add(dar);
        });
        return m;
    }

}
