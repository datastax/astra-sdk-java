package com.dtsx.astra.sdk.db;

import com.dtsx.astra.sdk.AbstractApiClient;
import com.dtsx.astra.sdk.db.domain.Database;
import com.dtsx.astra.sdk.utils.Assert;

/**
 * Operations on Access List.
 */
public class DbAccessListsClient extends AbstractApiClient {

    /** Get Available Regions. */
    public static final String PATH_ACCESS_LISTS = "/access-lists";

    /**
     * unique db identifier.
     */
    private final Database db;

    /**
     * Constructor.
     *
     * @param token
     *      token
     * @param databaseId
     *      databaseId
     */
    public DbAccessListsClient(String token, String databaseId) {
        super(token);
        Assert.hasLength(databaseId, "databaseId");
        // Test Db exists
        this.db = new DatabaseClient(token, databaseId).get();
    }

    /**
     * TODO Get access list for a database
     * https://docs.datastax.com/en/astra/docs/_attachments/devopsv2.html#operation/GetAccessListForDatabase
     */
    public void findAll() {
        throw new RuntimeException("This function is not yet implemented");
    }

    /**
     * TODO Replace access list for your database.
     * https://docs.datastax.com/en/astra/docs/_attachments/devopsv2.html#operation/AddAddressesToAccessListForDatabase
     */
    public void replace() {
        throw new RuntimeException("This function is not yet implemented");
    }

    /**
     * TODO Update existing fields in access list for database
     * https://docs.datastax.com/en/astra/docs/_attachments/devopsv2.html#operation/UpsertAccessListForDatabase
     */
    public void update() {
        throw new RuntimeException("This function is not yet implemented");
    }

    /**
     * TODO Add addresses to access list for a database
     * <p>
     * https://docs.datastax.com/en/astra/docs/_attachments/devopsv2.html#operation/AddAddressesToAccessListForDatabase
     */
    public void create() {
        throw new RuntimeException("This function is not yet implemented");
    }

    /**
     * TODO Delete addresses or access list for database
     * <p>
     * https://docs.datastax.com/en/astra/docs/_attachments/devopsv2.html#operation/DeleteAddressesOrAccessListForDatabase
     */
    public void delete() {
        throw new RuntimeException("This function is not yet implemented");
    }

}
