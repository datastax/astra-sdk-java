package com.dtsx.astra.sdk.db;

import com.dtsx.astra.sdk.AbstractApiClient;
import com.dtsx.astra.sdk.db.domain.Database;
import com.dtsx.astra.sdk.utils.Assert;
import com.dtsx.astra.sdk.utils.AstraEnvironment;

import java.util.Optional;

/**
 * Delegate private link operations.
 */
public class DbPrivateLinksClient extends AbstractApiClient  {

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
    public DbPrivateLinksClient(String token, String databaseId) {
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
    public DbPrivateLinksClient(String token, AstraEnvironment env, String databaseId) {
        super(token, env);
        Assert.hasLength(databaseId, "databaseId");
        this.db = new DbOpsClient(token, env, databaseId).get();
    }

    /** {@inheritDoc} */
    @Override
    public String getServiceName() {
        return "db.private-link";
    }

    /**
     * TODO Get info about all private endpoint connections for a specific database
     * <p>
     * <a href="https://docs.datastax.com/en/astra/docs/_attachments/devopsv2.html#operation/ListPrivateLinksForOrg">...</a>
     */
    public void findAll() {
        System.out.println(db);
        throw new RuntimeException("This function is not yet implemented");
    }

    /**
     * TODO  Get info about private endpoints in a region.
     *
     * @param region
     *         current region where add the private link
     *         <p>
     *         <a href="https://docs.datastax.com/en/astra/docs/_attachments/devopsv2.html#operation/GetPrivateLinksForDatacenter">...</a>
     */
    public void findAll(String region) {
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
     *<a href="https://docs.datastax.com/en/astra/docs/_attachments/devopsv2.html#operation/AcceptEndpointToService">...</a>
     */
    public void create(String region) {
        throw new RuntimeException("This function is not yet implemented");
    }

    /**
     * TODO Get a specific endpoint.
     * <p>
     * <a href="https://docs.datastax.com/en/astra/docs/_attachments/devopsv2.html#operation/GetPrivateLinkEndpoint">...</a>
     *
     * @param region
     *         current region
     * @param endpointId
     *         endpoint id fo the region
     * @return the private endpoint of exist
     */
    public Optional<Object> findById(String region, String endpointId) {
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
     * <a href="https://docs.datastax.com/en/astra/docs/_attachments/devopsv2.html#operation/UpdateEndpoint">...</a>
     */
    public void update(String region, String endpointId, Object endpoint) {
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
     * <a href="https://docs.datastax.com/en/astra/docs/_attachments/devopsv2.html#operation/DeleteEndpoint">...</a>
     */
    public void delete(String region, String endpointId) {
        throw new RuntimeException("This function is not yet implemented");
    }

}
