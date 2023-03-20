package com.dtsx.astra.sdk.utils;

import com.dtsx.astra.sdk.exception.AuthenticationException;
import org.apache.hc.client5.http.auth.StandardAuthScheme;
import org.apache.hc.client5.http.classic.methods.*;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.cookie.StandardCookieSpec;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.Method;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.util.TimeValue;
import org.apache.hc.core5.util.Timeout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.HttpURLConnection;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Helper to forge Http Requests to interact with Devops API.
 */
public class HttpClientWrapper {
    
    /** Logger for our Client. */
    private static final Logger LOGGER = LoggerFactory.getLogger(HttpClientWrapper.class);

    /** Value for the requested with. */
    private static final String REQUEST_WITH = "AstraJavaSDK " + HttpClientWrapper.class.getPackage().getImplementationVersion();

    /** Default settings in Request and Retry */
    private static final int DEFAULT_TIMEOUT_REQUEST   = 20;
    
    /** Default settings in Request and Retry */
    private static final int DEFAULT_TIMEOUT_CONNECT   = 20;

    /** Headers, Api is using JSON */
    private static final String CONTENT_TYPE_JSON        = "application/json";

    /** Header param. */
    private static final String HEADER_ACCEPT            = "Accept";

    /** Headers param to insert the conte type. */
    private static final String HEADER_CONTENT_TYPE      = "Content-Type";

    /** Headers param to insert the token for devops API. */
    private static final String HEADER_AUTHORIZATION     = "Authorization";

    /** Headers name to insert the user agent identifying the client. */
    private static final String HEADER_USER_AGENT        = "User-Agent";

    /** Headers param to insert the user agent identifying the client. */
    private static final String HEADER_REQUESTED_WITH    = "X-Requested-With";

    /** Current organization identifier. */
    private static final String HEADER_CURRENT_ORG = "X-DataStax-Current-Org";

    /** Current pulsar cluster. */
    private static final String HEADER_CURRENT_PULSAR_CLUSTER = "X-DataStax-Pulsar-Cluster";

    /** Singleton pattern. */
    private static HttpClientWrapper _instance = null;
    
    /** HttpComponent5. */
    protected CloseableHttpClient httpClient = null;

    /** Default request configuration. */
    protected static RequestConfig requestConfig = RequestConfig.custom()
            .setCookieSpec(StandardCookieSpec.STRICT)
            .setExpectContinueEnabled(true)
            .setConnectionRequestTimeout(Timeout.ofSeconds(DEFAULT_TIMEOUT_REQUEST))
            .setConnectTimeout(Timeout.ofSeconds(DEFAULT_TIMEOUT_CONNECT))
            .setTargetPreferredAuthSchemes(Arrays.asList(StandardAuthScheme.NTLM, StandardAuthScheme.DIGEST))
            .build();

    // -------------------------------------------
    // ----------------- Singleton ---------------
    // -------------------------------------------
    
    /**
     * Hide default constructor
     */
    private HttpClientWrapper() {}
    
    /**
     * Singleton Pattern.
     * 
     * @return
     *      singleton for the class
     */
    public static synchronized HttpClientWrapper getInstance() {
        if (_instance == null) {
            _instance = new HttpClientWrapper();
            final PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager();
            connManager.setValidateAfterInactivity(TimeValue.ofSeconds(10));
            connManager.setMaxTotal(100);
            connManager.setDefaultMaxPerRoute(10);
            _instance.httpClient = HttpClients.custom().setConnectionManager(connManager).build();
        }
        return _instance;
    }
    
    // -------------------------------------------
    // ---------- Working with HTTP --------------
    // -------------------------------------------
    
    /**
     * Helper to build the HTTP request.
     * 
     * @param url
     *      target url
     * @param token
     *      authentication token
     * @return
     *      http request
     */
    public ApiResponseHttp GET(String url, String token) {
        return executeHttp(Method.GET, url, token, null, CONTENT_TYPE_JSON, false);
    }

    /**
     * Helper to build the HTTP request.
     *
     * @param url
     *      target url
     * @param token
     *      authentication token
     * @param pulsarCluster
     *      pulsar cluster
     * @param organizationId
     *      organization identifier
     * @return
     *      http request
     */
    public ApiResponseHttp GET_PULSAR(String url, String token, String pulsarCluster, String organizationId) {
        HttpUriRequestBase request = buildRequest(Method.GET, url, token, null, CONTENT_TYPE_JSON);
        updatePulsarHttpRequest(request, token, pulsarCluster, organizationId);
        return executeHttp(request, false);
    }

