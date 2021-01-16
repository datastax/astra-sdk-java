package org.datastax.astra;

import java.io.Serializable;

import org.datastax.astra.doc.AstraCollection;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties
@AstraCollection("person")
public class Person implements Serializable {
    
    /** Serial. */
    private static final long serialVersionUID = 2798538288964412234L;

    private String firstName;
    
    private String lastName;

    /**
     * Defaut Constructor.
     */
    public Person() {}
    
    /**
     * Defaut Constructor.
     */
    public Person(String first, String last) {
        this.firstName = first;
        this.lastName  = last;
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
}
