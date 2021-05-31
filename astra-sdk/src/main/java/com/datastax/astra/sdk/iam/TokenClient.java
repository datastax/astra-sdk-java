package com.datastax.astra.sdk.iam;

import static com.datastax.stargate.sdk.core.ApiSupport.handleError;

import java.net.HttpURLConnection;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.Optional;

import com.datastax.astra.sdk.iam.domain.IamToken;
import com.datastax.astra.sdk.utils.ApiDevopsSupport;
import com.datastax.stargate.sdk.utils.Assert;

/**
 * Client for resource '/clientIdSecret' 
 * 
 * @author Cedrick LUNVEN (@clunven)
 */
public class TokenClient extends ApiDevopsSupport {
    
    /** Reference to IAM to list tokens. */
    private IamClient iamClient;
    
    /** Working role. */
    private final String tokenId;
    
    /** Current url. */
    private final String resourceSuffix;
    
    /**
     * Constructor for immutability
     * 
     * @param cli
     *      client to work with IAM
     * @param token
     *      authenticated token
     * @param tokenId
     *      unique token
     */
    public TokenClient(IamClient cli, String token, String tokenId) {
        super(token);
        this.iamClient      = cli;
        this.tokenId        = tokenId;
        this.resourceSuffix = IamClient.PATH_TOKENS + "/" + tokenId;
        Assert.hasLength(tokenId, "tokenId");
    }
    
    /**
     * Constructor with od
     * @param token
     *      authenticated token
     * @param tokenId
     *      unique token id
     */
    public TokenClient(String token, String tokenId) {
        this(null, token, tokenId);
    }
    
    /**
     * Retrieve role information from its id.
     *
     * @return
     *      role informations
     */
    public Optional<IamToken> find() {
        if (iamClient == null ) {
            iamClient = new IamClient(bearerAuthToken);
        }
        return iamClient.tokens()
                        .filter(t -> t.getClientId().equalsIgnoreCase(tokenId))
                        .findFirst();
    }
    
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
        HttpResponse<String> response;
        try {
            response = getHttpClient().send(
                    startRequest(resourceSuffix).DELETE().build(), 
                    BodyHandlers.ofString()
            );
            if (HttpURLConnection.HTTP_NO_CONTENT == response.statusCode()) {
                return;
            }
        } catch (Exception e) {
            throw new RuntimeException("Cannot invoke API to delete a document:", e);
        }
        handleError(response);
    }

}
