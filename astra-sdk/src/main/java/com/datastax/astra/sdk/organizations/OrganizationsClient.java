package com.datastax.astra.sdk.organizations;


import static com.datastax.stargate.sdk.core.ApiSupport.handleError;

import java.net.HttpURLConnection;
import java.net.http.HttpResponse;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import com.datastax.astra.sdk.databases.domain.CloudProviderType;
import com.datastax.astra.sdk.databases.domain.DatabaseRegion;
import com.datastax.astra.sdk.databases.domain.DatabaseTierType;
import com.datastax.astra.sdk.organizations.domain.CreateRoleResponse;
import com.datastax.astra.sdk.organizations.domain.CreateTokenResponse;
import com.datastax.astra.sdk.organizations.domain.DefaultRoles;
import com.datastax.astra.sdk.organizations.domain.IamToken;
import com.datastax.astra.sdk.organizations.domain.InviteUserRequest;
import com.datastax.astra.sdk.organizations.domain.ResponseAllIamTokens;
import com.datastax.astra.sdk.organizations.domain.ResponseAllUsers;
import com.datastax.astra.sdk.organizations.domain.Role;
import com.datastax.astra.sdk.organizations.domain.RoleDefinition;
import com.datastax.astra.sdk.organizations.domain.User;
import com.datastax.astra.sdk.utils.ApiDevopsSupport;
import com.datastax.astra.sdk.utils.IdUtils;
import com.datastax.stargate.sdk.utils.Assert;
import com.datastax.stargate.sdk.utils.JsonUtils;
import com.fasterxml.jackson.core.type.TypeReference;

/**
 * Client for the Astra Devops API.
 * 
 * The JDK11 client http is used and as such jdk11+ is required
 * 
 * https://docs.datastax.com/en/astra/docs/_attachments/devopsv1.html
 * 
 * @author Cedrick LUNVEN (@clunven)
 */
/**
 * Class to TODO
 *
 * @author Cedrick LUNVEN (@clunven)
 *
 */
public class OrganizationsClient extends ApiDevopsSupport {
    
    /** Get Available Regions. */
    public static final String PATH_REGIONS      = "/availableRegions";
    
    /** Get Available Regions. */
    public static final String PATH_ACCESS_LISTS = "/access-lists";
   
    /** Core Organization. */
    public static final String PATH_CURRENT_ORG    = "/currentOrg";
    
    /** Constants. */
    public static final String PATH_ORGANIZATIONS  = "/organizations";
    
    /** List clients. */
    public static final String PATH_TOKENS         = "/clientIdSecrets";
    
    /**
     * As immutable object use builder to initiate the object.
     * 
     * @param authToken
     *      authenticated token
     */
    public OrganizationsClient(String authToken) {
       super(authToken);
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
     * Returns supported regions and availability for a given user and organization
     * 
     * @return
     *      supported regions and availability 
     */
    public Stream<DatabaseRegion> regions() {
        HttpResponse<String> res;
        try {
           // Invocation with no marshalling
           res = http().send(
                   req(PATH_REGIONS).GET().build(), 
                    BodyHandlers.ofString());
            
            // Parsing as list of Bean if OK
            if (HttpURLConnection.HTTP_OK == res.statusCode()) {
                return  om().readValue(res.body(),
                        new TypeReference<List<DatabaseRegion>>(){})
                                   .stream();
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Cannot list regions", e);
        }
        
        LOGGER.error("Error in 'availableRegions'");
        throw processErrors(res);
    }
    
    /**
     * Map regions from plain list to Tier/Cloud/Region Structure.
     *
     * @return
     *      regions organized by cloud providers
     */
    public Map <DatabaseTierType, Map<CloudProviderType,List<DatabaseRegion>>> regionsMap() {
        Map<DatabaseTierType, Map<CloudProviderType,List<DatabaseRegion>>> m = new HashMap<>();
        regions().forEach(dar -> {
            if (!m.containsKey(dar.getTier())) {
                m.put(dar.getTier(), new HashMap<CloudProviderType,List<DatabaseRegion>>());
            }
            if (!m.get(dar.getTier()).containsKey(dar.getCloudProvider())) {
                m.get(dar.getTier()).put(dar.getCloudProvider(), new ArrayList<DatabaseRegion>());
            }
            m.get(dar.getTier()).get(dar.getCloudProvider()).add(dar);
        });
        return m;
    }
    
 // ------------------------------------------------------
    //                 WORKING WITH USERS
    // ------------------------------------------------------
    
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
     * Specialized a client for user.
     *
     * @param userId
     *      current identifier
     * @return
     *      client for a user
     */
    public UserClient user(String userId) {
        Assert.hasLength(userId, "userId Id should not be null nor empty");
        return new UserClient(this, bearerAuthToken, userId);
    }
    
    /**
     * Retrieve a suer from his email.
     * 
     * @param email
     *      user email
     * @return
     *      user iif exist
     */
    public Optional<User> findUserByEmail(String email) {
        Assert.hasLength(email, "User email should not be null nor empty");
        return users().filter(u-> u.getEmail().equalsIgnoreCase(email)).findFirst();
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
        
        InviteUserRequest iur = new InviteUserRequest(this.organizationId(), email);
        Arrays.asList(roles).stream().forEach(currentRole -> {
           if (IdUtils.isUUID(currentRole)) {
               iur.addRoles(currentRole);
           } else {
               Optional<Role> opt = findRoleByName(currentRole);
               if (opt.isPresent()) {
                   iur.addRoles(opt.get().getId());
               } else {
                   throw new IllegalArgumentException("Cannot find role with id " + currentRole);
               }
           }
        });
        
        HttpResponse<String> response;
        try {
           String reqBody = om().writeValueAsString(iur);
           response = http().send(req(PATH_ORGANIZATIONS + UserClient.PATH_USERS)
                            .PUT(BodyPublishers.ofString(reqBody)).build(),
                   BodyHandlers.ofString());
        } catch (Exception e) {
            throw new RuntimeException("Cannot create a new role", e);
        }
        handleError(response);
    }
    
    
    // ------------------------------------------------------
    //                 WORKING WITH ROLES
    // ------------------------------------------------------
    
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
     * Helper to find default Roles
     */
    public RoleClient role(DefaultRoles role) {
        Assert.notNull(role, "Role  should not be null nor empty");
        String id = findRole(role).get().getId();
        System.out.println(id);
        return new RoleClient(bearerAuthToken, findRole(role).get().getId());
    }
    
    /**
     * Retrieve a suer from his email.
     * 
     * @param roleName
     *      role name
     * @return
     *      user iif exist
     */
    public Optional<Role> findRole(DefaultRoles role) {
        return findRoleByName(role.getName());
    }
    
    /**
     * Retrieve a suer from his email.
     * 
     * @param roleName
     *      role name
     * @return
     *      user iif exist
     */
    public Optional<Role> findRoleByName(String roleName) {
        Assert.hasLength(roleName, "User email should not be null nor empty");
        return roles().filter(r-> r.getName().equalsIgnoreCase(roleName)).findFirst();
    }
    
    // ------------------------------------------------------
    //                 WORKING WITH TOKENS
    // ------------------------------------------------------
    
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
