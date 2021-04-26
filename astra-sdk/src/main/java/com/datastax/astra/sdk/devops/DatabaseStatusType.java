package com.datastax.astra.sdk.devops;

/**
 * Encoded all values for 'tier'
 *
 * @author Cedrick LUNVEN (@clunven)
 */
public enum DatabaseStatusType {
    ACTIVE,
    ERROR,
    INITIALIZING,
    MAINTENANCE,
    PARKED,
    PARKING,
    PENDING,
    PREPARED,
    PREPARING,
    RESIZING,
    TERMINATED,
    TERMINATING,
    UNKNOWN,
    UNPARKING;
}
