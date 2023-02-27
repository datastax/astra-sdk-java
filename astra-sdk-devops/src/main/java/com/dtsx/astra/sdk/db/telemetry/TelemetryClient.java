package com.dtsx.astra.sdk.db.telemetry;

import com.dtsx.astra.sdk.HttpClientWrapper;
import com.dtsx.astra.sdk.db.DatabaseClient;
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
 * Setup Database Telemetry.
 */
public class TelemetryClient {

    /** unique db identifier. */
    private final String databaseId;

    /** Wrapper handling header and error management as a singleton. */
    private final HttpClientWrapper http = HttpClientWrapper.getInstance();

    /** Logger for our Client. */
    private static final Logger LOGGER = LoggerFactory.getLogger(TelemetryClient.class);

    /** Load Database responses. */
    private static final TypeReference<Map<String, KafkaTelemetryRequest>> RESPONSE_KAFKA =
            new TypeReference<Map<String, KafkaTelemetryRequest>>(){};

    /** Load Database responses. */
    private static final TypeReference<Map<String, PrometheusTelemetryRequest>> RESPONSE_PROMETHEUS =
            new TypeReference<Map<String, PrometheusTelemetryRequest>>(){};

    /** key kafka. */
    private static final String KEY_KAFKA = "kafka";

    /** key prometheus. */
    private static final String KEY_PROMETHEUS = "prometheus_remote";

    /** Reference to upper resource. */
    private final DatabaseClient dbClient;

    /**
     * Default constructor.
     *
     * @param databaseClient
     *          database client
     * @param databaseId
     *          unique database identifier
     */
    public TelemetryClient(DatabaseClient databaseClient, String databaseId) {
        Assert.notNull(databaseClient,"databasesClient");
        Assert.hasLength(databaseId, "databaseId");
        this.databaseId = databaseId;
        this.dbClient = databaseClient;
    }

    /**
     * Configure Astra Remote Telemetry.
     *
     * @param ktr
     *      config request
     * @return
     *      http response
     */
    public ApiResponseHttp setupKafka(KafkaTelemetryRequest ktr) {
        Map<String, KafkaTelemetryRequest> bodyMap = new HashMap<>();
        bodyMap.put(KEY_KAFKA, ktr);
        return http.POST(getEndpointTelemetry(), dbClient.getToken(), JsonUtils.mapAsJson(bodyMap));
    }

    /**
     * Configure Astra Remote Telemetry.
     *
     * @param ktr
     *      config request
     * @return
     *      http response
     */
    public ApiResponseHttp setupPrometheus(PrometheusTelemetryRequest ktr) {
        Map<String, PrometheusTelemetryRequest> bodyMap = new HashMap<>();
        bodyMap.put(KEY_PROMETHEUS, ktr);
        return http.POST(getEndpointTelemetry(), dbClient.getToken(), JsonUtils.mapAsJson(bodyMap));
    }

    /**
     * Configure Astra Remote Telemetry.
     *
     * @param ktr
     *      config request
     * @return
     *      http response
     */
    public ApiResponseHttp setupCloudWatch(CloudWatchTelemetryRequest ktr) {
        return http.POST(getEndpointTelemetry(), dbClient.getToken(), JsonUtils.marshall(ktr));
    }

    /**
     * Retrieve Remote Telemetry configuration.
     * https://docs.datastax.com/en/astra/docs/_attachments/devopsv2.html#operation/getTelemetryConfig
     *
     * @return
     *      http response
     */
    public ApiResponseHttp findSetup() {
        return http.GET(getEndpointTelemetry(), dbClient.getToken());
    }

    /**
     * Retrieve Remote Telemetry configuration
     * https://docs.datastax.com/en/astra/docs/_attachments/devopsv2.html#operation/getTelemetryConfig
     *
     * @return
     *      telemetry request
     */
    public Optional<CloudWatchTelemetryRequest> findSetupCloudWatch() {
        ApiResponseHttp res = http.GET(getEndpointTelemetry(), dbClient.getToken());
        try{
            if (res.getCode() == HttpURLConnection.HTTP_OK) {
                return Optional.of(JsonUtils.unmarshallBean(res.getBody(), CloudWatchTelemetryRequest.class));
            }
        } catch(Exception e) {
            LOGGER.warn("Cannot read telemetry configuration for cloud watch", e);
        }
        return Optional.empty();
    }

    /**
     * Retrieve Remote Telemetry configuration
     * https://docs.datastax.com/en/astra/docs/_attachments/devopsv2.html#operation/getTelemetryConfig
     * @return
     *      telemetry request
     */
    public Optional<KafkaTelemetryRequest> findSetupKafka() {
        ApiResponseHttp res = http.GET(getEndpointTelemetry(), dbClient.getToken());
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

    /**
     * Retrieve Remote Telemetry configuration
     * https://docs.datastax.com/en/astra/docs/_attachments/devopsv2.html#operation/getTelemetryConfig
     * @return
     *      telemetry request
     */
    public Optional<PrometheusTelemetryRequest> findSetupPrometheus() {
        ApiResponseHttp res = http.GET(getEndpointTelemetry(), dbClient.getToken());
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

    /**
     * Accessing telemetry endpoint.
     *
     * @return
     *      telemetry endpoint
     */
    public String getEndpointTelemetry() {
        return dbClient.getEndpointDatabase() + "/telemetry/metrics";
    }

}
