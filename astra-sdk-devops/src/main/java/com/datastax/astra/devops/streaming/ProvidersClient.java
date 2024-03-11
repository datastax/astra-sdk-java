package com.datastax.astra.devops.streaming;

import com.datastax.astra.devops.AbstractApiClient;
import com.datastax.astra.devops.utils.ApiLocator;
import com.datastax.astra.devops.utils.AstraEnvironment;
import com.datastax.astra.devops.utils.JsonUtils;

import java.util.List;
import java.util.Map;

/**
 * Work with providers.
 */
public class ProvidersClient extends AbstractApiClient {

    /**
     * As immutable object use builder to initiate the object.
     *
     * @param token
     *      authenticated token
     */
    public ProvidersClient(String token) {
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
    public ProvidersClient(String token, AstraEnvironment env) {
        super(token, env);
    }

    /**
     * Operations on providers.
     *
     * @return
     *      list of cloud providers and regions
     */
    @SuppressWarnings("unchecked")
    public Map<String, List<String>> findAll() {
        return JsonUtils.unmarshallBean(
                GET(getApiDevopsEndpointProviders())
                .getBody(), Map.class);
    }

    /**
     * Endpoint to access schema for namespace.
     *
     * @return
     *      endpoint
     */
    public String getApiDevopsEndpointProviders() {
        return ApiLocator.getApiDevopsEndpoint(environment) + "/streaming" +  "/providers";
    }

}
