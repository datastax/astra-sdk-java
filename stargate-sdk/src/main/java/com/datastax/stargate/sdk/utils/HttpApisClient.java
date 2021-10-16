package com.datastax.stargate.sdk.utils;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.stream.Collectors;

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
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.Method;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.util.TimeValue;
import org.apache.hc.core5.util.Timeout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.datastax.oss.driver.internal.core.util.concurrent.CompletableFutures;
import com.datastax.stargate.sdk.audit.ApiInvocationEvent;
import com.datastax.stargate.sdk.audit.ApiInvocationObserver;
import com.datastax.stargate.sdk.core.ApiConstants;
import com.datastax.stargate.sdk.core.ApiResponseHttp;
import com.datastax.stargate.sdk.exception.AuthenticationException;
import com.datastax.stargate.sdk.loadbalancer.UnavailableResourceException;
import com.evanlennick.retry4j.CallExecutorBuilder;
import com.evanlennick.retry4j.Status;
import com.evanlennick.retry4j.config.RetryConfig;
import com.evanlennick.retry4j.config.RetryConfigBuilder;

/**
 * Wrapping the HttpClient and provide helpers
 * 
 * @author Cedrick LUNVEN (@clunven)
 */
public class HttpApisClient implements ApiConstants {
    
    /** Logger for our Client. */
    private static final Logger LOGGER = LoggerFactory.getLogger(HttpApisClient.class);
    
    /** Default settings in Request and Retry */
    public static final int DEFAULT_TIMEOUT_REQUEST   = 20;
    
    /** Default settings in Request and Retry */
    public static final int DEFAULT_TIMEOUT_CONNECT   = 20;
    
    /** Default settings in Request and Retry */
    public static final int DEFAULT_RETRY_COUNT       = 3;
    
    /** Default settings in Request and Retry */
    public static final Duration DEFAULT_RETRY_DELAY  = Duration.ofMillis(100);
    
    // -------------------------------------------
    // ----------------   Settings  --------------
    // -------------------------------------------
    
    /** Singleton pattern. */
    private static HttpApisClient _instance = null;
    
    /** HttpComponent5. */
    protected CloseableHttpClient httpClient = null;
    
    /** Observers. */
    protected static Map<String, ApiInvocationObserver > apiInvocationsObserversMap = new ConcurrentHashMap<>();
    
    /** Default request configuration. */
    protected static RequestConfig requestConfig = RequestConfig.custom()
            .setCookieSpec(StandardCookieSpec.STRICT)
            .setExpectContinueEnabled(true)
            .setConnectionRequestTimeout(Timeout.ofSeconds(DEFAULT_TIMEOUT_REQUEST))
            .setConnectTimeout(Timeout.ofSeconds(DEFAULT_TIMEOUT_CONNECT))
            .setTargetPreferredAuthSchemes(Arrays.asList(StandardAuthScheme.NTLM, StandardAuthScheme.DIGEST))
            .build();
    
    /** Default retry configuration. */
    protected static RetryConfig retryConfig = new RetryConfigBuilder()
            //.retryOnSpecificExceptions(ConnectException.class, IOException.class)
            .retryOnAnyException()
            .withDelayBetweenTries(DEFAULT_RETRY_DELAY)
            .withExponentialBackoff()
            .withMaxNumberOfTries(DEFAULT_RETRY_COUNT)
            .build();
    
    /**
     * Update Retry configuration of the HTTPClient.
     *
     * @param conf
     *      retryConfiguration
     */
    public static void withRetryConfig(RetryConfig conf) {
        retryConfig= conf;
    }
    
    /**
     * Update RequestConfig configuration of the HTTPClient.
     *
     * @param conf
     *      RequestConfig
     */
    public static void withRequestConfig(RequestConfig conf) {
        requestConfig = conf;
    }
    
    /**
     * Register a new listener.
     *
     * @param name
     *      current name
     * @param listener
     *      current listener
     */
    public static void registerListener(String name, ApiInvocationObserver listener) {
        apiInvocationsObserversMap.put(name, listener);
    }
    
