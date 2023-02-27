package com.dtsx.astra.sdk.streaming.domain;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

/**
 * Hold statistics for an item.
 */
public class Statistics {

    /** Name. */
    private String name;

    /** Total number of input messages. */
    private long totalMessagesIn = 0;

    /** Total number of output messages. */
    private long totalMessagesOut = 0;

    /** Total number of bytes in. */
    private long totalBytesIn = 0;

    /** Total number of bytes out. */
    private long totalBytesOut = 0;

    /** Total number of messages in. */
    private double msgRateIn = 0;

    /** Total number of messages out. */
    private double msgRateOut = 0;

    /** Inbound throughput. */
    private double throughputIn = 0;

    /** Outbound throughput. */
    private double throughputOut = 0;

    /** Subscription count. */
    private int subscriptionCount = 0;

    /** Producer count. */
    private int producerCount = 0;

    /** Consumer count. */
    private int consumerCount = 0;

    /** Subscription delays. */
    private int subscriptionDelayed = 0;

    /** Storage size. */
    private int storageSize = 0;

    /** backlog storage byte size. */
    private int backlogStorageByteSize = 0;

    /** backlog number. */
    private int msgBacklogNumber = 0;

    /** Object creation date. */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSSSSSSS'Z'")
    private Date updatedAt;

    /** default constructor. */
    public Statistics() {
    }

    /**
     * Gets name
     *
     * @return value of name
     */
    public String getName() {
        return name;
    }

    /**
     * Set value for name
     *
     * @param name
     *         new value for name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets totalMessagesIn
     *
     * @return value of totalMessagesIn
     */
    public long getTotalMessagesIn() {
        return totalMessagesIn;
    }

    /**
     * Set value for totalMessagesIn
     *
     * @param totalMessagesIn
     *         new value for totalMessagesIn
     */
    public void setTotalMessagesIn(long totalMessagesIn) {
        this.totalMessagesIn = totalMessagesIn;
    }

    /**
     * Gets totalMessagesOut
     *
     * @return value of totalMessagesOut
     */
    public long getTotalMessagesOut() {
        return totalMessagesOut;
    }

    /**
     * Set value for totalMessagesOut
     *
     * @param totalMessagesOut
     *         new value for totalMessagesOut
     */
    public void setTotalMessagesOut(long totalMessagesOut) {
        this.totalMessagesOut = totalMessagesOut;
    }

    /**
     * Gets totalBytesIn
     *
     * @return value of totalBytesIn
     */
    public long getTotalBytesIn() {
        return totalBytesIn;
    }

    /**
     * Set value for totalBytesIn
     *
     * @param totalBytesIn
     *         new value for totalBytesIn
     */
    public void setTotalBytesIn(long totalBytesIn) {
        this.totalBytesIn = totalBytesIn;
    }

    /**
     * Gets totalBytesOut
     *
     * @return value of totalBytesOut
     */
    public long getTotalBytesOut() {
        return totalBytesOut;
    }

    /**
     * Set value for totalBytesOut
     *
     * @param totalBytesOut
     *         new value for totalBytesOut
     */
    public void setTotalBytesOut(long totalBytesOut) {
        this.totalBytesOut = totalBytesOut;
    }

    /**
     * Gets msgRateIn
     *
     * @return value of msgRateIn
     */
    public double getMsgRateIn() {
        return msgRateIn;
    }

    /**
     * Set value for msgRateIn
     *
     * @param msgRateIn
     *         new value for msgRateIn
     */
    public void setMsgRateIn(double msgRateIn) {
        this.msgRateIn = msgRateIn;
    }

    /**
     * Gets msgRateOut
     *
     * @return value of msgRateOut
     */
    public double getMsgRateOut() {
        return msgRateOut;
    }

    /**
     * Set value for msgRateOut
     *
     * @param msgRateOut
     *         new value for msgRateOut
     */
    public void setMsgRateOut(double msgRateOut) {
        this.msgRateOut = msgRateOut;
    }

    /**
     * Gets throughputIn
     *
     * @return value of throughputIn
     */
    public double getThroughputIn() {
        return throughputIn;
    }

    /**
     * Set value for throughputIn
     *
     * @param throughputIn
     *         new value for throughputIn
     */
    public void setThroughputIn(double throughputIn) {
        this.throughputIn = throughputIn;
    }

    /**
     * Gets throughputOut
     *
     * @return value of throughputOut
     */
    public double getThroughputOut() {
        return throughputOut;
    }

    /**
     * Set value for throughputOut
     *
     * @param throughputOut
     *         new value for throughputOut
     */
    public void setThroughputOut(double throughputOut) {
        this.throughputOut = throughputOut;
    }

    /**
     * Gets subscriptionCount
     *
     * @return value of subscriptionCount
     */
    public int getSubscriptionCount() {
        return subscriptionCount;
    }

    /**
     * Set value for subscriptionCount
     *
     * @param subscriptionCount
     *         new value for subscriptionCount
     */
    public void setSubscriptionCount(int subscriptionCount) {
        this.subscriptionCount = subscriptionCount;
    }

    /**
     * Gets producerCount
     *
     * @return value of producerCount
     */
    public int getProducerCount() {
        return producerCount;
    }

    /**
     * Set value for producerCount
     *
     * @param producerCount
     *         new value for producerCount
     */
    public void setProducerCount(int producerCount) {
        this.producerCount = producerCount;
    }

    /**
     * Gets consumerCount
     *
     * @return value of consumerCount
     */
    public int getConsumerCount() {
        return consumerCount;
    }

    /**
     * Set value for consumerCount
     *
     * @param consumerCount
     *         new value for consumerCount
     */
    public void setConsumerCount(int consumerCount) {
        this.consumerCount = consumerCount;
    }

    /**
     * Gets subscriptionDelayed
     *
     * @return value of subscriptionDelayed
     */
    public int getSubscriptionDelayed() {
        return subscriptionDelayed;
    }

    /**
     * Set value for subscriptionDelayed
     *
     * @param subscriptionDelayed
     *         new value for subscriptionDelayed
     */
    public void setSubscriptionDelayed(int subscriptionDelayed) {
        this.subscriptionDelayed = subscriptionDelayed;
    }

    /**
     * Gets storageSize
     *
     * @return value of storageSize
     */
    public int getStorageSize() {
        return storageSize;
    }

    /**
     * Set value for storageSize
     *
     * @param storageSize
     *         new value for storageSize
     */
    public void setStorageSize(int storageSize) {
        this.storageSize = storageSize;
    }

    /**
     * Gets backlogStorageByteSize
     *
     * @return value of backlogStorageByteSize
     */
    public int getBacklogStorageByteSize() {
        return backlogStorageByteSize;
    }

    /**
     * Set value for backlogStorageByteSize
     *
     * @param backlogStorageByteSize
     *         new value for backlogStorageByteSize
     */
    public void setBacklogStorageByteSize(int backlogStorageByteSize) {
        this.backlogStorageByteSize = backlogStorageByteSize;
    }

    /**
     * Gets msgBacklogNumber
     *
     * @return value of msgBacklogNumber
     */
    public int getMsgBacklogNumber() {
        return msgBacklogNumber;
    }

    /**
     * Set value for msgBacklogNumber
     *
     * @param msgBacklogNumber
     *         new value for msgBacklogNumber
     */
    public void setMsgBacklogNumber(int msgBacklogNumber) {
        this.msgBacklogNumber = msgBacklogNumber;
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
}
