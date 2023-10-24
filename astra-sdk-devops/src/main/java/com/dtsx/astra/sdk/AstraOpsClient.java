package com.dtsx.astra.sdk;

import com.dtsx.astra.sdk.db.AstraDBOpsClient;
import com.dtsx.astra.sdk.org.KeysClient;
import com.dtsx.astra.sdk.org.RolesClient;
import com.dtsx.astra.sdk.org.TokensClient;
import com.dtsx.astra.sdk.org.UsersClient;
import com.dtsx.astra.sdk.org.domain.*;
import com.dtsx.astra.sdk.streaming.AstraStreamingClient;
import com.dtsx.astra.sdk.utils.ApiLocator;
import com.dtsx.astra.sdk.utils.ApiResponseHttp;
import com.dtsx.astra.sdk.utils.AstraEnvironment;
import com.dtsx.astra.sdk.utils.JsonUtils;

import java.util.Map;

/**
 * Main Class to interact with Astra Devops API.
 *
 * <p>This class uses </p>
 */
public class AstraOpsClient extends AbstractApiClient {

    /**
     * Initialize the Devops API with a token
     *
     * @param token
     *      bearerAuthToken token
     */
    public AstraOpsClient(String token) {
        this(token, AstraEnvironment.PROD);
    }

    /**
     * Initialize the Devops API with a token
     *
     * @param env
     *     environment Astra
     * @param token
     *      bearerAuthToken token
     */
    public AstraOpsClient(String token, AstraEnvironment env) {
        super(token, env);
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
    public String getOrganizationId() {
        // Invoke endpoint
        ApiResponseHttp res = getHttpClient().GET(ApiLocator.getApiDevopsEndpoint(environment) + "/currentOrg", token);
        // Parse response
        return (String) JsonUtils.unmarshallBean(res.getBody(),  Map.class).get("id");
    }

    /**
     * Retrieve the organization wth current token.
     *
     * @return
     *      current organization
     */
    public Organization getOrganization() {
        // Invoke endpoint
        ApiResponseHttp res = getHttpClient().GET(users().getEndpointUsers(), token);
        // Marshalling the users response to get org infos
        ResponseAllUsers body = JsonUtils.unmarshallBean(res.getBody(), ResponseAllUsers.class);
        // Build a proper result
        return new Organization(body.getOrgId(), body.getOrgName());
    }

    // ------------------------------------------------------
    //                 WORKING WITH ASTRA DB
    // ------------------------------------------------------

    /**
     * Works with db.
     *
     * @return
     *      databases client
     */
    public AstraDBOpsClient db() {
        return new AstraDBOpsClient(token, environment);
    }

    // ------------------------------------------------------
    //                 WORKING WITH ASTRA STREAMING
    // ------------------------------------------------------

    /**
     * Works with Streaming.
     *
     * @return
     *      streaming client
     */
    public AstraStreamingClient streaming() {
        return new AstraStreamingClient(token, environment);
    }


    // ------------------------------------------------------
    //                 WORKING WITH USERS
    // ------------------------------------------------------

    /**
     * List Users.
     *
     * @return
     *      user client
     */
    public UsersClient users() {
        return new UsersClient(token, environment);
    }

    // ------------------------------------------------------
    //                 WORKING WITH ROLES
    // ------------------------------------------------------

    /**
     * List Roles.
     *
     * @return
     *      role client
     */
    public RolesClient roles() {
        return new RolesClient(token, environment);
    }

    // ------------------------------------------------------
    //                 WORKING WITH KEYS
    // ------------------------------------------------------

    /**
     * List keys.
     *
     * @return
     *      keys client
     */
    public KeysClient keys() {
        return new KeysClient(token, environment);
    }

    // ------------------------------------------------------
    //                 WORKING WITH TOKENS
    // ------------------------------------------------------

    /**
     * List tokens.
     *
     * @return
     *      token client
     */
    public TokensClient tokens() {
        return new TokensClient(token, environment);
    }

}
