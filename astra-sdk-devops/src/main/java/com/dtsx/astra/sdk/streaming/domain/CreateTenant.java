package com.dtsx.astra.sdk.streaming.domain;

/**
 * @author Cedrick LUNVEN (@clunven)
 */
public class CreateTenant {
    
    private String cloudProvider = "aws";
    private String cloudRegion   = "useast2";
    private String plan          = "free";
    private String tenantName;
    private String userEmail;
    
    /**
     * Default Constructor.
     */
    public CreateTenant() {}
    
    /**
     * Provide tenant Name and email.
     * @param tenantName
     *      tenant identifier
     * @param email
     *      email
     */
    public CreateTenant(String tenantName, String email) {
        this.userEmail = email;
        this.tenantName = tenantName;
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
     * Getter accessor for attribute 'userEmail'.
     *
     * @return
     *       current value of 'userEmail'
     */
    public String getUserEmail() {
        return userEmail;
    }

    /**
     * Setter accessor for attribute 'userEmail'.
     * @param userEmail
     * 		new value for 'userEmail '
     */
    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

}
