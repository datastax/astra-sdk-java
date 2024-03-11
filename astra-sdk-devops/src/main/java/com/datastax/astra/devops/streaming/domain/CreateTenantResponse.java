package com.datastax.astra.devops.streaming.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Represents the pojo for tenant creation.
 */
@Data
@JsonIgnoreProperties
@EqualsAndHashCode(callSuper = true)
public class CreateTenantResponse extends Tenant {
    
    private String namespace;

    private String topic;

    /**
     * Default constructor.
     */
    public CreateTenantResponse() {}

}
