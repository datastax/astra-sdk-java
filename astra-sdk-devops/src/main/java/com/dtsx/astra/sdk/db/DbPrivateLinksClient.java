package com.dtsx.astra.sdk.db;

import com.dtsx.astra.sdk.HttpClientWrapper;
import com.dtsx.astra.sdk.db.domain.Database;

import java.util.Optional;

/**
 * Delegate private link operations.
 */
public class DbPrivateLinksClient {

    /**
     * Wrapper handling header and error management as a singleton.
     */
    private final HttpClientWrapper http = HttpClientWrapper.getInstance();

    /**
     * unique db identifier.
     */
    private final String token;

    /**
     * Load database
     */
    private final Database db;

    /**
     * Initialization of CDC.
     *
     * @param token
     *      current token
     * @param db
     *      database
     */
    public DbPrivateLinksClient(String token, Database db) {
        this.token = token;
        this.db    = db;
    }

    /**
     * TODO Get info about all private endpoint connections for a specific database
     * <p>
     * https://docs.datastax.com/en/astra/docs/_attachments/devopsv2.html#operation/ListPrivateLinksForOrg
     */
    public void findAllPrivateLinksByRegion() {
        throw new RuntimeException("This function is not yet implemented");
    }

    /**
     * TODO  Get info about private endpoints in a region.
     *
     * @param region
     *         current region where add the private link
     *         <p>
     *         https://docs.datastax.com/en/astra/docs/_attachments/devopsv2.html#operation/GetPrivateLinksForDatacenter
     */
    public void findAllPrivateLinksByRegion(String region) {
        throw new RuntimeException("This function is not yet implemented");
    }

    /**
     * TODO Add an allowed principal to the service.
     *
     * @param region
     *         region where add the principal
     *         Configure a private endpoint connection by providing the allowed principal to connect with
     */
    public void createPrincipal(String region) {
        throw new RuntimeException("This function is not yet implemented");
    }

    /**
     * TODO Accept a private endpoint connection.
     *
     * @param region
     *         region where add the private endpoint
     *         <p>
     *         https://docs.datastax.com/en/astra/docs/_attachments/devopsv2.html#operation/AcceptEndpointToService
     */
    public void createPrivateEndpoint(String region) {
        throw new RuntimeException("This function is not yet implemented");
    }

    /**
     * TODO Get a specific endpoint.
     * <p>
     * https://docs.datastax.com/en/astra/docs/_attachments/devopsv2.html#operation/GetPrivateLinkEndpoint
     *
     * @param region
     *         current region
     * @param endpointId
     *         endpoint id fo the region
     * @return the private endpoint of exist
     */
    public Optional<Object> findPrivateEndpoint(String region, String endpointId) {
        throw new RuntimeException("This function is not yet implemented");
    }

    /**
     * TODO Update private endpoint description.
     *
     * @param region
     *         current region
     * @param endpointId
     *         endpoint id fo the region
     * @param endpoint
     *         new value for the endpoint
     *         <p>
     *         https://docs.datastax.com/en/astra/docs/_attachments/devopsv2.html#operation/UpdateEndpoint
     */
    public void updatePrivateEndpoint(String region, String endpointId, Object endpoint) {
        throw new RuntimeException("This function is not yet implemented");
    }

    /**
     * TODO Delete private endpoint connection.
     *
     * @param region
     *         current region
     * @param endpointId
     *         endpoint id fo the region
     *         <p>
     *         https://docs.datastax.com/en/astra/docs/_attachments/devopsv2.html#operation/DeleteEndpoint
     */
    public void deletePrivateEndpoint(String region, String endpointId) {
        throw new RuntimeException("This function is not yet implemented");
    }

}
