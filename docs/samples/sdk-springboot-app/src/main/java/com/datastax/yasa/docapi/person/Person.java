package com.datastax.yasa.docapi.person;

import java.io.Serializable;
import java.util.List;

import com.datastax.stargate.sdk.doc.Collection;

@Collection("person")
public class Person implements Serializable {
    
    /** Serial. */
    private static final long serialVersionUID = 7566592685996797879L;
    
    private String firstName;
    
    private String lastName;
    
    private String email;
    
    private List<Address> adresses;
    
    /**
     * Default Constructor for instrospection
     */
    public Person() {
    }
            
    public Person(String firstName, String lastName, String email, List<Address> adresses) {
        super();
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.adresses = adresses;
    }
    
    /**
     * Getter accessor for attribute 'firstName'.
     *
     * @return
     *       current value of 'firstName'
     */
    public String getFirstName() {
        return firstName;
    }
    /**
     * Setter accessor for attribute 'firstName'.
     * @param firstName
     * 		new value for 'firstName '
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    /**
     * Getter accessor for attribute 'lastName'.
     *
     * @return
     *       current value of 'lastName'
     */
    public String getLastName() {
        return lastName;
    }
    /**
     * Setter accessor for attribute 'lastName'.
     * @param lastName
     * 		new value for 'lastName '
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    /**
     * Getter accessor for attribute 'email'.
     *
     * @return
     *       current value of 'email'
     */
    public String getEmail() {
        return email;
    }
    /**
     * Setter accessor for attribute 'email'.
     * @param email
     * 		new value for 'email '
     */
    public void setEmail(String email) {
        this.email = email;
    }
    /**
     * Getter accessor for attribute 'adresses'.
     *
     * @return
     *       current value of 'adresses'
     */
    public List<Address> getAdresses() {
        return adresses;
    }
    /**
     * Setter accessor for attribute 'adresses'.
     * @param adresses
     * 		new value for 'adresses '
     */
    public void setAdresses(List<Address> adresses) {
        this.adresses = adresses;
    }
    

}
