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
    private final HttpApisClient http = HttpApisClient.getInstance();
    
    /** reference to organization. */
    private final OrganizationsClient orgClient;
    
    /**
     * Default constructor.
     *
     * @param org
     *      organization client
     * @param tokenId
     *      current token identifier
     */
    public TokenClient(OrganizationsClient org, String tokenId) {
        this.tokenId   = tokenId;
        this.orgClient = org;
        Assert.hasLength(tokenId, "tokenId");
    }
    
    /**
     * Retrieve role information from its id.
     *
     * @return
     *      role informations
     */
    public Optional<IamToken> find() {
        return orgClient.tokens()
                        .filter(t -> t.getClientId().equalsIgnoreCase(tokenId))
                        .findFirst();
    }
    
    /**
     * Check in inexistence of a token.
     * 
     * @return
     *      if the provided token exist
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
        http.DELETE(getEndpointToken(), orgClient.bearerAuthToken);
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
     * @param tokenId
     *      token identifer
     * @return
     *      token endpoint
     */
    public static String getEndpointToken(String tokenId) {
        return OrganizationsClient.getApiDevopsEndpointTokens() + "/" + tokenId;
    }

}
