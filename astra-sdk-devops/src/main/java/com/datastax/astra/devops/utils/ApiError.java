package com.datastax.astra.devops.utils;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Bean to old devops errors
 */
public class ApiError {

    /**
     * Error code devops API
     */
    @JsonProperty("ID")
    private Integer id;

    /**
     * Error message Devops API
     */
    private String message;

    /**
     * Error constructor
     */
    public ApiError() {
    }

    /**
     * Gets id
     *
     * @return value of id
     */
    public Integer getId() {
        return id;
    }

    /**
     * Set value for id
     *
     * @param id new value for id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * Gets message
     *
     * @return value of message
     */
    public String getMessage() {
        return message;
    }

    /**
     * Set value for message
     *
     * @param message new value for message
     */
    public void setMessage(String message) {
        this.message = message;
    }
}
