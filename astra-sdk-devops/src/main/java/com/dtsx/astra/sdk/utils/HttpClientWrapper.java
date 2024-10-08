package com.dtsx.astra.sdk.utils;

import com.dtsx.astra.sdk.exception.AuthenticationException;
import com.dtsx.astra.sdk.utils.observability.ApiExecutionInfos;
import com.dtsx.astra.sdk.utils.observability.ApiRequestObserver;
import com.dtsx.astra.sdk.utils.observability.CompletableFutures;
import org.apache.hc.client5.http.auth.StandardAuthScheme;
import org.apache.hc.client5.http.classic.methods.HttpDelete;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpHead;
import org.apache.hc.client5.http.classic.methods.HttpPatch;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.classic.methods.HttpPut;
import org.apache.hc.client5.http.classic.methods.HttpTrace;
import org.apache.hc.client5.http.classic.methods.HttpUriRequestBase;
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
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.stream.Collectors;

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

    /** Observers. */
    protected static Map<String, ApiRequestObserver> observers = new LinkedHashMap<>();

    /** Observers. */
    protected String operationName= "n/a";

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
    private static synchronized HttpClientWrapper getInstance() {
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

    /**
     * Singleton Pattern.
     *
     * @param operation
     *      name of the operation
     * @return
     *      singleton for the class
     */
    public static synchronized HttpClientWrapper getInstance(String operation) {
        if (_instance == null) {
            _instance = new HttpClientWrapper();
            final PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager();
            connManager.setValidateAfterInactivity(TimeValue.ofSeconds(10));
            connManager.setMaxTotal(100);
            connManager.setDefaultMaxPerRoute(10);
            _instance.httpClient = HttpClients.custom().setConnectionManager(connManager).build();
        }
        _instance.operationName = operation;
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
     * Helper to build the HTTP request.
     *
     * @param url
     *      target url
     * @param token
     *      authentication token
     * @param body
     *      request body
     */
    public void PATCH(String url, String token, String body) {
        executeHttp(Method.PATCH, url, token, body, CONTENT_TYPE_JSON, false);
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

        // Execution Infos
        ApiExecutionInfos.ApiExecutionInfoBuilder executionInfo = ApiExecutionInfos.builder()
                .withOperationName(operationName)
                .withHttpRequest(req);

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
              String entity = "n/a";
              if (req.getEntity() != null) {
                  entity = EntityUtils.toString(req.getEntity());
              }
              LOGGER.error("Error for request, url={}, method={}, body={}",
                      req.getUri().toString(), req.getMethod(), entity);
              LOGGER.error("Response  code={}, body={}", res.getCode(), res.getBody());
              processErrors(res, mandatory);
              LOGGER.error("An HTTP Error occurred. The HTTP CODE Return is {}", res.getCode());
            }

            executionInfo.withHttpResponse(res);
            return res;
            // do not swallow the exception
        } catch (IllegalArgumentException | IllegalStateException e) {
            throw e;
        } catch(Exception e) {
            throw new RuntimeException("Error in HTTP Request: " + e.getMessage(), e);
        } finally {
            // Notify the observers
            CompletableFuture.runAsync(()-> notifyASync(l -> l.onRequest(executionInfo.build()), observers.values()));
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
        String body = res.getBody();
        switch(res.getCode()) {
                // 400
                case HttpURLConnection.HTTP_BAD_REQUEST:
                    throw new IllegalArgumentException("HTTP_BAD_REQUEST (code=" + res.getCode() +
                            "): Invalid Parameters " + body);
                // 401
                case HttpURLConnection.HTTP_UNAUTHORIZED:
                    throw new AuthenticationException("HTTP_UNAUTHORIZED (code=" + res.getCode() +
                            "): Invalid Credentials. Your token is invalid for target environment.");
                // 403
                case HttpURLConnection.HTTP_FORBIDDEN:
                    throw new AuthenticationException("HTTP_FORBIDDEN (code=" + res.getCode() +
                            "): Invalid permissions. Your token may not have expected permissions to perform this actions.");
                // 404    
                case HttpURLConnection.HTTP_NOT_FOUND:
                    if (mandatory) {
                        throw new IllegalArgumentException("HTTP_NOT_FOUND (code=" + res.getCode() +
                                ") Object not found:  " + body);
                    }
                break;
                // 409
                case HttpURLConnection.HTTP_CONFLICT:
                    throw new AuthenticationException("HTTP_CONFLICT (code=" + res.getCode() +
                            "): Object may already exist with same name or id " +
                            body);
                case 422:
                    throw new IllegalArgumentException("Error Code=" + res.getCode() + 
                            "(422) Invalid information provided to create DB: " 
                            + body);
                default:
                    if (res.getCode() == HttpURLConnection.HTTP_UNAVAILABLE) {
                        throw new IllegalStateException("(code=" + res.getCode() + ")" + body);
                    }
                    throw new RuntimeException(" (code=" + res.getCode() + ")" + body);
            }
    }

    /**
     * Allow to register a listener for the command.
     * @param name
     *      name of the observer
     * @param observer
     *     observer to register
     */
    public static void registerObserver(String name, ApiRequestObserver observer) {
        observers.put(name, observer);
    }

    /**
     * Allow to register a listener for the command.
     *
     * @param observers
     *     observer sto register
     */
    public static void registerObservers(Map<String, ApiRequestObserver> observers) {
        if (observers != null) {
            observers.forEach(HttpClientWrapper::registerObserver);
        }
    }

    /**
     * Register an observer with its className.
     *
     * @param observer
     *      command observer
     */
    public static void registerObserver(ApiRequestObserver observer) {
        registerObserver(observer.getClass().getSimpleName(), observer);
    }

    /**
     * Remove a listener from the command.
     *
     * @param name
     *      name of the observer
     */
    public static void unregisterObserver(String name) {
        observers.remove(name);
    }

    /**
     * Remove an observer by its class.
     *
     * @param observer
     *      observer to remove
     */
    public static void unregisterObserver(Class<ApiRequestObserver> observer) {
        unregisterObserver(observer.getSimpleName());
    }

    /**
     * Asynchronously send calls to listener for tracing.
     *
     * @param lambda
     *      operations to execute
     * @param observers
     *      list of observers to check
     *
     */
    private void notifyASync(Consumer<ApiRequestObserver> lambda, Collection<ApiRequestObserver> observers) {
        if (observers != null) {
            CompletableFutures.allDone(observers.stream()
                    .map(l -> CompletableFuture.runAsync(() -> lambda.accept(l)))
                    .collect(Collectors.toList()));
        }
    }

}
