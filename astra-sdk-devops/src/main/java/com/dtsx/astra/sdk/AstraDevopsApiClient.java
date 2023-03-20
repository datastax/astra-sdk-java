package com.dtsx.astra.sdk;

import com.dtsx.astra.sdk.db.AstraDbClient;
import com.dtsx.astra.sdk.org.KeysClient;
import com.dtsx.astra.sdk.org.RolesClient;
import com.dtsx.astra.sdk.org.TokensClient;
import com.dtsx.astra.sdk.org.UsersClient;
import com.dtsx.astra.sdk.org.domain.*;
import com.dtsx.astra.sdk.streaming.AstraStreamingClient;
import com.dtsx.astra.sdk.utils.ApiLocator;
import com.dtsx.astra.sdk.utils.ApiResponseHttp;
import com.dtsx.astra.sdk.utils.JsonUtils;

import java.util.Map;

/**
 * Main Class to interact with Astra Devops API.
 *
 * <p>This class uses </p>
 */
public class AstraDevopsApiClient extends AbstractApiClient {

    /**
     * Initialize the Devops API with a token
     *
     * @param token
     *      bearerAuthToken token
     */
    public AstraDevopsApiClient(String token) {
        super(token);
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
        ApiResponseHttp res = getHttpClient().GET(ApiLocator.getApiDevopsEndpoint() + "/currentOrg", token);
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
        ApiResponseHttp res = getHttpClient().GET(UsersClient.getEndpointUsers(), token);
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
    public AstraDbClient db() {
        return new AstraDbClient(token);
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
        return new AstraStreamingClient(token);
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
        return new UsersClient(token);
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
        return new RolesClient(token);
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
        return new KeysClient(token);
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
        return new TokensClient(token);
    }



}
