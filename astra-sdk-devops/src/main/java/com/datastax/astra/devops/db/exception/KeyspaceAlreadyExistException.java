package com.datastax.astra.devops.db.exception;

/**
 * Exception thrown when creating a keyspace with name already in use.
 */
public class KeyspaceAlreadyExistException extends RuntimeException {

    /**
     * Constructor with keyspace name
     * 
     * @param ksName
     *      keyspace name
     * @param dbname
     *      database name
     */
    public KeyspaceAlreadyExistException(String ksName, String dbname) {
        super("Keyspace '" + ksName + "' already exists for database '" + dbname +
                "' Cannot create another keyspace with same name. " +
                "Use flag --if-not-exist to connect to the existing keyspace.");
    }

}
