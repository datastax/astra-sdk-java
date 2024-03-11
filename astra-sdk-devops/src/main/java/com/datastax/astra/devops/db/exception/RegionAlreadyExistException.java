package com.datastax.astra.devops.db.exception;

/**
 * Exception thrown when creating a region with name already in use.
 */
public class RegionAlreadyExistException extends RuntimeException {

    /**
     * Constructor with region name
     *
     * @param regionName
     *      region name
     * @param dbname
     *      database name
     */
    public RegionAlreadyExistException(String regionName, String dbname) {
        super("Region '" + regionName + "' already exists for database '" + dbname + "'. " +
                "Cannot create another region with same name. " +
                "Use flag --if-not-exist to connect to the existing region.");
    }

}
