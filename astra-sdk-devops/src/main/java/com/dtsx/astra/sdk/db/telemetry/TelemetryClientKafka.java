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
public class TelemetryClientKafka {

    /** Logger for our Client. */
    private static final Logger LOGGER = LoggerFactory.getLogger(TelemetryClientKafka.class);

    /** key kafka. */
    private static final String KEY_KAFKA = "kafka";

    /** Wrapper handling header and error management as a singleton. */
    private final HttpClientWrapper http = HttpClientWrapper.getInstance();

    /** Load Database responses. */
    private static final TypeReference<Map<String, KafkaTelemetryRequest>> RESPONSE_KAFKA =
            new TypeReference<Map<String, KafkaTelemetryRequest>>(){};

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
    public TelemetryClientKafka(String token, String telemetryEndpoint) {
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
    public ApiResponseHttp setup(KafkaTelemetryRequest ktr) {
        Map<String, KafkaTelemetryRequest> bodyMap = new HashMap<>();
        bodyMap.put(KEY_KAFKA, ktr);
        return http.POST(telemetryEndpoint, token, JsonUtils.mapAsJson(bodyMap));
    }

    /**
     * Retrieve Remote Telemetry configuration
     * https://docs.datastax.com/en/astra/docs/_attachments/devopsv2.html#operation/getTelemetryConfig
     * @return
     *      telemetry request
     */
    public Optional<KafkaTelemetryRequest> find() {
        ApiResponseHttp res = http.GET(telemetryEndpoint, token);
        try{
            if (res.getCode() == HttpURLConnection.HTTP_OK) {
                return Optional.ofNullable(JsonUtils
                        .unmarshallType(res.getBody(), RESPONSE_KAFKA).get(KEY_KAFKA));
            }
        } catch(Exception e) {
            LOGGER.warn("Cannot read telemetry configuration for kafka", e);
        }
        return Optional.empty();
    }

}
