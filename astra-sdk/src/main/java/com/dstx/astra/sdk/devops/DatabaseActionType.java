package com.dstx.astra.sdk.devops;

/**
 * Encoded all values for 'availableActions'
 *
 * @author Cedrick LUNVEN (@clunven)
 */
public enum DatabaseActionType {
    addDatacenters,
    addKeyspace,
    addTable,
    getCreds,
    launchMigrationProxy,
    park,
    removeKeyspace,
    removeMigrationProxy,
    resize,
    resetPassword,
    suspend,
    terminate,
    terminateDatacenter,
    unpark;
}
