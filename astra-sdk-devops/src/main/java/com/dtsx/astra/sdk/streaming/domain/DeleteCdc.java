package com.dtsx.astra.sdk.streaming.domain;

/**
 * Delete a tenant.
 */
public class DeleteCdc {

    /** Organization ID. */
    private String orgId;

    /** Target db id. */
    private String databaseId;

    /** Keyspace. */
    private String keyspace;

    /** Table Name. */
    private String tableName;

    /**
     * Default.
     */
    public DeleteCdc() {}

    /**
     * Full Constructor.
     *
     * @param orgId
     *      organization identifier
     * @param databaseId
     *      database identifier
     * @param keyspace
     *      keyspace name
     * @param tableName
     *      table name
     */
    public DeleteCdc(String orgId, String databaseId, String keyspace, String tableName) {
        this.orgId = orgId;
        this.databaseId = databaseId;
        this.keyspace = keyspace;
        this.tableName = tableName;
    }

    /**
     * Gets databaseId
     *
     * @return value of databaseId
     */
    public String getDatabaseId() {
        return databaseId;
    }

    /**
     * Gets keyspace
     *
     * @return value of keyspace
     */
    public String getKeyspace() {
        return keyspace;
    }

    /**
     * Gets orgId
     *
     * @return value of orgId
     */
    public String getOrgId() {
        return orgId;
    }

    /**
     * Gets tableName
     *
     * @return value of tableName
     */
    public String getTableName() {
        return tableName;
    }

    /**
     * Set value for orgId
     *
     * @param orgId
     *         new value for orgId
     */
    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }

    /**
     * Set value for databaseId
     *
     * @param databaseId
     *         new value for databaseId
     */
    public void setDatabaseId(String databaseId) {
        this.databaseId = databaseId;
    }

    /**
     * Set value for keyspace
     *
     * @param keyspace
     *         new value for keyspace
     */
    public void setKeyspace(String keyspace) {
        this.keyspace = keyspace;
    }

    /**
     * Set value for tableName
     *
     * @param tableName
     *         new value for tableName
     */
    public void setTableName(String tableName) {
        this.tableName = tableName;
    }
}
