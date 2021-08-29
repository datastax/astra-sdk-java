package com.datastax.astra.sdk.organizations;

import static com.datastax.astra.sdk.utils.ApiLocator.getApiDevopsEndpoint;
import static com.datastax.stargate.sdk.utils.JsonUtils.marshall;
import static com.datastax.stargate.sdk.utils.JsonUtils.unmarshallBean;
import static com.datastax.stargate.sdk.utils.JsonUtils.unmarshallType;

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
import com.datastax.astra.sdk.utils.ApiLocator;
import com.datastax.astra.sdk.utils.IdUtils;
import com.datastax.stargate.sdk.core.ApiResponseHttp;
import com.datastax.stargate.sdk.utils.Assert;
import com.datastax.stargate.sdk.utils.HttpApisClient;
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
public class OrganizationsClient {
    
    /** Get Available Regions. */
    public static final String PATH_REGIONS = "/availableRegions";
    
    /** Get Available Regions. */
    public static final String PATH_ACCESS_LISTS = "/access-lists";
   
    /** Core Organization. */
    public static final String PATH_CURRENT_ORG = "/currentOrg";
    
    /** Constants. */
    public static final String PATH_ORGANIZATIONS = "/organizations";
    
    /** List clients. */
    public static final String PATH_TOKENS = "/clientIdSecrets";
    
    /** Path related to Roles. */
    public static final String PATH_ROLES = "/roles";
    
    /** Resource suffix. */ 
    public static final String PATH_USERS = "/users";
    
    /** List of regions. */
    public static final TypeReference<List<DatabaseRegion>> TYPE_LIST_REGION = 
            new TypeReference<List<DatabaseRegion>>(){};
            
    /** List of Roles. */
    public static final TypeReference<List<Role>> TYPE_LIST_ROLES = 
            new TypeReference<List<Role>>(){};
    
    /** Wrapper handling header and error management as a singleton. */
    private final HttpApisClient http;
    
    /** hold a reference to the bearer token. */
    private final String bearerAuthToken;
    
    /**
     * As immutable object use builder to initiate the object.
     * 
     * @param authToken
     *      authenticated token
     */
    public OrganizationsClient(HttpApisClient client) {
        this.http = client;
        this.bearerAuthToken = client.getToken();
    }
    
    /**
     * As immutable object use builder to initiate the object.
     * 
     * @param authToken
     *      authenticated token
     */
    public OrganizationsClient(String bearerAuthToken) {
       this.bearerAuthToken = bearerAuthToken;
       this.http = HttpApisClient.getInstance();
       http.setToken(bearerAuthToken);
    } 
    
    // ------------------------------------------------------
    //                 CORE FEATURES
    // ------------------------------------------------------
    
    /**
     * Retrieve Organization id.
     *
     * @return
     *      organization id.
     */
    public String organizationId() {
        // Invoke endpoint
        ApiResponseHttp res = http.GET(getApiDevopsEndpoint()+ PATH_CURRENT_ORG);
        // Parse response
        return (String) unmarshallBean(res.getBody(),  Map.class).get("id");
    }
     
