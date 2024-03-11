package com.datastax.astra.devops.db.exception;

/**
 * Exception thrown when accessing a keyspace from its name, and it is not found.
 */
public class KeyspaceNotFoundException extends RuntimeException {

    /**
     * Constructor
     * @param db
     *      db identifier
     * @param keyspace
     *      keyspace identifier
     */
    public KeyspaceNotFoundException(String db, String keyspace) {
        super("Keyspace " + keyspace + " does not exist for db" + db);
    }
}
