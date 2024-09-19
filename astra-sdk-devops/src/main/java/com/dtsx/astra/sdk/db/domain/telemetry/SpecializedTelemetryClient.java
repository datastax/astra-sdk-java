package com.dtsx.astra.sdk.db.domain.telemetry;

import com.dtsx.astra.sdk.utils.ApiResponseHttp;
import com.dtsx.astra.sdk.utils.Assert;
import com.dtsx.astra.sdk.utils.HttpClientWrapper;
import com.dtsx.astra.sdk.utils.JsonUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Kafka Client.
 *
 * @param <T>
 *          type of data
 */
public class SpecializedTelemetryClient<T> {

    /** Logger for our Client. */
    private static final Logger LOGGER = LoggerFactory.getLogger(SpecializedTelemetryClient.class);

    /** Load Database responses. */
    private final TypeReference<Map<String, T>> RESPONSE = new TypeReference<Map<String, T>>(){};

    /** unique db identifier. */
    private final String token;

    /** unique db identifier. */
    private final String key;

    /** Reference to upper resource. */
    private final String telemetryEndpoint;

    /**
     * Default constructor.
     *
     * @param token
     *          token client
     * @param telemetryEndpoint
     *          endpoint
     * @param key
     *      key for target system
     */
    public SpecializedTelemetryClient(String token, String telemetryEndpoint, String key) {
        Assert.notNull(token,"databasesClient");
        Assert.hasLength(telemetryEndpoint, "telemetryEndpoint");
        this.token = token;
        this.key   = key;
        this.telemetryEndpoint = telemetryEndpoint;
    }

    /**
     * Configure Astra Remote Telemetry.
     *
     * @param ktr
     *      config request
     * @return
     *      http response
     */
    public ApiResponseHttp setup(T ktr) {
        Map<String, T> bodyMap = new HashMap<>();
        bodyMap.put(key, ktr);
        return HttpClientWrapper.getInstance("db.telemetry.setup").POST(telemetryEndpoint, token, JsonUtils.mapAsJson(bodyMap));
    }

    /**
     * Retrieve Remote Telemetry configuration
     * <a href="https://docs.datastax.com/en/astra/docs/_attachments/devopsv2.html#operation/getTelemetryConfig">...</a>
     * @return
     *      telemetry request
     */
    public Optional<T> find() {
        ApiResponseHttp res =  HttpClientWrapper.getInstance("db.telemetry.find").GET(telemetryEndpoint, token);
        try{
            if (res.getCode() == HttpURLConnection.HTTP_OK) {
                return Optional.ofNullable(JsonUtils
                        .unmarshallType(res.getBody(), RESPONSE).get(key));
            }
        } catch(Exception e) {
            LOGGER.warn("Cannot read telemetry configuration for " + key, e);
        }
        return Optional.empty();
    }

}
