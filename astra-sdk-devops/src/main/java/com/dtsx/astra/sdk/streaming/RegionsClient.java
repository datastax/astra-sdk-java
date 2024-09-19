package com.dtsx.astra.sdk.streaming;

import com.dtsx.astra.sdk.AbstractApiClient;
import com.dtsx.astra.sdk.streaming.domain.StreamingRegion;
import com.dtsx.astra.sdk.utils.ApiLocator;
import com.dtsx.astra.sdk.utils.ApiResponse;
import com.dtsx.astra.sdk.utils.AstraEnvironment;
import com.dtsx.astra.sdk.utils.JsonUtils;
import com.fasterxml.jackson.core.type.TypeReference;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/**
 * Group operation on streaming regions
 */
public class RegionsClient extends AbstractApiClient {

    /** Marshalling beans data -> organization -> availableServerlessRegions */
    private static final TypeReference<ApiResponse<Map<String, Map<String, List<StreamingRegion>>>>> TYPE_LIST_REGIONS =
            new TypeReference<ApiResponse<Map<String, Map<String, List<StreamingRegion>>>>>(){};

    /** json key. */
    private static final String JSON_ORGANIZATION = "organization";

    /** json key. */
    private static final String JSON_SERVERLESS_REGIONS = "availableServerlessRegions";

    /**
     * As immutable object use builder to initiate the object.
     *
     * @param token
     *      authenticated token
     */
    public RegionsClient(String token) {
        this(token, AstraEnvironment.PROD);
    }

    /**
     * As immutable object use builder to initiate the object.
     *
     * @param env
     *      define target environment to be used
     * @param token
     *      authenticated token
     */
    public RegionsClient(String token, AstraEnvironment env) {
        super(token, env);
    }

    /** {@inheritDoc} */
    @Override
    public String getServiceName() {
        return "streaming.regions";
    }

    /**
     * Get available serverless for Streaming.
     *
     * @return
     *      serverless regions
     */
    public Stream<StreamingRegion> findAllServerless() {
        // Invoke api
        Map<String, Map<String, List<StreamingRegion>>> res = JsonUtils
                .unmarshallType(GET(getApiDevopsEndpointRegionsServerless(), getOperationName("findServerless"))
                .getBody(), TYPE_LIST_REGIONS).getData();
        if (null != res &&
                null != res.get(JSON_ORGANIZATION) &&
                null != res.get(JSON_ORGANIZATION).get(JSON_SERVERLESS_REGIONS)) {
            return res.get(JSON_ORGANIZATION).get(JSON_SERVERLESS_REGIONS).stream();
        }
        return Stream.of();
    }

    /**
     * Endpoint to access schema for namespace.
     *
     * @return
     *      endpoint
     */
    public String getApiDevopsEndpointRegionsServerless() {
        return ApiLocator.getApiDevopsEndpoint(environment) + "/streaming" + "/serverless-regions";
    }


}
