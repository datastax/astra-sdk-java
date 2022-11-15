package com.dtsx.astra.sdk.org.iam;

import com.dtsx.astra.sdk.HttpClientWrapper;
import com.dtsx.astra.sdk.org.OrganizationsClient;
import com.dtsx.astra.sdk.utils.ApiResponseHttp;
import com.dtsx.astra.sdk.utils.Assert;
import com.dtsx.astra.sdk.utils.JsonUtils;
import com.dtsx.astra.sdk.org.domain.Role;
import com.dtsx.astra.sdk.org.domain.RoleDefinition;

import java.net.HttpURLConnection;
import java.util.Optional;


/**
 * Working with the Role part of the devops API.
 *
 * @author Cedrick LUNVEN (@clunven)
 */
public class RoleClient {
   
    /** Working role. */
    private final String roleId;

    /** Wrapper handling header and error management as a singleton. */
    private final HttpClientWrapper http = HttpClientWrapper.getInstance();

    /** reference to organization. */
    private final OrganizationsClient orgClient;
    
    /**
     * Default constructor.
     *
     * @param org
     *      organization client 
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
     *      role information
     */
    public Optional<Role> find() {
        ApiResponseHttp res = http.GET(getEndpointRole(), orgClient.getToken());
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
        http.DELETE(getEndpointRole(), orgClient.getToken());
        
    }
    
    /**
     * Update an existing role.
     * 
     * @param cr
     *      role definition
     */
    public void update(RoleDefinition cr) {
        http.PUT(getEndpointRole(), orgClient.getToken(), JsonUtils.marshall(cr));
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
     *      database identifier
     * @return
     *      database endpoint
     */
    public static String getEndpointRole(String role) {
        return OrganizationsClient.getApiDevopsEndpointRoles() + "/" + role;
    }
    
   
}
