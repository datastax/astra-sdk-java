package com.dtsx.astra.sdk.utils;

/**
 * Utility class to find endpoints of the Astra APIs.
 *
 * @author Cedrick LUNVEN (@clunven)
 */
public class ApiLocator {

    /** Building Astra base URL. */
    public static final String HTTPS = "https://";

    /**
     * Hide constructor.
     */
    private ApiLocator() {}

    // -- Devops --

    /**
     * Get the Devops endpoint.
     *
     * @return
     *      the devops URL.
     */
    public static String getApiDevopsEndpoint() {
        return getApiDevopsEndpoint(AstraEnvironment.PROD);
    }

    /**
     * Get the Devops endpoint.
     *
     * @param env
     *      change target environment for the API
     * @return
     *      the devops URL.
     */
    public static String getApiDevopsEndpoint(AstraEnvironment env) {
        return env.getEndPoint();
    }

    /**
     * REST and DOCUMENT endpoint for a database and region.
     *
     * @param dbId
     *      database identifier
     * @param dbRegion
     *      region identifier
     * @return
     *      the url to invoke
     */
    public static String getApiRestEndpoint(String dbId, String dbRegion) {
        return getApiRestEndpoint(AstraEnvironment.PROD, dbId, dbRegion);
    }

    /**
     * REST and DOCUMENT endpoint for a database and region.
     *
     * @param env
     *      target environment
     * @param dbId
     *      database identifier
     * @param dbRegion
     *      region identifier
     * @return
     *      the url to invoke
     */
    public static String getApiRestEndpoint(AstraEnvironment env, String dbId, String dbRegion) {
        Assert.hasLength(dbId, "dbId");
        Assert.hasLength(dbRegion, "dbRegion");
        return new StringBuilder(HTTPS)
                .append(dbId).append("-").append(dbRegion)
                .append(env.getAppsSuffix())
                .append("/api/rest")
                .toString();
    }

    /**
     * REST and DOCUMENT endpoint for a database and region.
     *
     * @param dbId
     *      database identifier
     * @param dbRegion
     *      region identifier
     * @return
     *      the url to invoke
     */
    public static String getApiJsonEndpoint(String dbId, String dbRegion) {
        return getApiJsonEndpoint(AstraEnvironment.PROD, dbId, dbRegion);
    }

    /**
     * END Point for the Json API
     *
     * @param env
     *      target environment
     * @param dbId
     *      database identifier
     * @param dbRegion
     *      region identifier
     * @return
     *      the url to invoke
     */
    public static String getApiJsonEndpoint(AstraEnvironment env, String dbId, String dbRegion) {
        Assert.hasLength(dbId, "dbId");
        Assert.hasLength(dbRegion, "dbRegion");
        return HTTPS + dbId + "-" + dbRegion + env.getAppsSuffix() + "/api/json";
    }

    /**
     * Document endpoint.
     *
     * @param dbId
     *      database identifier
     * @param dbRegion
     *      database region
     * @return
     *      endpoint
     */
    public static final String getApiDocumentEndpoint(String dbId, String dbRegion) {
        return getApiDocumentEndpoint(AstraEnvironment.PROD, dbId, dbRegion);
    }

    /**
     * Document endpoint.
     *
     * @param env
     *      target environment
     * @param dbId
     *      database identifier
     * @param dbRegion
     *      database region
     * @return
     *      endpoint
     */
    public static final String getApiDocumentEndpoint(AstraEnvironment env, String dbId, String dbRegion) {
        return getApiRestEndpoint(env, dbId, dbRegion);
    }

    /**
     * Document endpoint
     * @param dbId
     *      database identifier
     * @param dbRegion
     *      database region
     * @return
     *      endpoint
     */
    public static final String getEndpointHealthCheck(String dbId, String dbRegion) {
        return getEndpointHealthCheck(AstraEnvironment.PROD, dbId, dbRegion);
    }

    /**
     * Document endpoint.
     *
     * @param env
     *      target environment
     * @param dbId
     *      database identifier
     * @param dbRegion
     *      database region
     * @return
     *      endpoint
     */
    public static final String getEndpointHealthCheck(AstraEnvironment env, String dbId, String dbRegion) {
        return getApiRestEndpoint(env, dbId, dbRegion) + "/swagger-ui/";
    }

    /**
     * GRAPHQL endpoint for a database and region working with Schema definition
     *
     * @param dbId
     *      database identifier
     * @param dbRegion
     *      region identifier
     * @return
     *      the url to invoke
     */
    public static final String getApiGrpcEndPoint(String dbId, String dbRegion) {
        return getApiGrpcEndPoint(AstraEnvironment.PROD, dbId, dbRegion);
    }