    /**
     * Helper to build the HTTP request.
     *
     * @param url
     *      target url
     * @param token
     *      authentication token
     * @param body
     *      request body
     * @param pulsarCluster
     *      pulsar cluster
     * @param organizationId
     *      organization identifier
     * @return
     *      http request
     */
    public ApiResponseHttp POST_PULSAR(String url, String token, String body, String pulsarCluster, String organizationId) {
        HttpUriRequestBase request = buildRequest(Method.POST, url, token, body, CONTENT_TYPE_JSON);
        updatePulsarHttpRequest(request, token, pulsarCluster, organizationId);
        return executeHttp(request, false);
    }

    /**
     * Helper to build the HTTP request.
     *
     * @param url
     *      target url
     * @param token
     *      authentication token
     * @param body
     *      request body
     * @param pulsarCluster
     *      pulsar cluster
     * @param organizationId
     *      organization identifier
     * @return
     *      http request
     */
    public ApiResponseHttp DELETE_PULSAR(String url, String token, String body, String pulsarCluster, String organizationId) {
        HttpUriRequestBase request = buildRequest(Method.DELETE, url, token, body, CONTENT_TYPE_JSON);
        updatePulsarHttpRequest(request, token, pulsarCluster, organizationId);
        return executeHttp(request, false);
    }

    /**
     * Add item for a pulsar request.
     *
     * @param request
     *      current request
     * @param pulsarToken
     *      pulsar token
     * @param pulsarCluster
     *      pulsar cluster
     * @param organizationId
     *      organization
     */
    private void updatePulsarHttpRequest(HttpUriRequestBase request, String pulsarToken, String pulsarCluster, String organizationId) {
        request.addHeader(HEADER_AUTHORIZATION, pulsarToken);
        request.addHeader(HEADER_CURRENT_ORG, organizationId);
        request.addHeader(HEADER_CURRENT_PULSAR_CLUSTER, pulsarCluster);
    }

    /**
     * Helper to build the HTTP request.
     * 
     * @param url
     *      target url
     * @param token
     *      authentication token
     * @return
     *      http request
     */
    public ApiResponseHttp HEAD(String url, String token) {
        return executeHttp(Method.HEAD, url, token, null, CONTENT_TYPE_JSON, false);
    }
    
    /**
     * Helper to build the HTTP request.
     * 
     * @param url
     *      target url
     * @param token
     *      authentication token
     * @return
     *      http request
     */
    public ApiResponseHttp POST(String url, String token) {
        return executeHttp(Method.POST, url, token, null, CONTENT_TYPE_JSON, true);
    }
    
    /**
     * Helper to build the HTTP request.
     * 
     * @param url
     *      target url
     * @param token
     *      authentication token
     * @param body
     *      request body     
     * @return
     *      http request
     */
    public ApiResponseHttp POST(String url, String token, String body) {
        return executeHttp(Method.POST, url, token, body, CONTENT_TYPE_JSON, true);
    }
    
    /**
     * Helper to build the HTTP request.
     * 
     * @param url
     *      target url
     * @param token
     *      authentication token
     */
    public void DELETE(String url, String token) {
        executeHttp(Method.DELETE, url, token, null, CONTENT_TYPE_JSON, true);
    }
    
    /**
     * Helper to build the HTTP request.
     * 
     * @param url
     *      target url
     * @param token
     *      authentication token
     * @param body
     *      request body
     */
    public void PUT(String url, String token, String body) {
        executeHttp(Method.PUT, url, token, body, CONTENT_TYPE_JSON, false);
    }

    /**
     * Main Method executing HTTP Request.
     * 
     * @param method
     *      http method
     * @param url
     *      url
     * @param token
     *      authentication token
     * @param contentType
     *      request content type
     * @param reqBody
     *      request body
     * @param mandatory
     *      allow 404 errors
     * @return
     *      basic request
     */
    public ApiResponseHttp executeHttp(final Method method, final String url, final String token, String reqBody, String contentType, boolean mandatory) {
        return executeHttp(buildRequest(method, url, token, reqBody, contentType), mandatory);
    }
    
