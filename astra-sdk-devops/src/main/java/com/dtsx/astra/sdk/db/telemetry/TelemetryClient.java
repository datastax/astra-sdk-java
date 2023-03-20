package com.dtsx.astra.sdk.db.telemetry;

import com.dtsx.astra.sdk.utils.HttpClientWrapper;
import com.dtsx.astra.sdk.db.DatabaseClient;
import com.dtsx.astra.sdk.utils.ApiResponseHttp;
import com.dtsx.astra.sdk.utils.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Setup Database Telemetry.
 */
public class TelemetryClient {

    /** Wrapper handling header and error management as a singleton. */
    private final HttpClientWrapper http = HttpClientWrapper.getInstance();

    /** Logger for our Client. */
    private static final Logger LOGGER = LoggerFactory.getLogger(TelemetryClient.class);

    /** Reference to upper resource. */
    private final DatabaseClient dbClient;

    /**
     * Default constructor.
     *
     * @param databaseClient
     *          database client
     */
    public TelemetryClient(DatabaseClient databaseClient) {
        Assert.notNull(databaseClient,"databasesClient");
        this.dbClient = databaseClient;
    }

    /**
     * Retrieve Remote Telemetry configuration.
     * https://docs.datastax.com/en/astra/docs/_attachments/devopsv2.html#operation/getTelemetryConfig
     *
     * @return
     *      http response
     */
    public ApiResponseHttp find() {
        return http.GET(getEndpointTelemetry(), dbClient.getToken());
    }

    /**
     * Specialization.
     *
     * @return
     *      kafka telemetry client
     */
    public TelemetryClientKafka kafka() {
        return new TelemetryClientKafka(dbClient.getToken(), getEndpointTelemetry());
    }

    /**
     * Specialization.
     *
     * @return
     *      cloudwatch telemetry client
     */
    public TelemetryClientCloudWatch cloudWatch() {
        return new TelemetryClientCloudWatch(dbClient.getToken(), getEndpointTelemetry());
    }

    /**
     * Specialization.
     *
     * @return
     *      cloudwatch telemetry client
     */
    public TelemetryClientPrometheus prometheus() {
        return new TelemetryClientPrometheus(dbClient.getToken(), getEndpointTelemetry());
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
