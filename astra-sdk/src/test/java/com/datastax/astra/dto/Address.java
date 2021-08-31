package com.datastax.astra.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties
public class Address {
    
    private String city;
    private int zipCode;
    
    public Address() {}
    
    public Address(String city, int zip) {
        this.city = city;
        this.zipCode = zip;
                
    }
    /**
     * Getter accessor for attribute 'city'.
     *
     * @return
     *       current value of 'city'
     */
    public String getCity() {
        return city;
    }
    /**
     * Setter accessor for attribute 'city'.
     * @param city
     *      new value for 'city '
     */
    public void setCity(String city) {
        this.city = city;
    }
    /**
     * Getter accessor for attribute 'zipCode'.
     *
     * @return
     *       current value of 'zipCode'
     */
    public int getZipCode() {
        return zipCode;
    }
    /**
     * Setter accessor for attribute 'zipCode'.
     * @param zipCode
     *      new value for 'zipCode '
     */
    public void setZipCode(int zipCode) {
        this.zipCode = zipCode;
    }
    

}
