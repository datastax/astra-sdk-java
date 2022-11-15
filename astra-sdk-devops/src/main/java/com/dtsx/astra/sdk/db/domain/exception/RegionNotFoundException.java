package com.dtsx.astra.sdk.db.domain.exception;

/**
 * Specializeed exception
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