    /**
     * GRAPHQL endpoint for a database and region working with Schema definition.
     *
     * @param env
     *      target environment
     * @param dbId
     *      database identifier
     * @param dbRegion
     *      region identifier
     * @return
     *      the url to invoke
     */
    public static final String getApiGrpcEndPoint(AstraEnvironment env, String dbId, String dbRegion) {
        Assert.hasLength(dbId, "dbId");
        Assert.hasLength(dbRegion, "dbRegion");
        return new StringBuilder()
                .append(dbId).append("-").append(dbRegion)
                .append(env.getAppsSuffix())
                .toString();
    }
    
    /**
     * GRAPHQL endpoint for a database and region working with Schema definition
     *
     * @param dbId
     *      database identifier
     * @param dbRegion
     *      region identifier
     * @return
     *      the url to invoke
     */
    public static final String getApiGraphQLEndPoint(String dbId, String dbRegion) {
        return getApiGraphQLEndPoint(AstraEnvironment.PROD, dbId, dbRegion);
    }

    /**
     * GRAPHQL endpoint for a database and region working with Schema definition
     *
     * @param env
     *      target environment
     * @param dbId
     *      database identifier
     * @param dbRegion
     *      region identifier
     * @return
     *      the url to invoke
     */
    public static final String getApiGraphQLEndPoint(AstraEnvironment env, String dbId, String dbRegion) {
        Assert.hasLength(dbId, "dbId");
        Assert.hasLength(dbRegion, "dbRegion");
        return new StringBuilder(HTTPS)
                .append(dbId).append("-").append(dbRegion)
                .append(env.getAppsSuffix())
                .append("/api")
                .toString();
    }
    
    /**
     * GRAPHQL endpoint for a database and region working with Schema definition
     *
     * @param dbId
     *      database identifier
     * @param dbRegion
     *      region identifier
     * @return
     *      the url to invoke
     */
    public static final String getApiGraphQLSchemaEndPoint(String dbId, String dbRegion) {
        return getApiGraphQLSchemaEndPoint(AstraEnvironment.PROD, dbId, dbRegion);
    }

    /**
     * GRAPHQL endpoint for a database and region working with Schema definition
     *
     * @param env
     *      target environment
     * @param dbId
     *      database identifier
     * @param dbRegion
     *      region identifier
     * @return
     *      the url to invoke
     */
    public static final String getApiGraphQLSchemaEndPoint(AstraEnvironment env, String dbId, String dbRegion) {
        return getApiGraphQLEndPoint(env, dbId, dbRegion) + "-schema";
    }

    /**
     * GRAPHQL endpoint for a database and region working with Schema definition
     *
     * @param dbId
     *      database identifier
     * @param dbRegion
     *      region identifier
     * @param keyspace
     *      keyspace identifier     
     * @return
     *      the url to invoke
     */
    public static final String getApiGraphQLEndPoint(String dbId, String dbRegion, String keyspace) {
         return getApiGraphQLEndPoint(AstraEnvironment.PROD, dbId, dbRegion, keyspace);
    }

    /**
     * GRAPHQL endpoint for a database and region working with Schema definition
     *
     * @param env
     *      target environment
     * @param dbId
     *      database identifier
     * @param dbRegion
     *      region identifier
     * @param keyspace
     *      keyspace identifier
     * @return
     *      the url to invoke
     */
    public static final String getApiGraphQLEndPoint(AstraEnvironment env, String dbId, String dbRegion, String keyspace) {
        return getApiGraphQLEndPoint(env, dbId, dbRegion) + "/" + keyspace;
    }

    /**
     * Access Streaming v3 Api.
     *
     * @param env
     *      target environment
     * @param cluster
     *      current cluster
     * @param tenant
     *      current tenant
     * @return
     *      api endpoint
     */
    public static final String getApiStreamingV3Endpoint(AstraEnvironment env, String cluster, String tenant) {
        return "https://" + cluster + env.getStreamingV3Suffix() + "/admin/v3/astra/tenants/" + tenant;
    }

    /**
     * Create streaming endpoint
     * @param env
     *      astra environment
     * @param cluster
     *      current pulsar cluster
     * @return
     *      url for streaming API
     */
    public static final String getApiStreamingV2Endpoint(AstraEnvironment env, String cluster) {
        return "https://" + cluster + env.getStreamingV3Suffix() + "/admin/v2";
    }


}
