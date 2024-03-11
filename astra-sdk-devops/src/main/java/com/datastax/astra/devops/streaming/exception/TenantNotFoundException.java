package com.datastax.astra.devops.streaming.exception;

/**
 * Exception thrown when accessing a tenant that does not exist.
 */
public class TenantNotFoundException extends RuntimeException {

    /**
     * Constructor with tenant name
     * 
     * @param tenantName
     *      tenant name
     */
    public TenantNotFoundException(String tenantName) {
        super("Tenant '" + tenantName + "' has not been found.");
    }

}