    /**
     * Execute a request coming from elsewhere.
     * 
     * @param req
     *      current request
     * @param mandatory
     *      mandatory
     * @return
     *      api response
     */
    public ApiResponseHttp executeHttp(HttpUriRequestBase req, boolean mandatory) {
        try(CloseableHttpResponse response = httpClient.execute(req)) {
            ApiResponseHttp res;
            if (response == null) {
                res = new ApiResponseHttp("Response is empty, please check url",
                        HttpURLConnection.HTTP_UNAVAILABLE, null);
            } else {
                // Mapping response
                String body = null;
                if (null != response.getEntity()) {
                    body = EntityUtils.toString(response.getEntity());
                    EntityUtils.consume(response.getEntity());
                }
                Map<String, String > headers = new HashMap<>();
                Arrays.stream(response.getHeaders()).forEach(h -> headers.put(h.getName(), h.getValue()));
                res = new ApiResponseHttp(body, response.getCode(), headers);
            }

            // Error management
            if (HttpURLConnection.HTTP_NOT_FOUND == res.getCode() && !mandatory) {
                return res;
            }
            if (res.getCode() >= 300) {
              LOGGER.error("Error for request, url={}, method={}, code={}, body={}",
                      req.getUri().toString(), req.getMethod(),
                      res.getCode(), res.getBody());
              processErrors(res, mandatory);
              LOGGER.error("An HTTP Error occurred. The HTTP CODE Return is {}", res.getCode());
            }
            return res;
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Error in HTTP Request: " + e.getMessage(), e);
        }
    }

    /**
     * Initialize an HTTP request against Stargate.
     * 
     * @param method
     *      http Method
     * @param url
     *      target URL
     * @param token
     *      current token
     * @return
     *      default http with header
     */
    private HttpUriRequestBase buildRequest(final Method method, final String url, final String token, String body, String contentType) {
        HttpUriRequestBase req;
        switch(method) {
            case GET:    req = new HttpGet(url);    break;
            case POST:   req = new HttpPost(url);   break;
            case PUT:    req = new HttpPut(url);    break;
            case DELETE: req = new HttpDelete(url); break;
            case PATCH:  req = new HttpPatch(url);  break;
            case HEAD:   req = new HttpHead(url);   break;
            case TRACE:  req = new HttpTrace(url);  break;
            case OPTIONS:
            case CONNECT:
            default:throw new IllegalArgumentException("Invalid HTTP Method");
        }
        req.addHeader(HEADER_CONTENT_TYPE, contentType);
        req.addHeader(HEADER_ACCEPT, CONTENT_TYPE_JSON);
        req.addHeader(HEADER_USER_AGENT, REQUEST_WITH);
        req.addHeader(HEADER_REQUESTED_WITH, REQUEST_WITH);
        req.addHeader(HEADER_AUTHORIZATION, "Bearer " + token);
        req.setConfig(requestConfig);
        if (null != body) {
            req.setEntity(new StringEntity(body, ContentType.TEXT_PLAIN));
        }
        return req;
    }

    /**
     * Process ERRORS.Anything above code 300 can be marked as an error Still something
     * 404 is expected and should not result in throwing exception (=not find)
     * @param res HttpResponse
     */
    private void processErrors(ApiResponseHttp res, boolean mandatory) {
        switch(res.getCode()) {
                // 400
                case HttpURLConnection.HTTP_BAD_REQUEST:
                    throw new IllegalArgumentException("Error Code=" + res.getCode() + 
                            " (HTTP_BAD_REQUEST) Invalid Parameters: " 
                            + res.getBody());
                // 401
                case HttpURLConnection.HTTP_UNAUTHORIZED:
                    throw new AuthenticationException("Error Code=" + res.getCode() +
                            ", (HTTP_UNAUTHORIZED) Invalid Credentials Check your token: " + 
                            res.getBody());
                // 403
                case HttpURLConnection.HTTP_FORBIDDEN:
                    throw new AuthenticationException("Error Code=" + res.getCode() + 
                            ", (HTTP_FORBIDDEN) Invalid permissions, check your token: " + 
                            res.getBody());
                // 404    
                case HttpURLConnection.HTTP_NOT_FOUND:
                    if (mandatory) {
                        throw new IllegalArgumentException("Error Code=" + res.getCode() + 
                                "(HTTP_NOT_FOUND) Object not found:  " 
                                + res.getBody());
                    }
                break;
                // 409
                case HttpURLConnection.HTTP_CONFLICT:
                    throw new AuthenticationException("Error Code=" + res.getCode() +
                            ", (HTTP_CONFLICT) Object may already exist with same identifiers: " +
                            res.getBody());                
                case 422:
                    throw new IllegalArgumentException("Error Code=" + res.getCode() + 
                            "(422) Invalid information provided to create DB: " 
                            + res.getBody());
                default:
                    if (res.getCode() == HttpURLConnection.HTTP_UNAVAILABLE) {
                        throw new IllegalStateException(res.getBody() + " (http:" + res.getCode() + ")");
                    }
                    throw new RuntimeException(res.getBody() + " (http:" + res.getCode() + ")");
            }
    }

}
