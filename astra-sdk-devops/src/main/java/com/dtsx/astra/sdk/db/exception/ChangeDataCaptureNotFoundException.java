package com.dtsx.astra.sdk.db.exception;

/**
 * Exception thrown when accessing a region from its name, and it is not found.
 */
public class ChangeDataCaptureNotFoundException extends RuntimeException {

    /**
     * Constructor.
     *
     * @param id
     *      cdc identifier
     * @param db
     *      database identifier
     */
    public ChangeDataCaptureNotFoundException(String id, String db) {
        super("Cdc " + id + " is not available in db " + db);
    }

    /**
     * Constructor.
     *
     * @param keyspace
     *      keyspace name
     * @param table
     *      table name
     * @param tenant
     *      tenant name
     * @param db
     *      database identifier
     */
    public ChangeDataCaptureNotFoundException(String keyspace, String table, String tenant,  String db) {
        super("Cdc for " +
                "keyspace:'" + keyspace + "' " +
                "table:'" + table + "'" +
                "tenant:'" + tenant + "'" +
                "is not available in db '" + db + "'");
    }

}
