package io.stargate.sdk.test.doc.domain;

import java.io.Serializable;

/**
 * POJO for tests
 *
 * @author Cedrick LUNVEN (@clunven)
 */
public class Address implements Serializable {

    /** Serial. */
    private static final long serialVersionUID = 5496004686973233873L;

    /** field. */
    private String city;

    /** field. */
    private int zipCode;

    /**
     * Default constructor
     */
    public Address() {
    }

    /**
     * Full constructor.
     * @param city
     *      city
     * @param zip
     *      zipcode
     */
    public Address(String city, int zip) {
        this.city = city;
        this.zipCode = zip;
    }

    /**
     * Gets city
     *
     * @return value of city
     */
    public String getCity() {
        return city;
    }

    /**
     * Gets zipCode
     *
     * @return value of zipCode
     */
    public int getZipCode() {
        return zipCode;
    }
}