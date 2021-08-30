package com.datastax.stargate.sdk.core;

public interface ApiConstants {

    String CONTENT_TYPE_JSON        = "application/json";
    
    String HEADER_ACCEPT            = "Accept";
    String HEADER_CASSANDRA         = "X-Cassandra-Token";
    String HEADER_REQUEST_ID        = "X-Cassandra-Request-Id";
    String HEADER_CONTENT_TYPE      = "Content-Type";
    String HEADER_AUTHORIZATION     = "Authorization";
    String HEADER_USER_AGENT        = "User-Agent";
    String HEADER_REQUESTED_WITH    = "X-Requested-With";
    
    String REQUEST_WITH = "AstraJavaSDK " + 
            ApiConstants.class.getPackage().getImplementationVersion();
    

}
