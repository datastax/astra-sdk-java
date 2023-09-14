package com.dtsx.astra.sdk;

import com.dtsx.astra.sdk.utils.ApiResponseHttp;
import com.dtsx.astra.sdk.utils.Assert;
import com.dtsx.astra.sdk.utils.AstraEnvironment;
import com.dtsx.astra.sdk.utils.HttpClientWrapper;
import lombok.Getter;

import static java.net.HttpURLConnection.HTTP_ACCEPTED;

/**
 * Super Class for the different Http Clients of the api
 */
@Getter
public abstract class AbstractApiClient {

    /**
     * Token Value
     */
    protected final String token;

    /**
     * Hold a reference to target Astra Environment.
     */
    protected final AstraEnvironment environment;

    /**
     * Default constructor.
     *
     * @param env
     *      astra environment
     * @param token
     *     token value
     */
    public AbstractApiClient(String token, AstraEnvironment env) {
        Assert.hasLength(token, "token");
        this.token = token;
        this.environment = env;
    }

    /**
     * Access Http Client.
     *
     * @return
     *      Http client
     */
    public HttpClientWrapper getHttpClient() {
        return HttpClientWrapper.getInstance();
    }

    /**
     * Syntax sugar http requests.
     *
     * @param url
     *      url
     * @return
     *      response
     */
    public ApiResponseHttp GET(String url) {
        return getHttpClient().GET(url, getToken());
    }

    /**
     * Syntax sugar http requests.
     *
     * @param url
     *      url
     * @return
     *      response
     */
    public ApiResponseHttp HEAD(String url) {
        System.out.println(url);
        return getHttpClient().HEAD(url, getToken());
    }

    /**
     * Syntax sugar http requests.
     *
     * @param url
     *      url
     * @return
     *      response
     */
    public ApiResponseHttp POST(String url) {
        return getHttpClient().POST(url, getToken());
    }

    /**
     * Syntax sugar http requests.
     *
     * @param body
     *      body
     * @param url
     *      url
     * @return
     *      response
     */
    public ApiResponseHttp POST(String url, String body) {
        return getHttpClient().POST(url, getToken(), body);
    }

    /**
     * Syntax sugar http requests.
     *
     * @param url
     *      url
     * @param body
     *      body
     */
    public void PUT(String url, String body) {
        getHttpClient().PUT(url, getToken(), body);
    }

    /**
     * Syntax sugar http requests.
     *
     * @param url
     *      url
     * @param body
     *      body
     */
    public void PATCH(String url, String body) {
        getHttpClient().PATCH(url, getToken(), body);
    }

    /**
     * Syntax sugar http requests.
     *
     * @param url
     *      url
     */
    public void DELETE(String url) {
        getHttpClient().DELETE(url, getToken());
    }

    /**
     * Response validation
     *
     * @param res
     *         current response
     * @param action
     *         action taken
     * @param entityId
     *         entity id
     */
    public void assertHttpCodeAccepted(ApiResponseHttp res, String action, String entityId) {
        String errorMsg = " Cannot " + action + " id=" + entityId + " code=" + res.getCode() + " msg=" + res.getBody();
        Assert.isTrue(HTTP_ACCEPTED == res.getCode(), errorMsg);
    }


}
