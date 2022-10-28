package org.datastax.astra.sdk.organizations;

import org.datastax.astra.sdk.HttpClientWrapper;
import org.datastax.astra.sdk.domain.IamToken;
import org.datastax.astra.sdk.utils.Assert;

import java.util.Optional;

/**
 * Client for resource '/clientIdSecret' 
 * 
 * @author Cedrick LUNVEN (@clunven)
 */
public class TokenClient {
    
    /** Working role. */
    private final String tokenId;

    /** Wrapper handling header and error management as a singleton. */
    private final HttpClientWrapper http = HttpClientWrapper.getInstance();

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
     *      role information
     */
    public Optional<IamToken> find() {
        return orgClient.tokens()
                        .filter(t -> t.getClientId().equalsIgnoreCase(tokenId))
                        .findFirst();
    }
    
    /**
     * Check in existence of a token.
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
     *      token identifier
     * @return
     *      token endpoint
     */
    public static String getEndpointToken(String tokenId) {
        return OrganizationsClient.getApiDevopsEndpointTokens() + "/" + tokenId;
    }

}
