package com.dtsx.astra.sdk.db.telemetry;

import com.dtsx.astra.sdk.utils.HttpClientWrapper;
import com.dtsx.astra.sdk.utils.ApiResponseHttp;
import com.dtsx.astra.sdk.utils.Assert;
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
 */
public class TelemetryClientPrometheus {

    /** Logger for our Client. */
    private static final Logger LOGGER = LoggerFactory.getLogger(TelemetryClientPrometheus.class);

    /** Wrapper handling header and error management as a singleton. */
    private final HttpClientWrapper http = HttpClientWrapper.getInstance();

    /** Load Database responses. */
    private static final TypeReference<Map<String, PrometheusTelemetryRequest>> RESPONSE_PROMETHEUS =
            new TypeReference<Map<String, PrometheusTelemetryRequest>>(){};

    /** key prometheus. */
    private static final String KEY_PROMETHEUS = "prometheus_remote";

    /** unique db identifier. */
    private final String token;

    /** Reference to upper resource. */
    private final String telemetryEndpoint;

    /**
     * Default constructor.
     *
     * @param token
     *          token client
     * @param telemetryEndpoint
     *          endpoint
     */
    public TelemetryClientPrometheus(String token, String telemetryEndpoint) {
        Assert.notNull(token,"databasesClient");
        Assert.hasLength(telemetryEndpoint, "telemetryEndpoint");
        this.token = token;
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
    public ApiResponseHttp setup(PrometheusTelemetryRequest ktr) {
        Map<String, PrometheusTelemetryRequest> bodyMap = new HashMap<>();
        bodyMap.put(KEY_PROMETHEUS, ktr);
        return http.POST(telemetryEndpoint, token, JsonUtils.mapAsJson(bodyMap));
    }

    /**
     * Retrieve Remote Telemetry configuration
     * https://docs.datastax.com/en/astra/docs/_attachments/devopsv2.html#operation/getTelemetryConfig
     * @return
     *      telemetry request
     */
    public Optional<PrometheusTelemetryRequest> find() {
        ApiResponseHttp res = http.GET(telemetryEndpoint, token);
        try{
            if (res.getCode() == HttpURLConnection.HTTP_OK) {
                return Optional.ofNullable(JsonUtils
                        .unmarshallType(res.getBody(), RESPONSE_PROMETHEUS).get(KEY_PROMETHEUS));
            }
        } catch(Exception e) {
            LOGGER.warn("Cannot read telemetry configuration for kafka", e);
        }
        return Optional.empty();
    }

}
