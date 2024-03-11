package com.datastax.astra.devops.db.exception;

/**
 * Exception thrown when accessing a database from name or id, and it is not found.
 */
public class DatabaseNotFoundException extends RuntimeException {

    /**
     * Constructor with dbName
     *
     * @param dbName
     *      db name
     */
    public DatabaseNotFoundException(String dbName) {
        super("Database '" + dbName + "' has not been found.");
    }

}
