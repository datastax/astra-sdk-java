package com.datastax.astra.sdk.organizations;

import java.util.Optional;

import com.datastax.astra.sdk.organizations.domain.IamToken;
import com.datastax.stargate.sdk.utils.Assert;
import com.datastax.stargate.sdk.utils.HttpApisClient;

/**
 * Client for resource '/clientIdSecret' 
 * 
 * @author Cedrick LUNVEN (@clunven)
 */
public class TokenClient {
    
    /** Working role. */
    private final String tokenId;
    
    /** Wrapper handling header and error management as a singleton. */
    private final HttpApisClient http;
    
    /** Wrapper handling header and error management as a singleton. */
    private final OrganizationsClient org;
    
    /**
     * Default constructor.
     *
     * @param http
     *      client
     * @param roleId
     *      current role identifier
     */
    public TokenClient(OrganizationsClient org, String tokenId) {
        this.tokenId = tokenId;
        this.org     = org;
        this.http    = HttpApisClient.getInstance();
        Assert.hasLength(tokenId, "tokenId");
    }
    
    /**
     * Retrieve role information from its id.
     *
     * @return
     *      role informations
     */
    public Optional<IamToken> find() {
        return org.tokens()
                  .filter(t -> t.getClientId().equalsIgnoreCase(tokenId))
                  .findFirst();
    }
    
    /**
     * Check in inexistence of a token.
     */
    public boolean exist() {
        return find().isPresent();
    }
    
    /**
     * Revoke a token.
     */
    public void delete() {
        if (!exist()) {
            throw new RuntimeException("Token '"+ tokenId + "' has not been found");
        }
        http.DELETE(getEndpointToken());
    }
    
    // ---------------------------------
    // ----       Utilities         ----
    // ---------------------------------
    
    /**
     * Endpoint to access dbs.
     *
     * @return
     *      database endpoint
     */
    public String getEndpointToken() {
        return getEndpointToken(tokenId);
    }
    
    /**
     * Endpoint to access dbs (static)
     *
     * @param dbId
     *      database identifer
     * @return
     *      database endpoint
     */
    public static String getEndpointToken(String tokenId) {
        return OrganizationsClient.getApiDevopsEndpointTokens() + "/" + tokenId;
    }

}
