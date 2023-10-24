package com.dtsx.astra.sdk.streaming.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

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
