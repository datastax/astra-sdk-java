package com.dtsx.astra.sdk;

import com.dtsx.astra.sdk.utils.ApiResponseHttp;
import com.dtsx.astra.sdk.utils.Assert;
import com.dtsx.astra.sdk.utils.HttpClientWrapper;

import static java.net.HttpURLConnection.HTTP_ACCEPTED;

/**
 * Super Class for the different Http Clients of the api
 */
public abstract class AbstractApiClient {

    /** hold a reference to the bearer token. */
    protected final String token;

    /**
     * Default constructor.
     *
     * @param token
     *     token value
     */
    public AbstractApiClient(String token) {
        Assert.hasLength(token, "token");
        this.token = token;
    }

    /**
     * Gets token
     *
     * @return value of token
     */
    public String getToken() {
        return token;
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
