package com.datastax.yasa.docapi.person;

public class Address {
    
    private int num;
    
    private String street;
    
    private String city;
    
    private int zipCode;
    
    public Address() {
    }
    
    public Address(int num, String street, String city, int zipCode) {
        super();
        this.num = num;
        this.street = street;
        this.city = city;
        this.zipCode = zipCode;
    }
    
    /**
     * Getter accessor for attribute 'num'.
     *
     * @return
     *       current value of 'num'
     */
    public int getNum() {
        return num;
    }
    /**
     * Setter accessor for attribute 'num'.
     * @param num
     * 		new value for 'num '
     */
    public void setNum(int num) {
        this.num = num;
    }
    /**
     * Getter accessor for attribute 'street'.
     *
     * @return
     *       current value of 'street'
     */
    public String getStreet() {
        return street;
    }
    /**
     * Setter accessor for attribute 'street'.
     * @param street
     * 		new value for 'street '
     */
    public void setStreet(String street) {
        this.street = street;
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
     * 		new value for 'city '
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
     * 		new value for 'zipCode '
     */
    public void setZipCode(int zipCode) {
        this.zipCode = zipCode;
    }

}
