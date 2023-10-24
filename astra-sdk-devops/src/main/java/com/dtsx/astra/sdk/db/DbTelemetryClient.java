package com.dtsx.astra.sdk.db;

import com.dtsx.astra.sdk.AbstractApiClient;
import com.dtsx.astra.sdk.db.domain.Database;
import com.dtsx.astra.sdk.db.domain.telemetry.CloudWatchTelemetryRequest;
import com.dtsx.astra.sdk.db.domain.telemetry.DatadogTelemetryRequest;
import com.dtsx.astra.sdk.db.domain.telemetry.KafkaTelemetryRequest;
import com.dtsx.astra.sdk.db.domain.telemetry.PrometheusTelemetryRequest;
import com.dtsx.astra.sdk.db.domain.telemetry.SpecializedTelemetryClient;
import com.dtsx.astra.sdk.db.domain.telemetry.SplunkTelemetryRequest;
import com.dtsx.astra.sdk.utils.ApiLocator;
import com.dtsx.astra.sdk.utils.ApiResponseHttp;
import com.dtsx.astra.sdk.utils.Assert;
import com.dtsx.astra.sdk.utils.AstraEnvironment;
import com.dtsx.astra.sdk.utils.HttpClientWrapper;

/**
 * Setup Database Telemetry.
 */
public class DbTelemetryClient extends AbstractApiClient {

    /**
     * unique db identifier.
     */
    private final Database db;

    /**
     * As immutable object use builder to initiate the object.
     *
     * @param token
     *      authenticated token
     * @param databaseId
     *      database identifier
     */
    public DbTelemetryClient(String token, String databaseId) {
        this(token, AstraEnvironment.PROD, databaseId);
    }

    /**
     * As immutable object use builder to initiate the object.
     *
     * @param env
     *      define target environment to be used
     * @param token
     *      authenticated token
     * @param databaseId
     *      database identifier
     */
    public DbTelemetryClient(String token, AstraEnvironment env, String databaseId) {
        super(token, env);
        Assert.hasLength(databaseId, "databaseId");
        this.db = new DbOpsClient(token, env, databaseId).get();
    }

    /**
     * Retrieve Remote Telemetry configuration.
     * https://docs.datastax.com/en/astra/docs/_attachments/devopsv2.html#operation/getTelemetryConfig
     *
     * @return
     *      http response
     */
    public ApiResponseHttp find() {
        return HttpClientWrapper.getInstance().GET(getEndpointTelemetry(), token);
    }

    /**
     * Specialization.
     *
     * @return
     *      kafka telemetry client
     */
    public SpecializedTelemetryClient<KafkaTelemetryRequest> kafka() {
        return new SpecializedTelemetryClient<KafkaTelemetryRequest>(token,
                getEndpointTelemetry(), "kafka");
    }

    /**
     * Specialization.
     *
     * @return
     *      cloudwatch telemetry client
     */
    public SpecializedTelemetryClient<CloudWatchTelemetryRequest> cloudWatch() {
        return new SpecializedTelemetryClient<CloudWatchTelemetryRequest>(token,
                getEndpointTelemetry(), "cloudwatch");
    }

    /**
     * Specialization.
     *
     * @return
     *      prometheus_remote telemetry client
     */
    public SpecializedTelemetryClient<PrometheusTelemetryRequest> prometheus() {
        return new SpecializedTelemetryClient<PrometheusTelemetryRequest>(token,
                getEndpointTelemetry(), "prometheus_remote");
    }

    /**
     * Specialization.
     *
     * @return
     *      Datadog telemetry client
     */
    public SpecializedTelemetryClient<DatadogTelemetryRequest> datadog() {
        return new SpecializedTelemetryClient<DatadogTelemetryRequest>(token,
                getEndpointTelemetry(), "Datadog");
    }

    /**
     * Specialization.
     *
     * @return
     *      splunk telemetry client
     */
    public SpecializedTelemetryClient<SplunkTelemetryRequest> splunk() {
        return new SpecializedTelemetryClient<SplunkTelemetryRequest>(token, getEndpointTelemetry(), "splunk");
    }

    /**
     * Accessing telemetry endpoint.
     *
     * @return
     *      telemetry endpoint
     */
    public String getEndpointTelemetry() {
        return ApiLocator.getApiDevopsEndpoint(environment) + "/databases/" + db.getId() + "/telemetry/metrics";
    }

}
