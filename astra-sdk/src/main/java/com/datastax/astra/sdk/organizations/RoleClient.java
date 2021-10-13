package com.datastax.astra.sdk.organizations;

import static com.datastax.stargate.sdk.utils.JsonUtils.marshall;

import java.net.HttpURLConnection;
import java.util.Optional;

import com.datastax.astra.sdk.organizations.domain.Role;
import com.datastax.astra.sdk.organizations.domain.RoleDefinition;
import com.datastax.stargate.sdk.core.ApiResponseHttp;
import com.datastax.stargate.sdk.utils.Assert;
import com.datastax.stargate.sdk.utils.HttpApisClient;
import com.datastax.stargate.sdk.utils.JsonUtils;


/**
 * Working with the Role part of the devop API.
 *
 * @author Cedrick LUNVEN (@clunven)
 */
public class RoleClient {
   
    /** Working role. */
    private final String roleId;
  
    /** Wrapper handling header and error management as a singleton. */
    private final HttpApisClient http = HttpApisClient.getInstance();
    
    /** reference to organization. */
    private final OrganizationsClient orgClient;
    
    /**
     * Default constructor.
     *
     * @param roleId
     *      current role identifier
     */
    public RoleClient(OrganizationsClient org, String roleId) {
        this.roleId    = roleId;
        this.orgClient = org;
        Assert.hasLength(roleId, "roleId");
    }
    
    /**
     * Retrieve role information from its id.
     *
     * @return
     *      role informations
     */
    public Optional<Role> find() {
        ApiResponseHttp res = http.GET(getEndpointRole(), orgClient.bearerAuthToken);
        if (HttpURLConnection.HTTP_NOT_FOUND == res.getCode()) {
            return Optional.empty();
        } else {
            return Optional.of(JsonUtils.unmarshallBean(res.getBody(), Role.class));
        }
    }
    
    /**
     * Check if a role is present
     * 
     * @return
     *      if current role with id exist
     */
    public boolean exist() {
        return find().isPresent();
    }
    
    /**
     * Delete a role from its id.
     */
    public void delete() {
        if (!exist()) {
            throw new RuntimeException("Role '"+ roleId + "' has not been found");
        }
        http.DELETE(getEndpointRole(), orgClient.bearerAuthToken);
        
    }
    
    /**
     * Update an existing role.
     * 
     * @param cr
     *      role definition
     */
    public void update(RoleDefinition cr) {
        http.PUT(getEndpointRole(), orgClient.bearerAuthToken, marshall(cr));
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
    public String getEndpointRole() {
        return getEndpointRole(roleId);
    }
    
    /**
     * Endpoint to access dbs (static)
     *
     * @param role
     *      database identifer
     * @return
     *      database endpoint
     */
    public static String getEndpointRole(String role) {
        return OrganizationsClient.getApiDevopsEndpointRoles() + "/" + role;
    }
    
   
}
