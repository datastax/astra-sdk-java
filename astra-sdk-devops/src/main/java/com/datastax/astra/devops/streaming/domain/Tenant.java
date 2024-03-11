package com.datastax.astra.devops.streaming.domain;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Hold elements for tenant.
 */
@Data
@JsonIgnoreProperties
public class Tenant {

    @JsonProperty("astraOrgGUID")
    private UUID organizationId;

    private String tenantName;
    private String clusterName;

    private String webServiceUrl;
    private String brokerServiceUrl;
    private String websocketUrl;
    private String websocketQueryParamUrl;
    private String pulsarToken;

    private String plan;
    private int    planCode;

    private String cloudRegion;
    private String cloudProvider;
    private int    cloudProviderCode;

    private String status;
    private String jvmVersion;
    private String pulsarVersion;

    /**
     * Default constructor.
     */
    public Tenant() {}
}
