package com.datastax.astra.sdk.iam;

import static com.datastax.stargate.sdk.core.ApiSupport.handleError;

import java.net.HttpURLConnection;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import com.datastax.astra.sdk.iam.domain.CreateRoleResponse;
import com.datastax.astra.sdk.iam.domain.CreateTokenResponse;
import com.datastax.astra.sdk.iam.domain.IamToken;
import com.datastax.astra.sdk.iam.domain.Role;
import com.datastax.astra.sdk.iam.domain.RoleDefinition;
import com.datastax.astra.sdk.iam.domain.TokensResponse;
import com.datastax.astra.sdk.utils.ApiDevopsSupport;
import com.datastax.stargate.sdk.utils.Assert;
import com.datastax.stargate.sdk.utils.JsonUtils;
import com.fasterxml.jackson.core.type.TypeReference;

/**
 * Group resources of organizations.
 *
 * @author Cedrick LUNVEN (@clunven)
 */
public class IamClient extends ApiDevopsSupport {
    
    /** Constants. */
    public static final String PATH_ORGANIZATIONS  = "/organizations";
    public static final String PATH_CURRENT_ORG    = "/currentOrg";
    public static final String PATH_TOKENS         = "/clientIdSecrets";
    
    /**
     * Full constructor.
     */
    public IamClient(String token) {
        super(token);
    }
    
    public String organizationId() {
        HttpResponse<String> res;
        try {
            // Invocation (no marshalling yet)
            res = getHttpClient()
                    .send(startRequest(PATH_CURRENT_ORG)
                    .GET().build(), BodyHandlers.ofString());
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        
        handleError(res);
        
        try {
            return (String) getObjectMapper()
                        .readValue(res.body(), Map.class)
                        .get("id");
        } catch (Exception e) {
            throw new RuntimeException("Cannot marshall organization id", e);
        }
    }
    
    /**
     * List roles in a Organizations.
     * 
     * @return
     *      list of roles in target organization.
     */
    public Stream<Role> roles() {
        HttpResponse<String> res;
        try {
            // Invocation (no marshalling yet)
            res = getHttpClient()
                    .send(startRequest(PATH_ORGANIZATIONS + RoleClient.PATH_ROLES)
                    .GET().build(), BodyHandlers.ofString());
            if (HttpURLConnection.HTTP_OK == res.statusCode()) {
                return getObjectMapper()
                        .readValue(res.body(), new TypeReference<List<Role>>(){})
                        .stream();
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        LOGGER.error("Error in 'roles'");
        throw processErrors(res);
    }
    
    /**
     * Create a new role.
     * 
     * @param cr
     *      new role request
     * @return
     *      new role created
     */
    public CreateRoleResponse createRole(RoleDefinition cr) {
        Assert.notNull(cr, "CreateRole request");
        HttpResponse<String> response;
        try {
           String reqBody = getObjectMapper().writeValueAsString(cr);
           response = getHttpClient().send(
                   startRequest(PATH_ORGANIZATIONS + RoleClient.PATH_ROLES)
                   .POST(BodyPublishers.ofString(reqBody)).build(),
                   BodyHandlers.ofString());
        } catch (Exception e) {
            throw new RuntimeException("Cannot create a new role", e);
        }
        
        handleError(response);
        
        try {
            return getObjectMapper().readValue(response.body(), CreateRoleResponse.class);
        } catch (Exception e) {
            throw new RuntimeException("Cannot marshall new role", e);
        }
    }
    
    /**
     * Move the document API (namespace client)
     */
    public RoleClient role(String roleId) {
        Assert.hasLength(roleId, "Role Id should not be null nor empty");
        return new RoleClient(bearerAuthToken, roleId);
    }
    
    /**
     * List tokens
     */
    public Stream<IamToken> tokens() {
        HttpResponse<String> res;
        try {
            // Invocation (no marshalling yet)
            res = getHttpClient()
                    .send(startRequest(PATH_TOKENS)
                    .GET().build(), BodyHandlers.ofString());
            if (HttpURLConnection.HTTP_OK == res.statusCode()) {
                return getObjectMapper()
                        .readValue(res.body(), TokensResponse.class)
                        .getClients()
                        .stream();
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        LOGGER.error("Error in 'clientIdSecrets'");
        throw processErrors(res);
    }
    
    /**
     * Move to token resource.
     */
    public TokenClient token(String tokenId) {
        return new TokenClient(this, bearerAuthToken, tokenId);
    }
    
    /**
     * Create token
     */
    public CreateTokenResponse createToken(String role) {
        Assert.hasLength(role, "role");
        HttpResponse<String> response;
        try {
           response = getHttpClient().send(
                   startRequest(PATH_TOKENS)
                   .POST(BodyPublishers.ofString("{"
                           + " \"roles\": [ \"" 
                           + JsonUtils.escapeJson(role) 
                           + "\"]}")).build(), BodyHandlers.ofString());
        } catch (Exception e) {
            throw new RuntimeException("Cannot create a new token", e);
        }
        
        handleError(response);
        
        try {
            return getObjectMapper().readValue(response.body(), CreateTokenResponse.class);
        } catch (Exception e) {
            throw new RuntimeException("Cannot marshall new token", e);
        }
    }
    

}
