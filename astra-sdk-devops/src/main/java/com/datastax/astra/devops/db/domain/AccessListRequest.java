package com.datastax.astra.devops.db.domain;

import java.util.List;

/**
 * Hold Access List
 */
public class AccessListRequest {

    /** Address. */
    private List<AccessListAddressRequest> addresses;

    /** Configuration. */
    private Configurations configurations;

    /**
     * Default constructor.
     */
    public AccessListRequest() {}

    /**
     * Gets addresses
     *
     * @return value of addresses
     */
    public List<AccessListAddressRequest> getAddresses() {
        return addresses;
    }

    /**
     * Set value for addresses
     *
     * @param addresses
     *         new value for addresses
     */
    public void setAddresses(List<AccessListAddressRequest> addresses) {
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
