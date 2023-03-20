package com.dtsx.astra.sdk.org;

import com.dtsx.astra.sdk.AbstractApiClient;
import com.dtsx.astra.sdk.AstraDevopsApiClient;
import com.dtsx.astra.sdk.utils.HttpClientWrapper;
import com.dtsx.astra.sdk.org.domain.InviteUserRequest;
import com.dtsx.astra.sdk.org.domain.ResponseAllUsers;
import com.dtsx.astra.sdk.org.domain.Role;
import com.dtsx.astra.sdk.org.domain.User;
import com.dtsx.astra.sdk.utils.*;

import java.net.HttpURLConnection;
import java.util.*;
import java.util.stream.Stream;

/**
 * Client to work with Users.
 */
public class UsersClient extends AbstractApiClient {

    /**
     * Constructor.
     *
     * @param token
     *      current token.
     */
    public UsersClient(String token) {
        super(token);
    }

    /**
     * List users in organization.
     *
     * @return
     *      list of roles in target organization.
     */
    public Stream<User> findAll() {
        // Invoke endpoint
        ApiResponseHttp res = GET(getEndpointUsers());
        // Marshall response
        return JsonUtils.unmarshallBean(res.getBody(), ResponseAllUsers.class).getUsers().stream();
    }

    /**
     * Retrieve user information from its id.
     *
     * @param userId
     *      user identifier
     * @return
     *      user information
     */
    public Optional<User> find(String userId) {
        ApiResponseHttp res = GET(getEndpointUser(userId));
        if (HttpURLConnection.HTTP_NOT_FOUND == res.getCode()) {
            return Optional.empty();
        } else {
            return Optional.of(JsonUtils.unmarshallBean(res.getBody(), User.class));
        }
    }

    /**
     * Retrieve a suer from his email.
     *
     * @param email
     *      user email
     * @return
     *      user iif exist
     */
    public Optional<User> findByEmail(String email) {
        Assert.hasLength(email, "User email should not be null nor empty");
        return findAll().filter(u-> u.getEmail().equalsIgnoreCase(email)).findFirst();
    }

    /**
     * Check if a role is present
     *
     * @param userId
     *      user identifier
     * @return
     *      iif the user exists
     */
    public boolean exist(String userId) {
        return find(userId).isPresent();
    }

    /**
     * Delete a user from its email.
     *
     * @param userEmail
     *      user emails
     * @return
     *      if the user exists
     */
    public boolean existByEmail(String userEmail) {
        return findByEmail(userEmail).isPresent();
    }

    /**
     * Delete a role from its id.
     *
     * @param userId
     *      user identifier
     */
    public void delete(String userId) {
        if (!exist(userId)) {
            throw new RuntimeException("User '"+ userId + "' has not been found");
        }
        DELETE(getEndpointUser(userId));
    }

    /**
     * Delete a user from its email.
     *
     * @param userEmail
     *      user emails
     */
    public void deleteByEmail(String userEmail) {
        delete(findByEmail(userEmail).get().getUserId());
    }

    /**
     * Invite a user.
     *
     * @param email
     *      user email
     * @param roles
     *      list of roles to assign
     */
    public void invite(String email, String... roles) {
        // Parameter validation
        Assert.notNull(email, "User email");
        Assert.notNull(roles, "User roles");
        Assert.isTrue(roles.length > 0, "Roles list cannot be empty");

        // Build the invite request with expected roles
        RolesClient rolesClient = new RolesClient(token);
        AstraDevopsApiClient devopsApiClient = new AstraDevopsApiClient(token);
        InviteUserRequest inviteRequest = new InviteUserRequest(devopsApiClient.getOrganizationId(), email);
        Arrays.asList(roles).forEach(currentRole -> {
            if (IdUtils.isUUID(currentRole)) {
                inviteRequest.addRoles(currentRole);
            } else {
                // If role provided is a role name...
                Optional<Role> opt = rolesClient.findByName(currentRole);
                if (opt.isPresent()) {
                    inviteRequest.addRoles(opt.get().getId());
                } else {
                    throw new IllegalArgumentException("Cannot find role with name " + currentRole);
                }
            }
        });

        // Invoke HTTP
        PUT(getEndpointUsers(), JsonUtils.marshall(inviteRequest));
    }

    /**
     * Replace roles of users.
     *
     * @param userId
     *      user identifier
     * @param roles
     *      replace existing roles of a user
     */
    public void updateRoles(String userId, String... roles) {
        // Parameter validation
        Assert.notNull(roles, "User roles");
        Assert.isTrue(roles.length >0 , "Roles list cannot be empty");
        if (!exist(userId)) {
            throw new RuntimeException("User '"+ userId + "' has not been found");
        }
        // Building body
        Map<String, List<String>> mapRoles = new HashMap<>();
        mapRoles.put("roles", new ArrayList<>());

        RolesClient rolesClient = new RolesClient(token);
        Arrays.stream(roles).forEach(currentRole -> {
            if (IdUtils.isUUID(currentRole)) {
                mapRoles.get("roles").add(currentRole);
            } else {
                Optional<Role> opt = rolesClient.findByName(currentRole);
                if (opt.isPresent()) {
                    mapRoles.get("roles").add(opt.get().getId());
                } else {
                    throw new IllegalArgumentException("Cannot find role with id " + currentRole);
                }
            }
        });
        PUT(getEndpointUser(userId) + "/roles", JsonUtils.marshall(mapRoles));
    }

    /**
     * Endpoint to access schema for namespace.
     *
     * @return
     *      endpoint
     */
    public static String getEndpointUsers() {
        return ApiLocator.getApiDevopsEndpoint() + "/organizations/users";
    }

    /**
     * Endpoint to access dbs.
     *
     * @param userId
     *      user identifier
     * @return
     *      database endpoint
     */
    private String getEndpointUser(String userId) {
        return UsersClient.getEndpointUsers() + "/" + userId;
    }

}
