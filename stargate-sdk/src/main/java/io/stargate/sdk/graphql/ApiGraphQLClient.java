package io.stargate.sdk.graphql;

import static io.stargate.sdk.utils.Assert.hasLength;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.stargate.sdk.utils.ApiSupport;

/**
 * Superclass to work with graphQL.
 *
 * @author Cedrick LUNVEN (@clunven)
 */
public class ApiGraphQLClient extends ApiSupport {

    /** Logger for our Client. */
    private static final Logger LOGGER = LoggerFactory.getLogger(ApiGraphQLClient.class);
    
    /** This the endPoint to invoke to work with different API(s). */
    protected final String endPointApiGraphQL;
    
    /**
     * Constructor for ASTRA.
     */
    public ApiGraphQLClient(String username, String password, String endPointAuthentication,  String appToken, String endPointApiGraphQL) {
        hasLength(endPointApiGraphQL, "endPointApiRest");
        hasLength(username, "username");
        hasLength(password, "password");
        this.username               = username;
        this.password               = password;
        this.endPointAuthentication = endPointAuthentication;
        this.endPointApiGraphQL     = endPointApiGraphQL;
        this.appToken               = appToken;
        LOGGER.info("+ Rest API: {}, ", endPointApiGraphQL);
    }
    

}
