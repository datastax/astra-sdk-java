package com.datastax.astra.sdk.iam;

import static com.datastax.stargate.sdk.core.ApiSupport.handleError;

import java.net.HttpURLConnection;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import com.datastax.astra.sdk.iam.domain.CreateRoleResponse;
import com.datastax.astra.sdk.iam.domain.CreateTokenResponse;
import com.datastax.astra.sdk.iam.domain.IamToken;
import com.datastax.astra.sdk.iam.domain.InviteUserRequest;
import com.datastax.astra.sdk.iam.domain.Role;
import com.datastax.astra.sdk.iam.domain.RoleDefinition;
import com.datastax.astra.sdk.iam.domain.ResponseAllIamTokens;
import com.datastax.astra.sdk.iam.domain.ResponseAllUsers;
import com.datastax.astra.sdk.iam.domain.User;
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
     * Default Constructor
     * 
     * @param token
     *          authenticated token
     */
    public IamClient(String token) {
        super(token);
    }
    
    /**
     * Retrieve Organization id.
     *
     * @return
     *      organization id.
     */
    public String organizationId() {
        HttpResponse<String> res;
        try {
            // Invocation (no marshalling yet)
            res = http().send(req(PATH_CURRENT_ORG)
                        .GET().build(), BodyHandlers.ofString());
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        
        handleError(res);
        
        try {
            return (String) om()
                        .readValue(res.body(), Map.class)
                        .get("id");
        } catch (Exception e) {
            throw new RuntimeException("Cannot marshall organization id", e);
        }
    }
    
    /**
     * List users in organization.
     * 
     * @return
     *      list of roles in target organization.
     */
    public Stream<User> users() {
        HttpResponse<String> res;
        try {
            // Invocation (no marshalling yet)
            res = http().send(req(PATH_ORGANIZATIONS + UserClient.PATH_USERS)
                        .GET().build(), BodyHandlers.ofString());
            if (HttpURLConnection.HTTP_OK == res.statusCode()) {
                return om().readValue(res.body(), ResponseAllUsers.class)
                                        .getUsers().stream();
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        LOGGER.error("Error in 'roles'");
        throw processErrors(res);
    }
    
    /**
     * Invite a user.
     * @param email
     *      user email
     * @param roles
     *      list of roles to assign
     */
    public void inviteUser(String email, String... roles) {
        Assert.notNull(email, "User email");
        Assert.notNull(roles, "User roles");
        
        if (roles.length == 0) {
            throw new IllegalArgumentException("Roles list cannot be empty");
        }
        
        // Validate roles ids
        Arrays.asList(roles).stream().forEach(r -> {
           if (!role(r).exist()) { 
               throw new IllegalArgumentException("Cannot find role with id " + r);
           }
        });
        
        HttpResponse<String> response;
        try {
           InviteUserRequest iur = new InviteUserRequest(this.organizationId(), email);
           iur.addRoles(roles);
           String reqBody = om().writeValueAsString(iur);
           response = http().send(req(PATH_ORGANIZATIONS + UserClient.PATH_USERS)
                            .PUT(BodyPublishers.ofString(reqBody)).build(),
                   BodyHandlers.ofString());
        } catch (Exception e) {
            throw new RuntimeException("Cannot create a new role", e);
        }
        handleError(response);
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
            res = http().send(req(PATH_ORGANIZATIONS + RoleClient.PATH_ROLES)
                        .GET().build(), BodyHandlers.ofString());
            if (HttpURLConnection.HTTP_OK == res.statusCode()) {
                return om().readValue(res.body(), new TypeReference<List<Role>>(){})
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
           String reqBody = om().writeValueAsString(cr);
           response = http().send(req(PATH_ORGANIZATIONS + RoleClient.PATH_ROLES)
                            .POST(BodyPublishers.ofString(reqBody)).build(),
                   BodyHandlers.ofString());
        } catch (Exception e) {
            throw new RuntimeException("Cannot create a new role", e);
        }
        
        handleError(response);
        
        try {
            return om().readValue(response.body(), CreateRoleResponse.class);
        } catch (Exception e) {
            throw new RuntimeException("Cannot marshall new role", e);
        }
    }
    
    /**
     * Move the document API (namespace client)
     * 
     * @param roleId
     *      unique identifier for the role
     * @return
     *      role rest client
     */
    public RoleClient role(String roleId) {
        Assert.hasLength(roleId, "Role Id should not be null nor empty");
        return new RoleClient(bearerAuthToken, roleId);
    }
    
    /**
     * List tokens
     *
     * @return
     *      list of tokens for this organization
     */
    public Stream<IamToken> tokens() {
        HttpResponse<String> res;
        try {
            // Invocation (no marshalling yet)
            res = http()
                    .send(req(PATH_TOKENS)
                    .GET().build(), BodyHandlers.ofString());
            if (HttpURLConnection.HTTP_OK == res.statusCode()) {
                return om()
                        .readValue(res.body(), ResponseAllIamTokens.class)
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
     * 
     * @param tokenId
     *      token identifier
     * @return
     *      rest client for a token
     */
    public TokenClient token(String tokenId) {
        return new TokenClient(this, bearerAuthToken, tokenId);
    }
    
    /**
     * Create token
     *
     * @param role
     *      create a token with dedicated role
     * @return
     *      created token
     */
    public CreateTokenResponse createToken(String role) {
        Assert.hasLength(role, "role");
        HttpResponse<String> response;
        try {
           response = http().send(
                   req(PATH_TOKENS)
                   .POST(BodyPublishers.ofString("{"
                           + " \"roles\": [ \"" 
                           + JsonUtils.escapeJson(role) 
                           + "\"]}")).build(), BodyHandlers.ofString());
        } catch (Exception e) {
            throw new RuntimeException("Cannot create a new token", e);
        }
        
        handleError(response);
        
        try {
            return om().readValue(response.body(), CreateTokenResponse.class);
        } catch (Exception e) {
            throw new RuntimeException("Cannot marshall new token", e);
        }
    }
    

}
