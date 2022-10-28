package com.dtsx.astra.sdk.streaming.domain;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Cedrick LUNVEN (@clunven)
 */
@JsonIgnoreProperties
public class Tenant {
    
    private String tenantName;
    private String clusterName;
    private String webServiceUrl;
    private String brokerServiceUrl;
    private String websocketUrl;
    private String websocketQueryParamUrl;
    private String pulsarToken;
    private String plan;
    private int planCode;
    
    @JsonProperty("astraOrgGUID")
    private UUID organizationId;
    
    private String cloudRegion;
    private String cloudProvider;
    private int cloudProviderCode;
    private String status;
    private String jvmVersion;
    private String pulsarVersion;
    /**
     * Getter accessor for attribute 'tenantName'.
     *
     * @return
     *       current value of 'tenantName'
     */
    public String getTenantName() {
        return tenantName;
    }
    /**
     * Setter accessor for attribute 'tenantName'.
     * @param tenantName
     * 		new value for 'tenantName '
     */
    public void setTenantName(String tenantName) {
        this.tenantName = tenantName;
    }
    /**
     * Getter accessor for attribute 'clusterName'.
     *
     * @return
     *       current value of 'clusterName'
     */
    public String getClusterName() {
        return clusterName;
    }
    /**
     * Setter accessor for attribute 'clusterName'.
     * @param clusterName
     * 		new value for 'clusterName '
     */
    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
    }
    /**
     * Getter accessor for attribute 'webServiceUrl'.
     *
     * @return
     *       current value of 'webServiceUrl'
     */
    public String getWebServiceUrl() {
        return webServiceUrl;
    }
    /**
     * Setter accessor for attribute 'webServiceUrl'.
     * @param webServiceUrl
     * 		new value for 'webServiceUrl '
     */
    public void setWebServiceUrl(String webServiceUrl) {
        this.webServiceUrl = webServiceUrl;
    }
    /**
     * Getter accessor for attribute 'brokerServiceUrl'.
     *
     * @return
     *       current value of 'brokerServiceUrl'
     */
    public String getBrokerServiceUrl() {
        return brokerServiceUrl;
    }
    /**
     * Setter accessor for attribute 'brokerServiceUrl'.
     * @param brokerServiceUrl
     * 		new value for 'brokerServiceUrl '
     */
    public void setBrokerServiceUrl(String brokerServiceUrl) {
        this.brokerServiceUrl = brokerServiceUrl;
    }
    /**
     * Getter accessor for attribute 'websocketUrl'.
     *
     * @return
     *       current value of 'websocketUrl'
     */
    public String getWebsocketUrl() {
        return websocketUrl;
    }
    /**
     * Setter accessor for attribute 'websocketUrl'.
     * @param websocketUrl
     * 		new value for 'websocketUrl '
     */
    public void setWebsocketUrl(String websocketUrl) {
        this.websocketUrl = websocketUrl;
    }
    /**
     * Getter accessor for attribute 'websocketQueryParamUrl'.
     *
     * @return
     *       current value of 'websocketQueryParamUrl'
     */
    public String getWebsocketQueryParamUrl() {
        return websocketQueryParamUrl;
    }
    /**
     * Setter accessor for attribute 'websocketQueryParamUrl'.
     * @param websocketQueryParamUrl
     * 		new value for 'websocketQueryParamUrl '
     */
    public void setWebsocketQueryParamUrl(String websocketQueryParamUrl) {
        this.websocketQueryParamUrl = websocketQueryParamUrl;
    }
    /**
     * Getter accessor for attribute 'pulsarToken'.
     *
     * @return
     *       current value of 'pulsarToken'
     */
    public String getPulsarToken() {
        return pulsarToken;
    }
    /**
     * Setter accessor for attribute 'pulsarToken'.
     * @param pulsarToken
     * 		new value for 'pulsarToken '
     */
    public void setPulsarToken(String pulsarToken) {
        this.pulsarToken = pulsarToken;
    }
    /**
     * Getter accessor for attribute 'plan'.
     *
     * @return
     *       current value of 'plan'
     */
    public String getPlan() {
        return plan;
    }
    /**
     * Setter accessor for attribute 'plan'.
     * @param plan
     * 		new value for 'plan '
     */
    public void setPlan(String plan) {
        this.plan = plan;
    }
    /**
     * Getter accessor for attribute 'planCode'.
     *
     * @return
     *       current value of 'planCode'
     */
    public int getPlanCode() {
        return planCode;
    }
    /**
     * Setter accessor for attribute 'planCode'.
     * @param planCode
     * 		new value for 'planCode '
     */
    public void setPlanCode(int planCode) {
        this.planCode = planCode;
    }
    /**
     * Getter accessor for attribute 'organizationId'.
     *
     * @return
     *       current value of 'organizationId'
     */
    public UUID getOrganizationId() {
        return organizationId;
    }
    /**
     * Setter accessor for attribute 'organizationId'.
     * @param organizationId
     * 		new value for 'organizationId '
     */
    public void setOrganizationId(UUID organizationId) {
        this.organizationId = organizationId;
    }
    /**
     * Getter accessor for attribute 'cloudRegion'.
     *
     * @return
     *       current value of 'cloudRegion'
     */
    public String getCloudRegion() {
        return cloudRegion;
    }
    /**
     * Setter accessor for attribute 'cloudRegion'.
     * @param cloudRegion
     * 		new value for 'cloudRegion '
     */
    public void setCloudRegion(String cloudRegion) {
        this.cloudRegion = cloudRegion;
    }
    /**
     * Getter accessor for attribute 'cloudProvider'.
     *
     * @return
     *       current value of 'cloudProvider'
     */
    public String getCloudProvider() {
        return cloudProvider;
    }
    /**
     * Setter accessor for attribute 'cloudProvider'.
     * @param cloudProvider
     * 		new value for 'cloudProvider '
     */
    public void setCloudProvider(String cloudProvider) {
        this.cloudProvider = cloudProvider;
    }
    /**
     * Getter accessor for attribute 'cloudProviderCode'.
     *
     * @return
     *       current value of 'cloudProviderCode'
     */
    public int getCloudProviderCode() {
        return cloudProviderCode;
    }
    /**
     * Setter accessor for attribute 'cloudProviderCode'.
     * @param cloudProviderCode
     * 		new value for 'cloudProviderCode '
     */
    public void setCloudProviderCode(int cloudProviderCode) {
        this.cloudProviderCode = cloudProviderCode;
    }
    /**
     * Getter accessor for attribute 'status'.
     *
     * @return
     *       current value of 'status'
     */
    public String getStatus() {
        return status;
    }
    /**
     * Setter accessor for attribute 'status'.
     * @param status
     * 		new value for 'status '
     */
    public void setStatus(String status) {
        this.status = status;
    }
    /**
     * Getter accessor for attribute 'jvmVersion'.
     *
     * @return
     *       current value of 'jvmVersion'
     */
    public String getJvmVersion() {
        return jvmVersion;
    }
    /**
     * Setter accessor for attribute 'jvmVersion'.
     * @param jvmVersion
     * 		new value for 'jvmVersion '
     */
    public void setJvmVersion(String jvmVersion) {
        this.jvmVersion = jvmVersion;
    }
    /**
     * Getter accessor for attribute 'pulsarVersion'.
     *
     * @return
     *       current value of 'pulsarVersion'
     */
    public String getPulsarVersion() {
        return pulsarVersion;
    }
    /**
     * Setter accessor for attribute 'pulsarVersion'.
     * @param pulsarVersion
     * 		new value for 'pulsarVersion '
     */
    public void setPulsarVersion(String pulsarVersion) {
        this.pulsarVersion = pulsarVersion;
    }     

}
