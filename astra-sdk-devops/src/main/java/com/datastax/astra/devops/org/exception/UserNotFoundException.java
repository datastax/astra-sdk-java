package com.datastax.astra.devops.org.exception;

/**
 * Exception thrown when accessing a user that does not exist.
 */
public class UserNotFoundException extends RuntimeException {

    /**
     * Constructor with userName
     * 
     * @param userName
     *      name of user
     */
    public UserNotFoundException(String userName) {
        super("User " + userName + "' has not been found.");
    }

}
