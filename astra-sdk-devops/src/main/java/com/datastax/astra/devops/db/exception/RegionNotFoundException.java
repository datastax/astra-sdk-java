package com.datastax.astra.devops.db.exception;

/**
 * Exception thrown when accessing a region from its name, and it is not found.
 */
public class RegionNotFoundException extends RuntimeException {

    /**
     * Constructor
     * @param db
     *      db identifier
     * @param region
     *      region identifier
     */
    public RegionNotFoundException(String db, String region) {
        super("Database " + db + " is not deployed in region");
    }
}
