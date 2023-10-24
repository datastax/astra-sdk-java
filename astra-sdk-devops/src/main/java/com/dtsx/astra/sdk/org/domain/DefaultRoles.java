package com.dtsx.astra.sdk.org.domain;

/**
 * Astra does provide some Ad Hoc roles with keys and labels.
 */
public enum DefaultRoles {
    
    /**
     * BILLING_ADMINISTRATOR.
     */
    BILLING_ADMINISTRATOR("Billing Admin", "Billing Administrator"),
   
    /**
     * ORGANIZATION_ADMINISTRATOR.
     */
    ORGANIZATION_ADMINISTRATOR("Organization Administrator", "Organization Administrator"),
    
    /**
     * DATABASE_ADMINISTRATOR.
     */
    DATABASE_ADMINISTRATOR("Database Administrator", "Database Administrator"),
    
    /**
     * ADMINISTRATOR_USER.
     */
    ADMINISTRATOR_USER("API Admin User", "Administrator User"),
    
    /**
     * ADMINISTRATOR_SERVICE_ACCOUNT.
     */
    ADMINISTRATOR_SERVICE_ACCOUNT("Admin Svc Acct","Administrator Service Account"),
    
    /**
     * READ_ONLY_USER.
     */
    READ_ONLY_USER("RO User", "Read Only User"),
    
    /**
     * READ_WRITE_USER.
     */
    READ_WRITE_USER("R/W User", "Read/Write User"),
    
    /**
     * READ_ONLY_SERVICE_ACCOUNT.
     */
    READ_ONLY_SERVICE_ACCOUNT("RO Svc Acct", "Read Only Service Account"),
    
    /**
     * READ_WRITE_SERVICE_ACCOUNT.
     */
    READ_WRITE_SERVICE_ACCOUNT("R/W Svc Acct", "Read/Write Service Account"),
    
    /**
     * API_ADMINISTRATOR_USER.
     */
    API_ADMINISTRATOR_USER("API Admin User", "API Administrator User"),
    
    /**
     * API_ADMINISTRATOR_SERVICE_ACCOUNT.
     */
    API_ADMINISTRATOR_SERVICE_ACCOUNT("API Admin Svc Acct", "API Administrator Service Account"),
    
    /**
     * API_READ_ONLY_USER.
     */
    API_READ_ONLY_USER("API RO User", "API Read Only User"),
    
    /**
     * API_READ_ONLY_SERVICE_ACCOUNT.
     */
    API_READ_ONLY_SERVICE_ACCOUNT("API RO Svc Acct", "API Read Only Service Account"),
    
    /**
     * API_READ_WRITE_USER.
     */
    API_READ_WRITE_USER("API R/W User", "API Read/Write User"),
    
    /**
     * API_READ_WRITE_SERVICE_ACCOUNT.
     */
    API_READ_WRITE_SERVICE_ACCOUNT("API R/W Svc Acct", "API Read/Write Service Account"),
    
    /**
     * UI_VIEW_ONLY.
     */
    UI_VIEW_ONLY("UI View Only", "UI View Only");
    
    /** Key to be used to find the role ID. */
    private String name;
    
    /** Current label on screen. */
    private String label;
    
    /**
     * Provide default roles.
     * 
     * @param key
     *      role key
     * @param label
     *      role value
     */
    private DefaultRoles(String key, String label) {
        this.name   = key;
        this.label = label;
    }

    /**
     * Getter accessor for attribute 'key'.
     *
     * @return
     *       current value of 'key'
     */
    public String getName() {
        return name;
    }

    /**
     * Getter accessor for attribute 'label'.
     *
     * @return
     *       current value of 'label'
     */
    public String getLabel() {
        return label;
    }
    

}
