package io.stargate.sdk.test.doc.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.stargate.sdk.test.doc.AbstractDocClientDocumentsTest;

import java.io.Serializable;

/**
 * POJO for tests
 *
 * @author Cedrick LUNVEN (@clunven)
 */
public class Person implements Serializable {

    /** Serial. */
    private static final long serialVersionUID = 5637323589269092358L;

    /** field. */
    private String firstname;

    /** field. */
    private String lastname;

    /** field. */
    private int age;

    /** field. */
    private Address address;

    /**
     * Default constructor.
     */
    public Person() {}

    /**
     * Full parameter constructor.
     * @param fn
     *      first name
     * @param ln
     *      last name
     */
    public Person(String fn, String ln) {
        this.firstname = fn;
        this.lastname =ln;
    }

    /**
     * Full parameter constructor.
     * @param fn
     *      first name
     * @param ln
     *      last name
     * @param age
     *      age
     * @param ad
     *      address
     */
    public Person(String fn, String ln, int age, Address ad) {
        this.firstname = fn;
        this.lastname = ln;
        this.age = age;
        this.address = ad;
    }

    /**
     * Getter accessor for attribute 'firstname'.
     *
     * @return
     *       current value of 'firstname'
     */
    public String getFirstname() {
        return firstname;
    }


    /**
     * Getter accessor for attribute 'age'.
     *
     * @return
     *       current value of 'age'
     */
    public int getAge() {
        return age;
    }

    /**
     * Setter accessor for attribute 'age'.
     * @param age
     * 		new value for 'age '
     */
    public void setAge(int age) {
        this.age = age;
    }

    /**
     * Gets address
     *
     * @return value of address
     */
    public Address getAddress() {
        return address;
    }

    /**
     * Set value for firstname
     *
     * @param firstname new value for firstname
     */
    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    /**
     * Gets lastname
     *
     * @return value of lastname
     */
    public String getLastname() {
        return lastname;
    }

    /**
     * Set value for lastname
     *
     * @param lastname new value for lastname
     */
    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    /**
     * Set value for address
     *
     * @param address new value for address
     */
    public void setAddress(Address address) {
        this.address = address;
    }
}