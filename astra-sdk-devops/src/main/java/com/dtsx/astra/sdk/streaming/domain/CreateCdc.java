package com.dtsx.astra.sdk.streaming.domain;

/**
 * Cdc Creation Request.
 */
public class CreateCdc extends DeleteCdc {

    /** db Name. */
    private String databaseName;

    /** Topic Partition. */
    private int topicPartitions = 1;

    /**
     * Default constructor
     */
    public CreateCdc() {}

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
     * Gets topicPartitions
     *
     * @return value of topicPartitions
     */
    public int getTopicPartitions() {
        return topicPartitions;
    }

    /**
     * Set value for topicPartitions
     *
     * @param topicPartitions
     *         new value for topicPartitions
     */
    public void setTopicPartitions(int topicPartitions) {
        this.topicPartitions = topicPartitions;
    }
}
