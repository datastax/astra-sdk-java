package com.dstx.astra.sdk.devops;

/**
 * Encoded all values for 'tier'
 *
 * @author Cedrick LUNVEN (@clunven)
 */
public enum DatabaseTierType {
    
    developer("developer"),
    cloudnative("cloudnative"),
    serverless("serverless"),
    A5("A5"),
    A10("A10"),
    A20("A20"),
    A40("A40"),
    C10("C10"),
    C20("C20"),
    C40("C40"),
    D10("D10"),
    D20("D20"),
    D40("D40");

    private String code;
    
    private DatabaseTierType(String code) {
        this.code = code;
    }
    
    public String getCode() {
        return code;
    }
    
}
