package com.datastax.astra.sdk.iam;

import static com.datastax.stargate.sdk.core.ApiSupport.handleError;

import java.net.HttpURLConnection;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.datastax.astra.sdk.iam.domain.Role;
import com.datastax.astra.sdk.iam.domain.User;
import com.datastax.astra.sdk.utils.ApiDevopsSupport;
import com.datastax.astra.sdk.utils.IdUtils;
import com.datastax.stargate.sdk.utils.Assert;

/**
 * @author Cedrick LUNVEN (@clunven)
 */
public class UserClient extends ApiDevopsSupport {

    /** Resource suffix. */ 
    public static final String PATH_USERS = "/users";
    
    /** Reference to iamClient. */
    private IamClient iamClient; 
    
    /** Role identifier. */
    private final String userId;
    
    /** Suffix for the resource. */ 
    private final String resourceSuffix;
    
    /**
     * Default constructor.
     *
     * @param iamClient
     *      iamClient
     * @param token
     *      authenticated token
     * @param userId
     *      current user identifier
     */
    public UserClient(IamClient iamClient, String token, String userId) {
        super(token);
        this.userId         = userId;
        this.iamClient      = iamClient;
        this.resourceSuffix = IamClient.PATH_ORGANIZATIONS + PATH_USERS + "/" + userId;
        Assert.hasLength(userId, "userId");
    }
    
    /**
     * Retrieve user information from its id.
     *
     * @return
     *      user informations
     */
    public Optional<User> find() {
         HttpResponse<String> response;
         try {
             response = http().send(req(resourceSuffix).GET().build(), 
                     BodyHandlers.ofString());
         } catch (Exception e) {
             throw new RuntimeException("Cannot invoke API to find document:", e);
         }
         
         if (HttpURLConnection.HTTP_NOT_FOUND == response.statusCode()) {
             return Optional.empty();
         }
         
         handleError(response);
         
         try {
             return Optional.of(om().readValue(response.body(), User.class));
         } catch (Exception e) {
             throw new RuntimeException("Cannot Marshall output in 'find user()' body=" + response.body(), e);
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
        HttpResponse<String> response;
        try {
            response = http().send(req(resourceSuffix)
                     .DELETE().build(), BodyHandlers.ofString());
            if (HttpURLConnection.HTTP_NO_CONTENT == response.statusCode()) {
                return;
            }
        } catch (Exception e) {
            throw new RuntimeException("Cannot invoke API to delete a user:", e);
        }
        handleError(response);
    }
    
    /**
     * Replace roles of users.
     *
     * @param roles
     *      replace existing roles of a userss
     */
    public void updateRoles(String... roles) {
        Assert.notNull(roles, "User roles");
        if (!exist()) {
            throw new RuntimeException("User '"+ userId + "' has not been found");
        }
        if (roles.length == 0) {
            throw new IllegalArgumentException("Roles list cannot be empty");
        }
        
        Map<String, List<String>> mapRoles = new HashMap<>();
        mapRoles.put("roles", new ArrayList<>());
        Arrays.asList(roles).stream().forEach(currentRole -> {
            if (IdUtils.isUUID(currentRole)) {
                mapRoles.get("roles").add(currentRole);
            } else {
                Optional<Role> opt = iamClient.findRoleByName(currentRole);
                if (opt.isPresent()) {
                    mapRoles.get("roles").add(opt.get().getId());
                } else {
                    throw new IllegalArgumentException("Cannot find role with id " + currentRole);
                }
            }
         });
        
        HttpResponse<String> response;
        try {
           response = http().send(req(resourceSuffix + "/roles")
                            .PUT(BodyPublishers.ofString(om().writeValueAsString(mapRoles)))
                            .build(),
                   BodyHandlers.ofString());
        } catch (Exception e) {
            throw new RuntimeException("Cannot create a new role", e);
        }
        handleError(response);
    }
    
    
}
