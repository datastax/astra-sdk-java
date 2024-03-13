package com.datastax.astra.db;

import com.datastax.astra.devops.utils.ApiLocator;
import com.datastax.astra.devops.utils.AstraEnvironment;
import io.stargate.sdk.utils.Assert;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Create an endpoint to connect to a database.
 */
@Data @NoArgsConstructor
public class AstraDBEndpoint {

    /**
     * Current Environment
     */
    AstraEnvironment env;

    /**
     * Database Identifier
     */
    UUID databaseId;

    /**
     * Database Region
     */
    String databaseRegion;

    /**
     * Parse an endpoint URL to know information on the DB.
     *
     * @param endpointUrl
     *      endpoint URL copy from UI
     * @return
     *      astra db endpoint parsed
     */
    public static AstraDBEndpoint parse(String endpointUrl) {
        Assert.notNull(endpointUrl, "endpoint");
        AstraDBEndpoint endpoint = new AstraDBEndpoint();
        String tmpUrl;
        if (endpointUrl.contains(AstraEnvironment.PROD.getAppsSuffix())) {
            endpoint.env = AstraEnvironment.PROD;
            tmpUrl= endpointUrl.replaceAll(AstraEnvironment.PROD.getAppsSuffix(), "");
        } else if (endpointUrl.contains(AstraEnvironment.TEST.getAppsSuffix())) {
            endpoint.env = AstraEnvironment.TEST;
            tmpUrl= endpointUrl.replaceAll(AstraEnvironment.TEST.getAppsSuffix(), "");
        } else if (endpointUrl.contains(AstraEnvironment.DEV.getAppsSuffix())) {
            endpoint.env = AstraEnvironment.DEV;
            tmpUrl= endpointUrl.replaceAll(AstraEnvironment.DEV.getAppsSuffix(), "");
        } else {
            throw new IllegalArgumentException("Unable to detect environment from endpoint");
        }
        tmpUrl = tmpUrl.replaceAll("https://", "");
        endpoint.databaseId = UUID.fromString(tmpUrl.substring(0,36));
        endpoint.databaseRegion = tmpUrl.substring(37);
        return endpoint;
    }

    /**
     * Constructor with chunk of the URL.
     *
     * @param databaseId
     *      database identifier
     * @param databaseRegion
     *      database region
     * @param env
     *      environment
     */
    public AstraDBEndpoint(UUID databaseId, String databaseRegion, AstraEnvironment env) {
        this.databaseId     = databaseId;
        this.databaseRegion = databaseRegion;
        this.env = env;
    }

    /**
     * Return the endpoint URL based on the chunks.
     *
     * @return
     *      endpoint URL.
     */
    public String getApiEndPoint() {
        return ApiLocator.getApiJsonEndpoint(env, databaseId.toString(), databaseRegion);
    }

    /**
     * Return the endpoint URL based on the chunks.
     *
     * @return
     *      endpoint URL.
     */
    public String getOriginalEndPoint() {
        return getApiEndPoint().replaceAll("/api/json", "");
    }

}
