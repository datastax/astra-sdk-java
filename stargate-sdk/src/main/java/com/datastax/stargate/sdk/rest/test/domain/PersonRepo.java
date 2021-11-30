/*
 * Copyright DataStax, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.datastax.stargate.sdk.rest.test.domain;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Sample POJO
 *
 * @author Cedrick LUNVEN (@clunven)
 */
@JsonIgnoreProperties
public class PersonRepo {
    
    /** param. */
    private String firstname;
    
    /** param. */
    private String lastname;
    
    /** param. */
    private int age;
    
    /** param. */
    private List<String> countries;
    
    /** param. */
    private Address address;

    /**
     * Defaut Constructor.
     */
    public PersonRepo() {}
    
    /**
     * Defaut Constructor.
     * 
     * @param first
     *      firstname
     * @param last
     *      lastname
     */
    public PersonRepo(String first, String last) {
        this.firstname = first;
        this.lastname  = last;
    }
    
    /**
     * Defaut Constructor.
     * 
     * @param first
     *      firstname
     * @param last
     *      lastname
     * @param age
     *      age
     * @param a
     *      adress
     */
    public PersonRepo(String first, String last, int age, Address a) {
        this.firstname = first;
        this.lastname  = last;
        this.age = age;
        this.address = a;
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
     * Getter accessor for attribute 'address'.
     *
     * @return
     *       current value of 'address'
     */
    public Address getAddress() {
        return address;
    }

    /**
     * Setter accessor for attribute 'address'.
     * @param address
     * 		new value for 'address '
     */
    public void setAddress(Address address) {
        this.address = address;
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
     * Setter accessor for attribute 'firstname'.
     * @param firstname
     * 		new value for 'firstname '
     */
    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    /**
     * Getter accessor for attribute 'lastname'.
     *
     * @return
     *       current value of 'lastname'
     */
    public String getLastname() {
        return lastname;
    }

    /**
     * Setter accessor for attribute 'lastname'.
     * @param lastname
     * 		new value for 'lastname '
     */
    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    /**
     * Getter accessor for attribute 'contries'.
     *
     * @return
     *       current value of 'contries'
     */
    public List<String> getCountries() {
        return countries;
    }

    /**
     * Setter accessor for attribute 'contries'.
     * @param contries
     * 		new value for 'contries '
     */
    public void setConutries(List<String> contries) {
        this.countries = contries;
    }
}
