package com.datastax.astra.sdk.iam;

import static com.datastax.stargate.sdk.core.ApiSupport.handleError;

import java.net.HttpURLConnection;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.Optional;

import com.datastax.astra.sdk.iam.domain.RoleDefinition;
import com.datastax.astra.sdk.iam.domain.Role;
import com.datastax.astra.sdk.utils.ApiDevopsSupport;
import com.datastax.stargate.sdk.utils.Assert;

/**
 * Working with the Role part of the devop API.
 *
 * @author Cedrick LUNVEN (@clunven)
 */
public class RoleClient extends ApiDevopsSupport {
   
    /** Path related to Roles. */
    public static final String PATH_ROLES = "/roles";
    
    /** */
    public static final String ROLE = "/roles";
    
    
    /** Working role. */
    private final String roleId;
    
    private final String resourceSuffix;
    
    /**
     * Default constructor.
     *
     * @param token
     *      authenticated token
     * @param roleId
     *      current role identifier
     */
    public RoleClient(String token, String roleId) {
        super(token);
        this.roleId = roleId;
        this.resourceSuffix = IamClient.PATH_ORGANIZATIONS + PATH_ROLES + "/" + roleId;
        Assert.hasLength(roleId, "roleId");
    }
    
    /**
     * Retrieve role information from its id.
     *
     * @return
     *      role informations
     */
    public Optional<Role> find() {
         HttpResponse<String> response;
         try {
             response = http().send(
                     req(resourceSuffix).GET().build(), 
                     BodyHandlers.ofString());
         } catch (Exception e) {
             throw new RuntimeException("Cannot invoke API to find document:", e);
         }
         
         if (HttpURLConnection.HTTP_NOT_FOUND == response.statusCode()) {
             return Optional.empty();
         }
         
         handleError(response);
         
         try {
             return Optional.of(om().readValue(response.body(), Role.class));
         } catch (Exception e) {
             throw new RuntimeException("Cannot Marshall output in 'find role()' body=" + response.body(), e);
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
        HttpResponse<String> response;
        try {
            response = http().send(req(resourceSuffix)
                     .DELETE().build(), BodyHandlers.ofString());
            if (HttpURLConnection.HTTP_NO_CONTENT == response.statusCode()) {
                return;
            }
        } catch (Exception e) {
            throw new RuntimeException("Cannot invoke API to delete a document:", e);
        }
        handleError(response);
    }
    
    /**
     * Update an existing role.
     * 
     * @param cr
     *      role definition
     */
    public void update(RoleDefinition cr) {
        Assert.notNull(cr, "CreateRole request");
        HttpResponse<String> response;
        try {
           String reqBody = om().writeValueAsString(cr);
           response = http().send(
                   req(resourceSuffix)
                   .PUT(BodyPublishers.ofString(reqBody)).build(),
                   BodyHandlers.ofString());
        } catch (Exception e) {
            throw new RuntimeException("Cannot update a new role", e);
        }
        handleError(response);
    }
   
}
