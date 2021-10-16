package com.datastax.stargate.sdk.core;

/**
 * Group constants on a dedicated interface.
 * 
 * @author Cedrick LUNVEN (@clunven)
 */
public interface ApiConstants {

    /** Headers, Api is usig JSON */
    String CONTENT_TYPE_JSON        = "application/json";
    
    /** Header param. */
    String HEADER_ACCEPT            = "Accept";
    
    /** Headers param to insert the token. */
    String HEADER_CASSANDRA         = "X-Cassandra-Token";
    
    /** Headers param to insert the unique identifier for the request. */
    String HEADER_REQUEST_ID        = "X-Cassandra-Request-Id";
    
    /** Headers param to insert the conte type. */
    String HEADER_CONTENT_TYPE      = "Content-Type";
    
    /** Headers param to insert the token for devops API. */
    String HEADER_AUTHORIZATION     = "Authorization";
    
    /** Headers name to insert the user agent identifying the client. */
    String HEADER_USER_AGENT        = "User-Agent";
    
    /** Headers param to insert the user agent identifying the client. */
    String HEADER_REQUESTED_WITH    = "X-Requested-With";
    
    /** Value for the requested with. */
    String REQUEST_WITH = "AstraJavaSDK " + ApiConstants.class.getPackage().getImplementationVersion();

}
