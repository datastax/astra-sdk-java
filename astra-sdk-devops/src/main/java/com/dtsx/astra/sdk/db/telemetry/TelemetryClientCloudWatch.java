package com.dtsx.astra.sdk.db.telemetry;

import com.dtsx.astra.sdk.utils.HttpClientWrapper;
import com.dtsx.astra.sdk.utils.ApiResponseHttp;
import com.dtsx.astra.sdk.utils.Assert;
import com.dtsx.astra.sdk.utils.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.HttpURLConnection;
import java.util.Optional;

/**
 * Kafka Client.
 */
public class TelemetryClientCloudWatch {

    /** Logger for our Client. */
    private static final Logger LOGGER = LoggerFactory.getLogger(TelemetryClientCloudWatch.class);

    /** Wrapper handling header and error management as a singleton. */
    private final HttpClientWrapper http = HttpClientWrapper.getInstance();

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
    public TelemetryClientCloudWatch(String token, String telemetryEndpoint) {
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
    public ApiResponseHttp setup(CloudWatchTelemetryRequest ktr) {
        return http.POST(telemetryEndpoint, token, JsonUtils.marshall(ktr));
    }

    /**
     * Retrieve Remote Telemetry configuration
     * https://docs.datastax.com/en/astra/docs/_attachments/devopsv2.html#operation/getTelemetryConfig
     *
     * @return
     *      telemetry request
     */
    public Optional<CloudWatchTelemetryRequest> find() {
        ApiResponseHttp res = http.GET(telemetryEndpoint, token);
        try{
            if (res.getCode() == HttpURLConnection.HTTP_OK) {
                return Optional.of(JsonUtils.unmarshallBean(res.getBody(), CloudWatchTelemetryRequest.class));
            }
        } catch(Exception e) {
            LOGGER.warn("Cannot read telemetry configuration for cloud watch", e);
        }
        return Optional.empty();
    }

}
