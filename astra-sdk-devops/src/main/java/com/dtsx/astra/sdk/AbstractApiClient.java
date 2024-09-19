package com.dtsx.astra.sdk;

import com.dtsx.astra.sdk.utils.ApiResponseHttp;
import com.dtsx.astra.sdk.utils.Assert;
import com.dtsx.astra.sdk.utils.AstraEnvironment;
import com.dtsx.astra.sdk.utils.HttpClientWrapper;
import com.dtsx.astra.sdk.utils.observability.ApiRequestObserver;
import lombok.Getter;

import java.util.LinkedHashMap;
import java.util.Map;

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
     * Observers to notify.
     */
    protected final Map<String, ApiRequestObserver> observers = new LinkedHashMap<>();

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
     * Default constructor.
     *
     * @param env
     *      astra environment
     * @param token
     *     token value
     * @param observers
     *      list of observers
     */
    public AbstractApiClient(String token, AstraEnvironment env, Map<String, ApiRequestObserver> observers) {
        Assert.hasLength(token, "token");
        this.token = token;
        this.environment = env;
        this.observers.putAll(observers);
    }

    /**
     * Access Http Client.
     *
     * @param operation
     *      operation name (tracking)
     * @return
     *      Http client
     */
    public HttpClientWrapper getHttpClient(String operation) {
        return HttpClientWrapper.getInstance(operation);
    }

    /**
     * Provide a service Name.
     *
     * @return
     *      service name
     */
    public abstract String getServiceName();

    /**
     * Get the full name for the operation.
     *
     * @param operation
     *      operation name
     * @return
     *      full operation name
     */
    protected String getOperationName(String operation) {
        return getServiceName() + "." + operation;
    }

    /**
     * Syntax sugar http requests.
     *
     * @param url
     *      url
     * @param operation
     *      operation name (tracking)
     * @return
     *      response
     */
    public ApiResponseHttp GET(String url, String operation) {
        return getHttpClient(operation).GET(url, getToken());
    }

    /**
     * Syntax sugar http requests.
     *
     * @param url
     *      url
     * @param operation
     *      operation name (tracking)
     * @return
     *      response
     */
    public ApiResponseHttp HEAD(String url, String operation) {
        return getHttpClient(operation).HEAD(url, getToken());
    }

    /**
     * Syntax sugar http requests.
     *
     * @param url
     *      url
     * @param operation
     *      operation name (tracking)
     * @return
     *      response
     */
    public ApiResponseHttp POST(String url, String operation) {
        return getHttpClient(operation).POST(url, getToken());
    }

    /**
     * Syntax sugar http requests.
     *
     * @param body
     *      body
     * @param url
     *      url
     * @param operation
     *      operation name (tracking)
     * @return
     *      response
     */
    public ApiResponseHttp POST(String url, String body, String operation) {
        return getHttpClient(operation).POST(url, getToken(), body);
    }

    /**
     * Syntax sugar http requests.
     *
     * @param url
     *      url
     * @param body
     *      body
     * @param operation
     *      operation name (tracking)
     */
    public void PUT(String url, String body, String operation) {
        getHttpClient(operation).PUT(url, getToken(), body);
    }

    /**
     * Syntax sugar http requests.
     *
     * @param url
     *      url
     * @param body
     *      body
     * @param operation
     *      operation name (tracking)
     */
    public void PATCH(String url, String body, String operation) {
        getHttpClient(operation).PATCH(url, getToken(), body);
    }

    /**
     * Syntax sugar http requests.
     *
     * @param url
     *      url
     * @param operation
     *      operation name (tracking)
     */
    public void DELETE(String url, String operation) {
        getHttpClient(operation).DELETE(url, getToken());
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
