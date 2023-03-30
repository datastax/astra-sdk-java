package com.dtsx.astra.sdk.db.domain;

/**
 * Create new Address.
 */
public class AccessListAddressRequest {

    /** Address. */
    private final String address;

    /** Description. */
    private final String description;

    /** Enabled. */
    private final boolean enabled;

    /**
     * Complete constructor.
     *
     * @param address
     *      ip address
     * @param description
     *      description
     */
    public AccessListAddressRequest(String address, String description) {
       this.address     = address;
       this.enabled     = true;
       this.description = description;
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
     * Gets description
     *
     * @return value of description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Gets enabled
     *
     * @return value of enabled
     */
    public boolean isEnabled() {
        return enabled;
    }
}
