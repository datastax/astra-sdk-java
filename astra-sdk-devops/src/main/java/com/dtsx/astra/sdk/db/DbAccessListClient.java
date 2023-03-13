package com.dtsx.astra.sdk.db;

import com.dtsx.astra.sdk.HttpClientWrapper;
import com.dtsx.astra.sdk.db.domain.Database;

/**
 * Operations on Access List.
 */
public class DbAccessListClient {

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
    private Database db;

    /**
     * Initialization of CDC.
     *
     * @param token
     *      current token
     * @param db
     *      database
     */
    public DbAccessListClient(String token, Database db) {
        this.token = token;
        this.db    = db;
    }

    /**
     * TODO Get access list for a database
     * https://docs.datastax.com/en/astra/docs/_attachments/devopsv2.html#operation/GetAccessListForDatabase
     */
    public void findAllAccessLists() {
        throw new RuntimeException("This function is not yet implemented");
    }

    /**
     * TODO Replace access list for your database.
     * https://docs.datastax.com/en/astra/docs/_attachments/devopsv2.html#operation/AddAddressesToAccessListForDatabase
     */
    public void replaceAccessLists() {
        throw new RuntimeException("This function is not yet implemented");
    }

    /**
     * TODO Update existing fields in access list for database
     * https://docs.datastax.com/en/astra/docs/_attachments/devopsv2.html#operation/UpsertAccessListForDatabase
     */
    public void updateAccessLists() {
        throw new RuntimeException("This function is not yet implemented");
    }

    /**
     * TODO Add addresses to access list for a database
     * <p>
     * https://docs.datastax.com/en/astra/docs/_attachments/devopsv2.html#operation/AddAddressesToAccessListForDatabase
     */
    public void createAccessList() {
        throw new RuntimeException("This function is not yet implemented");
    }

    /**
     * TODO Delete addresses or access list for database
     * <p>
     * https://docs.datastax.com/en/astra/docs/_attachments/devopsv2.html#operation/DeleteAddressesOrAccessListForDatabase
     */
    public void deleteAccessList() {
        throw new RuntimeException("This function is not yet implemented");
    }

}
