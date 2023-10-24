package com.dtsx.astra.sdk.utils;

import java.util.UUID;

/**
 * Utilities to work with ids
 */
public class IdUtils {

    /** Hide constructor. */
    private IdUtils() {}

    /**
     * Check if it is uuid.
     *
     * @param uuid
     *      unique identifier
     * @return
     *      check if this is uuid
     */
    public static boolean isUUID(String uuid) {
        try {
            UUID.fromString(uuid);
        } catch(IllegalArgumentException ieox) {
            return false;
        }
        return true;
    }
}
