package com.datastax.astra.sdk.organizations;

import static com.datastax.stargate.sdk.utils.JsonUtils.marshall;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.datastax.astra.sdk.organizations.domain.Role;
import com.datastax.astra.sdk.organizations.domain.User;
import com.datastax.astra.sdk.utils.IdUtils;
import com.datastax.stargate.sdk.core.ApiResponseHttp;
import com.datastax.stargate.sdk.utils.Assert;
import com.datastax.stargate.sdk.utils.HttpApisClient;
import com.datastax.stargate.sdk.utils.JsonUtils;

/**
 * Work with the user resource.
 *
 * @author Cedrick LUNVEN (@clunven)
 */
public class UserClient{

    /** Role identifier. */
    private final String userId;
    
    /** Wrapper handling header and error management as a singleton. */
    private final HttpApisClient http = HttpApisClient.getInstance();
    
    /** reference to organization. */
    private final OrganizationsClient orgClient;
    
    /**
     * Default constructor.
     *
     * @param org
     *      organization client
     * @param userId
     *      current user identifier
     */
    public UserClient(OrganizationsClient org, String userId) {
        this.userId    = userId;
        this.orgClient = org;
        Assert.hasLength(userId, "userId");
    }
    
    /**
     * Retrieve user information from its id.
     *
     * @return
     *      user informations
     */
    public Optional<User> find() {
        ApiResponseHttp res = http.GET(getEndpointUser(), orgClient.bearerAuthToken);
        if (HttpURLConnection.HTTP_NOT_FOUND == res.getCode()) {
            return Optional.empty();
        } else {
            return Optional.of(JsonUtils.unmarshallBean(res.getBody(), User.class));
        }
    }
    
    /**
     * Check if a role is present
     * 
     * @return
     *      iif the user exists
     */
    public boolean exist() {
        return find().isPresent();
    }
    
    /**
     * Delete a role from its id.
     */
    public void delete() {
        if (!exist()) {
            throw new RuntimeException("User '"+ userId + "' has not been found");
        }
        http.DELETE(getEndpointUser(), orgClient.bearerAuthToken);
    }
    
    /**
     * Replace roles of users.
     *
     * @param roles
     *      replace existing roles of a userss
     */
    public void updateRoles(String... roles) {
        // Parameter valication
        Assert.notNull(roles, "User roles");
        Assert.isTrue(roles.length >0 , "Roles list cannot be empty");
        if (!exist()) {
            throw new RuntimeException("User '"+ userId + "' has not been found");
        }
        
        // Building body
        Map<String, List<String>> mapRoles = new HashMap<>();
        mapRoles.put("roles", new ArrayList<>());
        Arrays.asList(roles).stream().forEach(currentRole -> {
            if (IdUtils.isUUID(currentRole)) {
                mapRoles.get("roles").add(currentRole);
            } else {
                Optional<Role> opt = orgClient.findRoleByName(currentRole);
                if (opt.isPresent()) {
                    mapRoles.get("roles").add(opt.get().getId());
                } else {
                    throw new IllegalArgumentException("Cannot find role with id " + currentRole);
                }
            }
         });
        
        http.PUT(getEndpointUser() + "/roles", orgClient.bearerAuthToken, marshall(mapRoles));
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
    public String getEndpointUser() {
        return getEndpointUser(userId);
    }
    
    /**
     * Endpoint to access dbs (static)
     *
     * @param user
     *      user identifer
     * @return
     *      user endpoint
     */
    public static String getEndpointUser(String user) {
        return OrganizationsClient.getApiDevopsEndpointUsers() + "/" + user;
    }
    
}