    /**
     * Returns supported regions and availability for a given user and organization
     * 
     * @return
     *      supported regions and availability 
     */
    public Stream<DatabaseRegion> regions() {
        // Invoke endpoint
        ApiResponseHttp res = http.GET(getApiDevopsEndpoint() + PATH_REGIONS);
        // Marshall response
        return unmarshallType(res.getBody(), TYPE_LIST_REGION).stream();
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
        // Invoke endpoint
        ApiResponseHttp res = http.GET(getApiDevopsEndpointUsers());
        // Marshall response
        return unmarshallBean(res.getBody(), ResponseAllUsers.class).getUsers().stream();
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
     *
     * @param email
     *      user email
     * @param roles
     *      list of roles to assign
     */
    public void inviteUser(String email, String... roles) {
        // Parameter validattion
        Assert.notNull(email, "User email");
        Assert.notNull(roles, "User roles");
        Assert.isTrue(roles.length > 0, "Roles list cannot be empty");
        
        // Build the invite request with expected roles
        InviteUserRequest inviteRequest = new InviteUserRequest(this.organizationId(), email);
        Arrays.asList(roles).stream().forEach(currentRole -> {
           if (IdUtils.isUUID(currentRole)) {
               inviteRequest.addRoles(currentRole);
           } else {
               // If role provided is a role name...
               Optional<Role> opt = findRoleByName(currentRole);
               if (opt.isPresent()) {
                   inviteRequest.addRoles(opt.get().getId());
               } else {
                   throw new IllegalArgumentException("Cannot find role with name " + currentRole);
               }
           }
        });
        
        // Invoke HTTP
        http.PUT(getApiDevopsEndpointUsers(), marshall(inviteRequest));
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
        // Invoke endpoint
        ApiResponseHttp res = http.GET(getApiDevopsEndpointRoles());
        // Mapping
        return unmarshallType(res.getBody(), TYPE_LIST_ROLES).stream();
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
        // Invoke endpoint
        ApiResponseHttp res = http.POST(getApiDevopsEndpointRoles(), marshall(cr));
        return unmarshallBean(res.getBody(), CreateRoleResponse.class);
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
        // Invoke endpoint
        ApiResponseHttp res = http.GET(getApiDevopsEndpointTokens());
        // Marshall
        return unmarshallBean(res.getBody(), ResponseAllIamTokens.class).getClients().stream();
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
        // Building request
        String body = "{ \"roles\": [ \"" + JsonUtils.escapeJson(role) + "\"]}";
        // Invoke endpoint
        ApiResponseHttp res = http.POST(getApiDevopsEndpointTokens(), body);
        // Marshall response
        return unmarshallBean(res.getBody(), CreateTokenResponse.class);
    }
    
    // ---------------------------------
    // ----      ACCESS LISTS       ----
    // ---------------------------------
    
    /**
     * TODO Get all access lists for an organization
     * 
     * https://docs.datastax.com/en/astra/docs/_attachments/devopsv2.html#operation/GetAllAccessListsForOrganization
     *
     * @return
     *      access lists for an organization
     */
    public Stream<Object> accessLists() {
        throw new RuntimeException("This function is not yet implemented");
    }
    
    /**
     * TODO Get access list template
     * 
     * https://docs.datastax.com/en/astra/docs/_attachments/devopsv2.html#operation/GetAccessListTemplate
     */
    public Object accessListTemplate() {
        throw new RuntimeException("This function is not yet implemented");
    }
    
    /**
     * TODO Validate structure of an access list
     * 
     * https://docs.datastax.com/en/astra/docs/_attachments/devopsv2.html#operation/ValidateAccessList
     */
    public Object validateAccessList() {
        throw new RuntimeException("This function is not yet implemented");
    }
    
    // ---------------------------------
    // ----      PRIVATE LINKS      ----
    // ---------------------------------
    
    /**
     * TODO Get info about all private endpoint connections for a specific org
     * 
     * https://docs.datastax.com/en/astra/docs/_attachments/devopsv2.html#operation/ListPrivateLinksForOrg
     *
     * @return
     *      private endpoint connections for a specific org
     */
    public Stream<Object> privateLinks() {
        throw new RuntimeException("This function is not yet implemented");
    }
    
    // ---------------------------------
    // ---- Accessing sub resources ----
    // ---------------------------------
    
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
        return new UserClient(this, userId);
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
        return new TokenClient(this , tokenId);
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
        return new RoleClient(roleId);
    }
    
    /**
     * Helper to find default Roles
     */
    public RoleClient role(DefaultRoles role) {
        Assert.notNull(role, "Role  should not be null nor empty");
        return role(findRole(role).get().getId());
    }
    
    // ---------------------------------
    // ----       Utilities         ----
    // ---------------------------------
    
    /**
     * Endpoint to access schema for namespace.
     *
     * @return
     *      endpoint
     */
    public static String getApiDevopsEndpointTokens() {
        return ApiLocator.getApiDevopsEndpoint() + PATH_TOKENS;
    }
    
    /**
     * Endpoint to access schema for namespace.
     *
     * @return
     *      endpoint
     */
    public static String getApiDevopsEndpointCurrentOrganization() {
        return ApiLocator.getApiDevopsEndpoint() + PATH_CURRENT_ORG;
    }
    
    /**
     * Endpoint to access schema for namespace.
     *
     * @return
     *      endpoint
     */
    public static String getApiDevopsEndpointAvailableRegions() {
        return ApiLocator.getApiDevopsEndpoint() + PATH_REGIONS;
    }
    
    /**
     * Endpoint to access schema for namespace.
     *
     * @return
     *      endpoint
     */
    public static String getApiDevopsEndpointUsers() {
        return ApiLocator.getApiDevopsEndpoint() + PATH_ORGANIZATIONS + PATH_USERS;
    }
    
    /**
     * Endpoint to access schema for namespace.
     *
     * @return
     *      endpoint
     */
    public static String getApiDevopsEndpointRoles() {
        return ApiLocator.getApiDevopsEndpoint() + PATH_ORGANIZATIONS + PATH_ROLES;
    }
    
    /**
     * Access to the current authentication token.
     *
     * @return
     *      authentication token
     */
    public String getToken() {
        return bearerAuthToken;
    }

}
