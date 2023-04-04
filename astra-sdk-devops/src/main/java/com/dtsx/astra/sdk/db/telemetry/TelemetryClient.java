package com.dtsx.astra.sdk.db.telemetry;

import com.dtsx.astra.sdk.utils.HttpClientWrapper;
import com.dtsx.astra.sdk.db.DatabaseClient;
import com.dtsx.astra.sdk.utils.ApiResponseHttp;
import com.dtsx.astra.sdk.utils.Assert;

/**
 * Setup Database Telemetry.
 */
public class TelemetryClient {

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
        return HttpClientWrapper.getInstance().GET(getEndpointTelemetry(), dbClient.getToken());
    }

    /**
     * Specialization.
     *
     * @return
     *      kafka telemetry client
     */
    public SpecializedTelemetryClient<KafkaTelemetryRequest> kafka() {
        return new SpecializedTelemetryClient<KafkaTelemetryRequest>(
                dbClient.getToken(), getEndpointTelemetry(), "kafka");
    }

    /**
     * Specialization.
     *
     * @return
     *      cloudwatch telemetry client
     */
    public SpecializedTelemetryClient<CloudWatchTelemetryRequest> cloudWatch() {
        return new SpecializedTelemetryClient<CloudWatchTelemetryRequest>(
                dbClient.getToken(), getEndpointTelemetry(), "cloudwatch");
    }

    /**
     * Specialization.
     *
     * @return
     *      prometheus_remote telemetry client
     */
    public SpecializedTelemetryClient<PrometheusTelemetryRequest> prometheus() {
        return new SpecializedTelemetryClient<PrometheusTelemetryRequest>(
                dbClient.getToken(), getEndpointTelemetry(), "prometheus_remote");
    }

    /**
     * Specialization.
     *
     * @return
     *      Datadog telemetry client
     */
    public SpecializedTelemetryClient<DatadogTelemetryRequest> datadog() {
        return new SpecializedTelemetryClient<DatadogTelemetryRequest>(
                dbClient.getToken(), getEndpointTelemetry(), "Datadog");
    }

    /**
     * Specialization.
     *
     * @return
     *      splunk telemetry client
     */
    public SpecializedTelemetryClient<SplunkTelemetryRequest> splunk() {
        return new SpecializedTelemetryClient<SplunkTelemetryRequest>(
                dbClient.getToken(), getEndpointTelemetry(), "splunk");
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
