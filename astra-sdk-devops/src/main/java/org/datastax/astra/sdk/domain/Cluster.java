package org.datastax.astra.sdk.domain;

import java.io.Serializable;

/**
 * Represent a cluster when you list them.
 *
 * @author Cedrick LUNVEN (@clunven)
 */
public class Cluster implements Serializable {
    
    /** Serial. */
    private static final long serialVersionUID = 7245105195981994526L;
    
    /** Cluster name. */
    private String clusterName;
    
    /** Cloud Provider. */
    private String cloudProvider;
    
    /** Cloud Region. */
    private String cloudRegion;
    
    /** Cluster type. */
    private String clusterType;
    
    /** Web service url. */
    private String webServiceUrl;
    
    /** Borker service url. */
    private String brokerServiceUrl;
    
    /** Web socker url */
    private String websocketUrl;
    
    /** Default constructor. */
    public Cluster() {}

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
     * Getter accessor for attribute 'clusterType'.
     *
     * @return
     *       current value of 'clusterType'
     */
    public String getClusterType() {
        return clusterType;
    }

    /**
     * Setter accessor for attribute 'clusterType'.
     * @param clusterType
     * 		new value for 'clusterType '
     */
    public void setClusterType(String clusterType) {
        this.clusterType = clusterType;
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
    
    

}
