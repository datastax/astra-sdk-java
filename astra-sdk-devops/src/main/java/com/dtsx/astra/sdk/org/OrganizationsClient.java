package com.dtsx.astra.sdk.org;

import com.dtsx.astra.sdk.HttpClientWrapper;
import com.dtsx.astra.sdk.db.domain.CloudProviderType;
import com.dtsx.astra.sdk.db.domain.DatabaseRegion;
import com.dtsx.astra.sdk.db.domain.DatabaseRegionServerless;
import com.dtsx.astra.sdk.org.domain.*;
import com.dtsx.astra.sdk.org.iam.RoleClient;
import com.dtsx.astra.sdk.org.iam.TokenClient;
import com.dtsx.astra.sdk.org.iam.UserClient;
import com.dtsx.astra.sdk.utils.*;
import com.fasterxml.jackson.core.type.TypeReference;

import java.util.*;
import java.util.stream.Stream;

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
    public static final String PATH_REGIONS_SERVERLESS = "/regions/serverless";
    
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
    
    /** Resource suffix. */ 
    public static final String PATH_KEYS = "/kms";
    
    /** List of regions. */
    public static final TypeReference<List<DatabaseRegion>> TYPE_LIST_REGION =
            new TypeReference<List<DatabaseRegion>>(){};
            
    /** List of Roles. */
    public static final TypeReference<List<Role>> TYPE_LIST_ROLES =
            new TypeReference<List<Role>>(){};
            
    /** List of Keys. */
    public static final TypeReference<List<Key>> TYPE_LIST_KEYS =
            new TypeReference<List<Key>>(){};            
    
    /** Wrapper handling header and error management as a singleton. */
    private final HttpClientWrapper http = HttpClientWrapper.getInstance();
    
    /** hold a reference to the bearer token. */
    protected final String bearerAuthToken;
    
    /**
     * As immutable object use builder to initiate the object.
     * 
     * @param bearerAuthToken
     *      authenticated token
     */
    public OrganizationsClient(String bearerAuthToken) {
       this.bearerAuthToken = bearerAuthToken;
       Assert.hasLength(bearerAuthToken, "bearerAuthToken");
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
        ApiResponseHttp res = http.GET(ApiLocator.getApiDevopsEndpoint() + PATH_CURRENT_ORG, bearerAuthToken);
        // Parse response
        return (String) JsonUtils.unmarshallBean(res.getBody(),  Map.class).get("id");
    }
    
    /**
     * Retrieve the organization wth current token.
     *
     * @return
     *      current organization
     */
    public Organization organization() {
        // Invoke endpoint
        ApiResponseHttp  res  = http.GET(getApiDevopsEndpointUsers(), bearerAuthToken);
        // Marshalling the all users response to get org infos
        ResponseAllUsers body = JsonUtils.unmarshallBean(res.getBody(), ResponseAllUsers.class);
        // Build a proper result
        return new Organization(body.getOrgId(), body.getOrgName());
    }
     
    /**
     * Returns supported regions and availability for a given user and organization
     * 
     * @return
     *      supported regions and availability 
     */
    public Stream<DatabaseRegion> regions() {
        // Invoke endpoint
        ApiResponseHttp res = http.GET(ApiLocator.getApiDevopsEndpoint() + PATH_REGIONS, bearerAuthToken);
        // Marshall response
        return JsonUtils.unmarshallType(res.getBody(), TYPE_LIST_REGION).stream();
    }
    
    /**
     * List serverless regions.
     * 
     * @return
     *      serverless region
     */
    public Stream<DatabaseRegionServerless> regionsServerless() {
        // Invoke endpoint
        ApiResponseHttp res = http.GET(ApiLocator.getApiDevopsEndpoint() + PATH_REGIONS_SERVERLESS, bearerAuthToken);
        // Marshall response
        return JsonUtils.unmarshallType(res.getBody(), new TypeReference<List<DatabaseRegionServerless>>(){}).stream();
    }
    
    /**
     * Map regions from plain list to Tier/Cloud/Region Structure.
     *
     * @return
     *      regions organized by cloud providers
     */
    public Map <String, Map<CloudProviderType,List<DatabaseRegion>>> regionsMap() {
        Map<String, Map<CloudProviderType,List<DatabaseRegion>>> m = new HashMap<>();
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
        ApiResponseHttp res = http.GET(getApiDevopsEndpointUsers(), bearerAuthToken);
        // Marshall response
        return JsonUtils.unmarshallBean(res.getBody(), ResponseAllUsers.class).getUsers().stream();
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
        http.PUT(getApiDevopsEndpointUsers(), bearerAuthToken, JsonUtils.marshall(inviteRequest));
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
        ApiResponseHttp res = http.GET(getApiDevopsEndpointRoles(), bearerAuthToken);
        // Mapping
        return JsonUtils.unmarshallType(res.getBody(), TYPE_LIST_ROLES).stream();
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
        ApiResponseHttp res = http.POST(getApiDevopsEndpointRoles(), bearerAuthToken, JsonUtils.marshall(cr));
        return JsonUtils.unmarshallBean(res.getBody(), CreateRoleResponse.class);
    }
    
    /**
     * Retrieve a suer from his email.
     * 
     * @param role
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
        ApiResponseHttp res = http.GET(getApiDevopsEndpointTokens(), bearerAuthToken);
        // Marshall
        return JsonUtils.unmarshallBean(res.getBody(), ResponseAllIamTokens.class).getClients().stream();
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
        ApiResponseHttp res = http.POST(getApiDevopsEndpointTokens(), bearerAuthToken, body);
        // Marshall response
        return JsonUtils.unmarshallBean(res.getBody(), CreateTokenResponse.class);
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
     * @return
     *      access lists template
     * 
     * https://docs.datastax.com/en/astra/docs/_attachments/devopsv2.html#operation/GetAccessListTemplate
     */
    public Object accessListTemplate() {
        throw new RuntimeException("This function is not yet implemented");
    }
    
    /**
     * TODO Validate structure of an access list
     *
     * @return
     *      validateAccessList
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
    
    // ------------------------------------------------------
    //                WORKING WITH CUSTOM KEYS
    // ------------------------------------------------------
    
    /**
     * List keys in a Organizations.
     * 
     * @return
     *      list of keys in target organization.
     */
    public Stream<Key> keys() {
        // Invoke endpoint
        ApiResponseHttp res = http.GET(getApiDevopsEndpointKeys(), bearerAuthToken);
        // Mapping
        return JsonUtils.unmarshallType(res.getBody(), TYPE_LIST_KEYS).stream();
    }
    
    /**
     * Create a new key.
     * 
     * @param keyDef
     *      key definition request
     * @return
     *      new role created
     */
    public Object createKey(KeyDefinition keyDef) {
        Assert.notNull(keyDef, "CreateRole request");
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
        return new TokenClient(this, tokenId);
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
        return new RoleClient(this, roleId);
    }
    
    /**
     * Helper to find default Roles
     *
     * @param role
     *      list of roles
     * @return
     *      role client
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
     * Endpoint to access a key.
     *
     * @return
     *      endpoint
     */
    public static String getApiDevopsEndpointKeys() {
        return ApiLocator.getApiDevopsEndpoint() + PATH_KEYS;
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
