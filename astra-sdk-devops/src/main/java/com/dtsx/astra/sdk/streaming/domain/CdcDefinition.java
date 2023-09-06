package com.dtsx.astra.sdk.streaming.domain;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

/**
 * Represent a CDC configuration.
 */
public class CdcDefinition {

    /** Organization identifier. */
    private String orgId;

    /** Cluster Name. */
    private String clusterName;

    /** Tenant identifier. */
    private String tenant;

    /** Tenant Namespace (astracdc). */
    private String namespace;

    /** Unique connector identifier. */
    private String connectorName;

    /** Configuration nature. */
    private String configType;

    /** Database identifier. */
    private String databaseId;

    /** Database name. */
    private String databaseName;

    /** Keyspace name. */
    private String keyspace;

    /** Database table. */
    private String databaseTable;

    /** Connector status. */
    private String connectorStatus;

    /** Cdc. */
    private String cdcStatus;

    /** Cdc. */
    private String codStatus;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'")
    private Date createdAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    private Date updatedAt;

    /** Event topic. */
    private String eventTopic;

    /** Data topic. */
    private String dataTopic;

    /** Number of instances. */
    private int instances;

    /** Number of CPU. */
    private int cpu;

    /** Size of memory. */
    private int memory;

    /**
     * Default constructor.
     */
    public CdcDefinition() {
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
     * Set value for orgId
     *
     * @param orgId
     *         new value for orgId
     */
    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }

    /**
     * Gets clusterName
     *
     * @return value of clusterName
     */
    public String getClusterName() {
        return clusterName;
    }

    /**
     * Set value for clusterName
     *
     * @param clusterName
     *         new value for clusterName
     */
    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
    }

    /**
     * Gets tenant
     *
     * @return value of tenant
     */
    public String getTenant() {
        return tenant;
    }

    /**
     * Set value for tenant
     *
     * @param tenant
     *         new value for tenant
     */
    public void setTenant(String tenant) {
        this.tenant = tenant;
    }

    /**
     * Gets namespace
     *
     * @return value of namespace
     */
    public String getNamespace() {
        return namespace;
    }

    /**
     * Set value for namespace
     *
     * @param namespace
     *         new value for namespace
     */
    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    /**
     * Gets connectorName
     *
     * @return value of connectorName
     */
    public String getConnectorName() {
        return connectorName;
    }

    /**
     * Set value for connectorName
     *
     * @param connectorName
     *         new value for connectorName
     */
    public void setConnectorName(String connectorName) {
        this.connectorName = connectorName;
    }

    /**
     * Gets configType
     *
     * @return value of configType
     */
    public String getConfigType() {
        return configType;
    }

    /**
     * Set value for configType
     *
     * @param configType
     *         new value for configType
     */
    public void setConfigType(String configType) {
        this.configType = configType;
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
     * Set value for databaseId
     *
     * @param databaseId
     *         new value for databaseId
     */
    public void setDatabaseId(String databaseId) {
        this.databaseId = databaseId;
    }

    /**
     * Gets databaseName
     *
     * @return value of databaseName
     */
    public String getDatabaseName() {
        return databaseName;
    }

    /**
     * Set value for databaseName
     *
     * @param databaseName
     *         new value for databaseName
     */
    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
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
     * Set value for keyspace
     *
     * @param keyspace
     *         new value for keyspace
     */
    public void setKeyspace(String keyspace) {
        this.keyspace = keyspace;
    }

    /**
     * Gets databaseTable
     *
     * @return value of databaseTable
     */
    public String getDatabaseTable() {
        return databaseTable;
    }

    /**
     * Set value for databaseTable
     *
     * @param databaseTable
     *         new value for databaseTable
     */
    public void setDatabaseTable(String databaseTable) {
        this.databaseTable = databaseTable;
    }

    /**
     * Gets connectorStatus
     *
     * @return value of connectorStatus
     */
    public String getConnectorStatus() {
        return connectorStatus;
    }

    /**
     * Set value for connectorStatus
     *
     * @param connectorStatus
     *         new value for connectorStatus
     */
    public void setConnectorStatus(String connectorStatus) {
        this.connectorStatus = connectorStatus;
    }

    /**
     * Gets cdcStatus
     *
     * @return value of cdcStatus
     */
    public String getCdcStatus() {
        return cdcStatus;
    }

    /**
     * Set value for cdcStatus
     *
     * @param cdcStatus
     *         new value for cdcStatus
     */
    public void setCdcStatus(String cdcStatus) {
        this.cdcStatus = cdcStatus;
    }

    /**
     * Gets codStatus
     *
     * @return value of codStatus
     */
    public String getCodStatus() {
        return codStatus;
    }

    /**
     * Set value for codStatus
     *
     * @param codStatus
     *         new value for codStatus
     */
    public void setCodStatus(String codStatus) {
        this.codStatus = codStatus;
    }

    /**
     * Gets createdAt
     *
     * @return value of createdAt
     */
    public Date getCreatedAt() {
        return createdAt;
    }

    /**
     * Set value for createdAt
     *
     * @param createdAt
     *         new value for createdAt
     */
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * Gets updatedAt
     *
     * @return value of updatedAt
     */
    public Date getUpdatedAt() {
        return updatedAt;
    }

    /**
     * Set value for updatedAt
     *
     * @param updatedAt
     *         new value for updatedAt
     */
    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    /**
     * Gets eventTopic
     *
     * @return value of eventTopic
     */
    public String getEventTopic() {
        return eventTopic;
    }

    /**
     * Set value for eventTopic
     *
     * @param eventTopic
     *         new value for eventTopic
     */
    public void setEventTopic(String eventTopic) {
        this.eventTopic = eventTopic;
    }

    /**
     * Gets dataTopic
     *
     * @return value of dataTopic
     */
    public String getDataTopic() {
        return dataTopic;
    }

    /**
     * Set value for dataTopic
     *
     * @param dataTopic
     *         new value for dataTopic
     */
    public void setDataTopic(String dataTopic) {
        this.dataTopic = dataTopic;
    }

    /**
     * Gets instances
     *
     * @return value of instances
     */
    public int getInstances() {
        return instances;
    }

    /**
     * Set value for instances
     *
     * @param instances
     *         new value for instances
     */
    public void setInstances(int instances) {
        this.instances = instances;
    }

    /**
     * Gets cpu
     *
     * @return value of cpu
     */
    public int getCpu() {
        return cpu;
    }

    /**
     * Set value for cpu
     *
     * @param cpu
     *         new value for cpu
     */
    public void setCpu(int cpu) {
        this.cpu = cpu;
    }

    /**
     * Gets memory
     *
     * @return value of memory
     */
    public int getMemory() {
        return memory;
    }

    /**
     * Set value for memory
     *
     * @param memory
     *         new value for memory
     */
    public void setMemory(int memory) {
        this.memory = memory;
    }
}
