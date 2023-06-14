package com.dtsx.astra.sdk.org;

import com.dtsx.astra.sdk.AbstractApiClient;
import com.dtsx.astra.sdk.org.domain.*;
import com.dtsx.astra.sdk.utils.ApiLocator;
import com.dtsx.astra.sdk.utils.ApiResponseHttp;
import com.dtsx.astra.sdk.utils.Assert;
import com.dtsx.astra.sdk.utils.JsonUtils;

import java.util.Optional;
import java.util.stream.Stream;

/**
 * Group token operations.
 */
public class TokensClient extends AbstractApiClient {

    /** useful with tokens interactions. */
    private final RolesClient rolesClient;

    /**
     * As immutable object use builder to initiate the object.
     *
     * @param token
     *      authenticated token
     */
    public TokensClient(String token) {
        this(token, ApiLocator.AstraEnvironment.PROD);
    }

    /**
     * As immutable object use builder to initiate the object.
     *
     * @param env
     *      define target environment to be used
     * @param token
     *      authenticated token
     */
    public TokensClient(String token, ApiLocator.AstraEnvironment env) {
        super(token, env);
        rolesClient = new RolesClient(token, env);
    }

    /**
     * List tokens
     *
     * @return
     *      list of tokens for this organization
     */
    public Stream<IamToken> findAll() {
        // Invoke endpoint
        ApiResponseHttp res = GET(getEndpointTokens());
        // Marshall
        return JsonUtils.unmarshallBean(res.getBody(), ResponseAllIamTokens.class).getClients().stream();
    }

    /**
     * Retrieve role information from its id.
     *
     * @param tokenId
     *      token identifier
     * @return
     *      role information
     */
    public Optional<IamToken> findById(String tokenId) {
        return findAll()
                .filter(t -> t.getClientId().equalsIgnoreCase(tokenId))
                .findFirst();
    }

    /**
     * Check in existence of a token.
     *
     * @param tokenId
     *      token identifier
     * @return
     *      if the provided token exist
     */
    public boolean exist(String tokenId) {
        return findById(tokenId).isPresent();
    }

    /**
     * Revoke a token.
     *
     * @param tokenId
     *      token identifier
     */
    public void delete(String tokenId) {
        if (!exist(tokenId)) {
            throw new RuntimeException("Token '"+ tokenId + "' has not been found");
        }
        DELETE(getEndpointToken(tokenId));
    }

    /**
     * Create token
     *
     * @param role
     *      create a token with dedicated role
     * @return
     *      created token
     */
    public CreateTokenResponse create(String role) {
        Assert.hasLength(role, "role");
        // Role should exist
        Optional<Role> optRole = rolesClient.findByName(role);
        String roleId;
        if (optRole.isPresent()) {
            roleId = optRole.get().getId();
        } else {
            roleId = rolesClient.get(role).getId();
        }
        // Building request
        String body = "{ \"roles\": [ \"" + JsonUtils.escapeJson(roleId) + "\"]}";
        // Invoke endpoint
        ApiResponseHttp res = POST(getEndpointTokens(), body);
        // Marshall response
        return JsonUtils.unmarshallBean(res.getBody(), CreateTokenResponse.class);
    }

    /**
     * Create token
     *
     * @param role
     *      create a token with dedicated role
     * @return
     *      created token
     */
    public CreateTokenResponse create(DefaultRoles role) {
        return create(role.getName());
    }

    /**
     * Endpoint to access schema for namespace.
     *
     * @return
     *      endpoint
     */
    public String getEndpointTokens() {
        return ApiLocator.getApiDevopsEndpoint(environment) + "/clientIdSecrets";
    }

    /**
     * Endpoint to access dbs (static)
     *
     * @param tokenId
     *      token identifier
     * @return
     *      token endpoint
     */
    public String getEndpointToken(String tokenId) {
        return getEndpointTokens() + "/" + tokenId;
    }
}