    // -------------------------------------------
    // ----------------- Singleton ---------------
    // -------------------------------------------
    
    /**
     * Hide default constructor
     */
    private HttpApisClient() {}
    
    /**
     * Singleton Pattern.
     * 
     * @return
     *      singleton for the class
     */
    public static synchronized HttpApisClient getInstance() {
        if (_instance == null) {
            _instance = new HttpApisClient();
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
        return executeHttp(Method.GET, url, token, null, false);
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
        return executeHttp(Method.HEAD, url, token, null, false);
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
        return executeHttp(Method.POST, url, token, null, true);
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
        return executeHttp(Method.POST, url, token, body, true);
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
    public ApiResponseHttp DELETE(String url, String token) {
        return executeHttp(Method.DELETE, url, token, null, true);
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
    public ApiResponseHttp PUT(String url, String token, String body) {
        return executeHttp(Method.PUT, url, token, body, false);
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
    public ApiResponseHttp PATCH(String url, String token, String body) {
        return executeHttp(Method.PATCH, url, token, body, true);
    }
    
    /**
     * Main Method executting HTTP Request.
     * 
     * @param method
     *      http method
     * @param url
     *      url
     * @param token
     *      authentication token
     * @param reqBody
     *      request body
     * @param mandatory
     *      allow 404 errors
     * @return
     *      basic request
     */
    public ApiResponseHttp executeHttp(final Method method, final String url, final String token, String reqBody, boolean mandatory) {
        return executeHttp(buildRequest(method, url, token, reqBody), mandatory);
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
     *      
     */
    public ApiResponseHttp executeHttp(HttpUriRequestBase req, boolean mandatory) {
     // Initializing the invocation event
        ApiInvocationEvent event = new ApiInvocationEvent(req);
        // Invoking the expected endpoint
        Status<CloseableHttpResponse> status = executeWithRetries(req);
        try {
            // Parsing result as expected bean
            ApiResponseHttp res = mapResponse(status, event);
            // Error managment
            if (HttpURLConnection.HTTP_NOT_FOUND == res.getCode() && !mandatory) {
                return res;
            }
            if (res.getCode() >= 300) {
              LOGGER.error("Error for request [{}], url={}, method={}, code={}, body={}", 
                      event.getRequestId(), 
                      req.getUri().toString(), req.getMethod(),
                      res.getCode(), res.getBody());
              processErrors(res, mandatory);
            }
            return res;
        } catch (UnavailableResourceException e) {
            event.setErrorClass(e.getClass().getName());
            event.setErrorMessage(e.getMessage());
            throw e;
        } catch (IllegalArgumentException e) {
            event.setErrorClass(e.getClass().getName());
            event.setErrorMessage(e.getMessage());
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            event.setErrorClass(e.getClass().getName());
            event.setErrorMessage(e.getMessage());
            throw new RuntimeException("Error in HTTP Request", e);
        } finally {
            CompletableFuture.runAsync(()-> notifyAsync(listener->listener.onCall(event)));
        }
    }
    
    
    
    /**
     * Mapping HTTP Response to framework HTTP BEAN.
     *
     * @param status
     *      current result of the retries
     * @param event
     *      event to be sent
     * @return
     *      bean populated
     * @throws ParseException
     *      error in parsing
     * @throws IOException
     *      error in accessing payload
     */
    private ApiResponseHttp mapResponse(Status<CloseableHttpResponse> status, ApiInvocationEvent event)
    throws ParseException, IOException {
        // Evaluate output
        ApiResponseHttp res = null;
        event.setTotalTries(status.getTotalTries());
        event.setLastException(status.getLastExceptionThatCausedRetry());
        event.setResponseElapsedTime(status.getTotalElapsedDuration().toMillis());
        try (CloseableHttpResponse response = status.getResult()) {
            event.setResponseTimestamp(status.getEndTime());
            if (response == null) {
                event.setResponseCode(HttpURLConnection.HTTP_UNAVAILABLE);
                res = new ApiResponseHttp("Response is empty, cannot contact endpoint, please check url", 
                        HttpURLConnection.HTTP_UNAVAILABLE, null);
            } else {
                event.setResponseCode(response.getCode());
                Map<String, String > headers = new HashMap<>();
                Arrays.asList(response.getHeaders())
                          .stream()
                          .forEach(h -> headers.put(h.getName(), h.getValue()));
                event.setResponseHeaders(headers);
    
                // Parse body if present
                String body = null;
                if (null != response.getEntity()) {
                     body = EntityUtils.toString(response.getEntity());
                     EntityUtils.consume(response.getEntity());
                }
                event.setResponseBody(body);
            
                // Mapping respoonse
                res = new ApiResponseHttp(body, response.getCode(), headers);
            }
        }
        return res;
    }
    
    /**
     * Asynchronously send calls to listener for tracing.
     *
     * @param lambda
     *      operations to execute
     * @return
     *      void
     */
    private CompletionStage<Void> notifyAsync(Consumer<ApiInvocationObserver> lambda) {
        return CompletableFutures.allDone(apiInvocationsObserversMap.values().stream()
                .map(l -> CompletableFuture.runAsync(() -> lambda.accept(l)))
                .collect(Collectors.toList()));
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
    private HttpUriRequestBase buildRequest(final Method method, final String url, final String token, String body) {
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
        req.addHeader(HEADER_CONTENT_TYPE, CONTENT_TYPE_JSON);
        req.addHeader(HEADER_ACCEPT, CONTENT_TYPE_JSON);
        req.addHeader(HEADER_USER_AGENT, REQUEST_WITH);
        req.addHeader(HEADER_REQUEST_ID, UUID.randomUUID().toString());
        req.addHeader(HEADER_REQUESTED_WITH, REQUEST_WITH);
        req.addHeader(HEADER_CASSANDRA, token);
        req.addHeader(HEADER_AUTHORIZATION, "Bearer " + token);
        if (null != body) {
            req.setEntity(new StringEntity(body));
        }
        return req;
    }
    
    /**
     * Implementing retries.
     *
     * @param req
     *      current request
     * @return
     *      the closeable response
     */
    @SuppressWarnings("unchecked")
    private Status<CloseableHttpResponse> executeWithRetries(ClassicHttpRequest req) {
        Callable<CloseableHttpResponse> executeRequest = () -> {
            return httpClient.execute(req);
        };
        return new CallExecutorBuilder<String>()
                .config(retryConfig)
                .onSuccessListener(s -> {
                    LOGGER.debug("Call successFull");
                })
                .onCompletionListener(s -> {
                    LOGGER.debug("Call completed in in {} millis.", s.getTotalElapsedDuration().get(ChronoUnit.NANOS)/1000000);
                })
                .onFailureListener(s -> {
                    LOGGER.error("Calls failed after {} retries",
                            s.getTotalTries());
                })
                .afterFailedTryListener(s -> {
                    LOGGER.error("Failure on attempt {}/{} ",
                            s.getTotalTries(), retryConfig.getMaxNumberOfTries());
                })
                .build()
                .execute(executeRequest);
    }
    
    /**
     * Process ERRORS.Anything above code 300 can be marked as an error Still something
     * 404 is expected and should not result in throwing expection (=not find)
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
                            ", (HTTP_CONFLICT) Object may alreayd exist with same identifiers: " + 
                            res.getBody());                
                case 422:
                    throw new IllegalArgumentException("Error Code=" + res.getCode() + 
                            "(422) Invalid information provided to create DB: " 
                            + res.getBody());
                default:
                    if (res.getCode() == HttpURLConnection.HTTP_UNAVAILABLE) {
                        throw new UnavailableResourceException(res.getBody() + " (http:" + res.getCode() + ")");
                    }
                    throw new RuntimeException(res.getBody() + " (http:" + res.getCode() + ")");
            }
    }

}
