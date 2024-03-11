package com.datastax.astra.devops.db;

import com.datastax.astra.devops.db.domain.AccessList;
import com.datastax.astra.devops.db.domain.AccessListAddressRequest;
import com.datastax.astra.devops.db.domain.AccessListRequest;
import com.datastax.astra.devops.db.domain.Database;
import com.datastax.astra.devops.utils.Assert;
import com.datastax.astra.devops.utils.JsonUtils;
import com.datastax.astra.devops.AbstractApiClient;
import com.datastax.astra.devops.utils.ApiLocator;
import com.datastax.astra.devops.utils.AstraEnvironment;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Operations on Access List.
 */
public class DbAccessListsClient extends AbstractApiClient {

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
    public DbAccessListsClient(String token, String databaseId) {
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
    public DbAccessListsClient(String token, AstraEnvironment env, String databaseId) {
        super(token, env);
        Assert.hasLength(databaseId, "databaseId");
        this.db = new DbOpsClient(token, env, databaseId).get();
    }

    /**
     * Retrieve the access list for a DB.
     *
     * @return
     *      current access list
     */
    public AccessList get() {
        try {
            return JsonUtils.unmarshallBean(GET(getApiDevopsEndpointAccessListsDb()).getBody(), AccessList.class);
        } catch(RuntimeException mex) {
            AccessList ac = new AccessList();
            ac.setDatabaseId(db.getId());
            ac.setOrganizationId(db.getOrgId());
            ac.setAddresses(new ArrayList<>());
            ac.setConfigurations(new AccessList.Configurations(false));
            return ac;
        }
    }

    /**
     * Create a new Address for the DB.
     *
     * @param newAddressed
     *      address to be added
     * @see <a href="https://docs.datastax.com/en/astra/docs/_attachments/devopsv2.html#operation/AddAddressesToAccessListForDatabase">Reference Documentation</a>
     */
    public void addAddress(AccessListAddressRequest... newAddressed) {
        Assert.notNull(newAddressed, "New addresses should not be null");
        Assert.isTrue(newAddressed.length > 0, "New address should not be empty");
        POST(getApiDevopsEndpointAccessListsDb(), JsonUtils.marshall(newAddressed));
    }

    /**
     * Delete the addresses List.
     *
     * @see <a href="https://docs.datastax.com/en/astra/docs/_attachments/devopsv2.html#operation/DeleteAddressesOrAccessListForDatabase">Reference Documentation</a>
     */
    public void delete() {
        DELETE(getApiDevopsEndpointAccessListsDb());
    }

    /**
     * Replace the addresses for a DB
     *
     * @param addresses
     *      address to be added
     *
     * @see <a href="https://docs.datastax.com/en/astra/docs/_attachments/devopsv2.html#operation/AddAddressesToAccessListForDatabase">Reference Documentation</a>
     */
    public void replaceAddresses(AccessListAddressRequest... addresses) {
        Assert.notNull(addresses, "Addresses should not be null");
        Assert.isTrue(addresses.length > 0, "Address should not be empty");
        PUT(getApiDevopsEndpointAccessListsDb(), JsonUtils.marshall(addresses));
    }

    /**
     * Replace the addresses for a DB
     *
     * @param addresses
     *      address to be updated
     *
     * @see <a href="https://docs.datastax.com/en/astra/docs/_attachments/devopsv2.html#operation/UpsertAccessListForDatabase">Reference Documentation</a>
     */
    public void update(AccessListAddressRequest... addresses) {
        Assert.notNull(addresses, "Addresses should not be null");
        Assert.isTrue(addresses.length > 0, "Address should not be empty");
        AccessListRequest alr = new AccessListRequest();
        alr.setAddresses(Arrays.asList(addresses));
        alr.setConfigurations(new AccessListRequest.Configurations(true));
        PATCH(getApiDevopsEndpointAccessListsDb(), JsonUtils.marshall(alr));
    }

    /**
     * Endpoint to access schema for namespace.
     *
     * @return
     *      endpoint
     */
    public String getApiDevopsEndpointAccessListsDb() {
        return ApiLocator.getApiDevopsEndpoint(environment) + "/databases/" + db.getId() + "/access-list";
    }

}
