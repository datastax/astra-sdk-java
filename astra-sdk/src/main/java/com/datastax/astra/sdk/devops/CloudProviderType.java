package com.datastax.astra.sdk.devops;

/**
 * Encoded all values for 'cloudProvider'
 *
 * @author Cedrick LUNVEN (@clunven)
 */
public enum CloudProviderType {
    
    ALL("ALL"), 
    GCP("GCP"), 
    GCP_MARKETPLACE("GCP_MARKETPLACE"), 
    AZURE("AZURE"), 
    AWS("AWS");
    
    private String code;
    
    private CloudProviderType(String code) {
        this.code = code;
    }
    
    public String getCode() {
        return code;
    }
    
}
