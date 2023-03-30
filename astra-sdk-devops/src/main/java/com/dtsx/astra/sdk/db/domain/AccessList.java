package com.dtsx.astra.sdk.db.domain;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Hold an Access List
 */
public class AccessList {

    /** Organization identifier. */
    private String organizationId;

    /** Database Identifier. */
    private String databaseId;

    /** Address. */
    private List<AccessListAddress> addresses;

    /** Configuration. */
    private Configurations configurations;

    /**
     * Default constructor.
     */
    public AccessList() {}

    /**
     * Gets organizationId
     *
     * @return value of organizationId
     */
    public String getOrganizationId() {
        return organizationId;
    }

    /**
     * Set value for organizationId
     *
     * @param organizationId
     *         new value for organizationId
     */
    public void setOrganizationId(String organizationId) {
        this.organizationId = organizationId;
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
     * Gets addresses
     *
     * @return value of addresses
     */
    public List<AccessListAddress> getAddresses() {
        return addresses;
    }

    /**
     * Set value for addresses
     *
     * @param addresses
     *         new value for addresses
     */
    public void setAddresses(List<AccessListAddress> addresses) {
        this.addresses = addresses;
    }

    /**
     * Gets configurations
     *
     * @return value of configurations
     */
    public Configurations getConfigurations() {
        return configurations;
    }

    /**
     * Set value for configurations
     *
     * @param configurations
     *         new value for configurations
     */
    public void setConfigurations(Configurations configurations) {
        this.configurations = configurations;
    }



    /**
     * Configuration.
     */
    public static class Configurations {

        /** configuration key. */
        private boolean accessListEnabled;

        /**
         * Default constructor
         */
        public Configurations() {}

        /**
         * Complete constructor.
         *
         * @param accessListEnabled
         *      access list enabled
         */
        public Configurations(boolean accessListEnabled) {
            this.accessListEnabled = accessListEnabled;
        }

        /**
         * Gets accessListEnabled
         *
         * @return value of accessListEnabled
         */
        public boolean isAccessListEnabled() {
            return accessListEnabled;
        }

        /**
         * Set value for accessListEnabled
         *
         * @param accessListEnabled
         *         new value for accessListEnabled
         */
        public void setAccessListEnabled(boolean accessListEnabled) {
            this.accessListEnabled = accessListEnabled;
        }
    }
}
