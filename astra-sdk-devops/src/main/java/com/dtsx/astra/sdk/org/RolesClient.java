package com.dtsx.astra.sdk.org;

import com.dtsx.astra.sdk.AbstractApiClient;
import com.dtsx.astra.sdk.org.domain.CreateRoleResponse;
import com.dtsx.astra.sdk.org.domain.DefaultRoles;
import com.dtsx.astra.sdk.org.domain.Role;
import com.dtsx.astra.sdk.org.domain.RoleDefinition;
import com.dtsx.astra.sdk.org.exception.RoleNotFoundException;
import com.dtsx.astra.sdk.utils.ApiLocator;
import com.dtsx.astra.sdk.utils.ApiResponseHttp;
import com.dtsx.astra.sdk.utils.Assert;
import com.dtsx.astra.sdk.utils.AstraEnvironment;
import com.dtsx.astra.sdk.utils.JsonUtils;
import com.fasterxml.jackson.core.type.TypeReference;

import java.net.HttpURLConnection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Group roles operations.
 */
public class RolesClient extends AbstractApiClient {

    /** Constants. */
    public static final String PATH_ORGANIZATIONS = "/organizations";

    /** Path related to Roles. */
    public static final String PATH_ROLES = "/roles";

    /** List of Roles. */
    public static final TypeReference<List<Role>> TYPE_LIST_ROLES =
            new TypeReference<List<Role>>(){};

    /**
     * As immutable object use builder to initiate the object.
     *
     * @param token
     *      authenticated token
     */
    public RolesClient(String token) {
        this(token, AstraEnvironment.PROD);
    }

    /**
     * As immutable object use builder to initiate the object.
     *
     * @param env
     *      define target environment to be used
     * @param token
     *      authenticated token
     */
    public RolesClient(String token, AstraEnvironment env) {
        super(token, env);
    }

    /**
     * List roles in a Organizations.
     *
     * @return
     *      list of roles in target organization.
     */
    public Stream<Role> findAll() {
        // Invoke endpoint
        ApiResponseHttp res = GET(getApiEndpointRoles());
        // Mapping
        return JsonUtils.unmarshallType(res.getBody(), TYPE_LIST_ROLES).stream();
    }

    /**
     * Retrieve role information from its id.
     *
     * @param roleId
     *      role identifier
     * @return
     *      role information
     */
    public Optional<Role> find(String roleId) {
        ApiResponseHttp res = GET(getEndpointRole(roleId));
        if (HttpURLConnection.HTTP_NOT_FOUND == res.getCode()) {
            return Optional.empty();
        } else {
            return Optional.of(JsonUtils.unmarshallBean(res.getBody(), Role.class));
        }
    }

    /**
     * Access the role if exist or exception.
     *
     * @param roleId
     *      role identifier
     * @return
     *      role
     */
    public Role get(String roleId) {
        return find(roleId).orElseThrow(() -> new RoleNotFoundException(roleId));
    }

    /**
     * Retrieve a suer from his email.
     *
     * @param role
     *      role name
     * @return
     *      user iif exist
     */
    public Optional<Role> find(DefaultRoles role) {
        return findByName(role.getName());
    }

    /**
     * Access the role if exist or exception.
     *
     * @param role
     *      current role
     * @return
     *      role
     */
    public Role get(DefaultRoles role) {
        return find(role).orElseThrow(() -> new RoleNotFoundException(role.getName()));
    }

    /**
     * Retrieve a suer from his email.
     *
     * @param roleName
     *      role name
     * @return
     *      user iif exist
     */
    public Optional<Role> findByName(String roleName) {
        Assert.hasLength(roleName, "User email should not be null nor empty");
        return findAll().filter(r-> r.getName().equalsIgnoreCase(roleName)).findFirst();
    }

    /**
     * Access the role if exist or exception.
     *
     * @param roleName
     *      role name
     * @return
     *      role
     */
    public Role getByName(String roleName) {
        return findByName(roleName).orElseThrow(() -> new RoleNotFoundException(roleName));
    }

    /**
     * Create a new role.
     *
     * @param cr
     *      new role request
     * @return
     *      new role created
     */
    public CreateRoleResponse create(RoleDefinition cr) {
        Assert.notNull(cr, "CreateRole request");
        ApiResponseHttp res = POST(getApiEndpointRoles(), JsonUtils.marshall(cr));
        return JsonUtils.unmarshallBean(res.getBody(), CreateRoleResponse.class);
    }

    /**
     * Check if a role is present
     *
     * @param roleId
     *      role identifier
     * @return
     *      if current role with id exist
     */
    public boolean exist(String roleId) {
        return find(roleId).isPresent();
    }

    /**
     * Delete a role from its id.
     *
     * @param roleId
     *      role identifier
     */
    public void delete(String roleId) {
        // Ensure role exist
        get(roleId);
        // Http Request
        DELETE(getEndpointRole(roleId));
    }

    /**
     * Update an existing role.
     *
     * @param roleId
     *      role identifier
     * @param cr
     *      role definition
     */
    public void update(String roleId, RoleDefinition cr) {
        PUT(getEndpointRole(roleId), JsonUtils.marshall(cr));
    }

    /**
     * Endpoint to access schema for namespace.
     *
     * @return
     *      endpoint
     */
    private String getApiEndpointRoles() {
        return ApiLocator.getApiDevopsEndpoint(environment) + PATH_ORGANIZATIONS + PATH_ROLES;
    }

    /**
     * Endpoint to access dbs (static)
     *
     * @param role
     *      database identifier
     * @return
     *      database endpoint
     */
    private String getEndpointRole(String role) {
        return getApiEndpointRoles() + "/" + role;
    }

}
