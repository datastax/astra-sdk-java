package com.datastax.stargate.sdk.utils;

import java.net.HttpURLConnection;
import java.time.Duration;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
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
import org.apache.hc.client5.http.classic.methods.HttpUriRequestBase;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.cookie.StandardCookieSpec;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.core5.http.ClassicHttpRequest;
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
import com.datastax.stargate.sdk.core.ApiTokenProvider;
import com.datastax.stargate.sdk.core.TokenProviderStatic;
import com.datastax.stargate.sdk.exception.AuthenticationException;
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
    public static final int DEFAULT_TIMEOUT_CONNECT   = 20;
    public static final int DEFAULT_RETRY_COUNT       = 3;
    public static final Duration DEFAULT_RETRY_DELAY  = Duration.ofMillis(100);
    
    /** . */
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
    
    /** Singleton pattern. */
    private static HttpApisClient _instance = null;
    
    /** HhtpComponent5. */
    protected CloseableHttpClient httpClient = null;
    
    /** Working with the token. */
    private ApiTokenProvider tokenProvider;
    
    /**
     * Update Retry configuration of the HTTPClient.
     *
     * @param conf
     *      retryConfiguration
     */
    public static void withRetryConfig(RetryConfig conf) {
        retryConfig= conf;
    }
    
    public static void withRequestConfig(RequestConfig conf) {
        requestConfig = conf;
    }
    
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
    
    public void setTokenProvider(ApiTokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
    }
    
    public void setToken(String token) {
        this.tokenProvider = new TokenProviderStatic(token);
    }
    
    public String getToken() {
        return tokenProvider.getToken();
    }
    
    // -------------------------------------------
    // ---------- Working with HTTP --------------
    // -------------------------------------------
    
    public ApiResponseHttp GET(String url) {
        HttpGet httpGet = new HttpGet(url);
        addHeaders(httpGet);
        return executeHttp(httpGet, false);
    }
    
    public ApiResponseHttp HEAD(String url) {
        HttpHead httpHead = new HttpHead(url);
        addHeaders(httpHead);
       return executeHttp(httpHead, false);
    }
    
    public ApiResponseHttp POST(String url) {
        HttpPost httpPost = new HttpPost(url);
        addHeaders(httpPost);
        return executeHttp(httpPost, true);
    }
    
    public ApiResponseHttp POST(String url, String body) {
        HttpPost httpPost = new HttpPost(url);
        addHeaders(httpPost);
        httpPost.setEntity(new StringEntity(body));
        return executeHttp(httpPost, true);
    }
    
    public ApiResponseHttp DELETE(String url) {
        HttpDelete httpDelete = new HttpDelete(url);
        addHeaders(httpDelete);
        return executeHttp(httpDelete, true);
    }
    
    public ApiResponseHttp PUT(String url,  String body) {
        HttpPut httpPut = new HttpPut(url);
        addHeaders(httpPut);
        httpPut.setEntity(new StringEntity(body));
        return executeHttp(httpPut, false);
    }
    
    public ApiResponseHttp PATCH(String url,  String body) {
        HttpPatch httpPatch = new HttpPatch(url);
        addHeaders(httpPatch);
        httpPatch.setEntity(new StringEntity(body));
        return executeHttp(httpPatch, true);
    }
    
    private void addHeaders(HttpUriRequestBase req) {
        req.addHeader(HEADER_CONTENT_TYPE, CONTENT_TYPE_JSON);
        req.addHeader(HEADER_ACCEPT, CONTENT_TYPE_JSON);
        req.addHeader(HEADER_USER_AGENT, REQUEST_WITH);
        req.addHeader(HEADER_REQUEST_ID, UUID.randomUUID().toString());
        req.addHeader(HEADER_REQUESTED_WITH, REQUEST_WITH);
        req.addHeader(HEADER_CASSANDRA, getInstance().tokenProvider.getToken());
        req.addHeader(HEADER_AUTHORIZATION, "Bearer " + getInstance().tokenProvider.getToken());
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
    public Status<CloseableHttpResponse> executeWithRetries(ClassicHttpRequest req) {
        Callable<CloseableHttpResponse> executeRequest = () -> {
            return getHttpClient().execute(req);
        };
        return new CallExecutorBuilder<String>()
                .config(retryConfig)
                .onSuccessListener(s -> {
                    // No need to log anything on success
                    //System.out.println("Success!");
                    //System.out.println("Status: " + s);
                })
                .onCompletionListener(s -> {
                    
                    System.out.println("Retry execution complete!");
                })
                .onFailureListener(s -> {
                    System.out.println("Failed!");
                })
                .afterFailedTryListener(s -> {
                    System.out.println("Try failed! Will try again in 250ms.");
                })
                .beforeNextTryListener(s -> {
                    System.out.println("Trying again...");
                })
                .build()
                .execute(executeRequest);
    }
    
    /**
     * Main Method executting HTTP Request.
     *
     * @param req
     *      http request
     * @return
     */
    private ApiResponseHttp executeHttp(ClassicHttpRequest req, boolean mandatory) {
        // Invoke with Retries (status get a lot of insights
        System.out.println("OK");
        ApiInvocationEvent event = new ApiInvocationEvent(req);
        Status<CloseableHttpResponse> status = executeWithRetries(req);
        // Evaluate output
        ApiResponseHttp res = null;
       
        event.setTotalTries(status.getTotalTries());
        event.setLastException(status.getLastExceptionThatCausedRetry());
        event.setResponseElapsedTime(status.getTotalElapsedDuration().toMillis());
        try (CloseableHttpResponse response = status.getResult()) {
            event.setResponseTimestamp(status.getEndTime());
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
            if (HttpURLConnection.HTTP_NOT_FOUND == res.getCode() && !mandatory) {
                return res;
            }
            if (res.getCode() >= 300) {
              LOGGER.error("HTTP ERROR:");
              LOGGER.error("+ request_method={}", req.getMethod());
              LOGGER.error("+ request_url={}", req.getUri().toString());
              LOGGER.error("+ response_code={}", res.getCode());
              LOGGER.error("+ response_body={}", res.getBody());
              processErrors(res, mandatory);
            }
            return res;
        } catch (IllegalArgumentException e) {
            event.setErrorClass(IllegalArgumentException.class.getName());
            event.setErrorMessage(e.getMessage());
            throw e;
        } catch (Exception e) {
            event.setErrorClass(Exception.class.getName());
            event.setErrorMessage(e.getMessage());
            throw new IllegalArgumentException("Error in HTTP Request", e);
        } finally {
            CompletableFuture.runAsync(()-> notifyAsync(listener->listener.onCall(event)));
        }
    }
    
    /**
     * Retrieve HttpClient from the instance.
     *
     * @return
     *      httpclient
     */
    public CloseableHttpClient getHttpClient() {
        return httpClient;
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
    
    /**
     * Retrieve a listener from its name.
     *
     * @param name
     *      listener name
     * @return
     *      get listener
     */
    public Optional<ApiInvocationObserver> getListener(String name) {
        return Optional.ofNullable(apiInvocationsObserversMap.get(name));
    }
    
    /**
     * Unregister a listener for the calls.
     *
     * @param name
     *          listener name
     */
    public void unRegisterListener(String name) {
        if (apiInvocationsObserversMap.containsKey(name)) {
            apiInvocationsObserversMap.remove(name);
        }
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
                    throw new RuntimeException("Error Code=" + res.getCode() + 
                    "Internal ERROR: " + res.getBody());
            }
    }
    
    public CompletionStage<Void> notifyAsync(Consumer<ApiInvocationObserver> lambda) {
        return CompletableFutures.allDone(apiInvocationsObserversMap.values().stream()
                .map(l -> CompletableFuture.runAsync(() -> lambda.accept(l)))
                .collect(Collectors.toList()));
    }
   

}
