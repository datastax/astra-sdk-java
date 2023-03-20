package com.dtsx.astra.sdk.streaming;

import com.dtsx.astra.sdk.AbstractApiClient;
import com.dtsx.astra.sdk.utils.JsonUtils;

import java.util.List;
import java.util.Map;

/**
 * Work with providers.
 */
public class ProvidersClient extends AbstractApiClient {

    /**
     * Constructor.
     *
     * @param token
     *      current token.
     */
    public ProvidersClient(String token) {
        super(token);
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
    public static String getApiDevopsEndpointProviders() {
        return AstraStreamingClient.getApiDevopsEndpointStreaming() +  "/providers";
    }

}
