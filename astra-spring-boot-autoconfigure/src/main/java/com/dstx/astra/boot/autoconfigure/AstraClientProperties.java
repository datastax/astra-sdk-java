package com.dstx.astra.boot.autoconfigure;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "astra")
public class AstraClientProperties {
    
    /** Database unique identifier.  */
    private String astraDatabaseId;
    
    /** Astra database region. */
    private String astraDatabaseRegion;
    
    /** This the endPoint to invoke to work with different API(s). */
    private String baseUrl;
    
    /** Username - required all the time */
    private String username;
    
    /** Password - required all the time */
    private String password;
    
    // --- Devops---
    
    /** Service Account for Devops API. */
    private String clientId;
    
    /** Service Account for Devops API. */
    private String clientName;
    
    /** Service Account for Devops API. */
    private  String clientSecret;
    
    // --- Cql ---
    
    /** working with local Cassandra. */
    private List<String> contactPoints;
    
    /** working with Astra. */
    private String secureConnectBundlePath;
    
    /** setup Astra from an external file. */
    private String driverConfigFile;

}
