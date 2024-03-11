package com.datastax.astra.devops.db.domain;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

/**
 * Nested Address
 */
public class AccessListAddress {

    /** Address. */
    private String address;

    /** Description. */
    private String description;

    /** Enabled. */
    private boolean enabled;

    /** Last Updates. */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss.SSS Z z")
    private LocalDateTime lastUpdateDateTime;

    /**
     * Default constructor.
     */
    public AccessListAddress() {}

    /**
     * Complete constructor.
     *
     * @param address
     *      ip address
     * @param description
     *      description
     */
    public AccessListAddress(String address, String description) {
        this(address, description, true, null);
    }

    /**
     * Complete constructor.
     *
     * @param address
     *      ip address
     * @param description
     *      description
     * @param enabled
     *      last updated
     * @param lastUpdateDateTime
     *      last updated
     */
    public AccessListAddress(String address, String description, boolean enabled, LocalDateTime lastUpdateDateTime) {
        this.address = address;
        this.description = description;
        this.enabled = enabled;
        this.lastUpdateDateTime = lastUpdateDateTime;
    }

    /**
     * Gets address
     *
     * @return value of address
     */
    public String getAddress() {
        return address;
    }

    /**
     * Set value for address
     *
     * @param address
     *         new value for address
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * Gets description
     *
     * @return value of description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Set value for description
     *
     * @param description
     *         new value for description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Gets enabled
     *
     * @return value of enabled
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Set value for enabled
     *
     * @param enabled
     *         new value for enabled
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * Gets lastUpdateDateTime
     *
     * @return value of lastUpdateDateTime
     */
    public LocalDateTime getLastUpdateDateTime() {
        return lastUpdateDateTime;
    }

    /**
     * Set value for lastUpdateDateTime
     *
     * @param lastUpdateDateTime
     *         new value for lastUpdateDateTime
     */
    public void setLastUpdateDateTime(LocalDateTime lastUpdateDateTime) {
        this.lastUpdateDateTime = lastUpdateDateTime;
    }
}