package com.datastax.stargate.sdk.utils;

import java.net.HttpURLConnection;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.apache.hc.client5.http.auth.StandardAuthScheme;
import org.apache.hc.client5.http.classic.methods.HttpDelete;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPatch;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.classic.methods.HttpPut;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.cookie.StandardCookieSpec;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.util.TimeValue;
import org.apache.hc.core5.util.Timeout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.datastax.stargate.sdk.core.ApiConstants;
import com.datastax.stargate.sdk.core.ApiResponseHttp;
import com.datastax.stargate.sdk.core.ApiTokenProvider;
import com.datastax.stargate.sdk.core.TokenProviderStatic;
import com.datastax.stargate.sdk.exception.AuthenticationException;

/**
 * Wrapping the HttpClient and provide helpers
 * 
 * @author Cedrick LUNVEN (@clunven)
 */
public class HttpApisClient implements ApiConstants {
    
    /** Logger for our Client. */
    private static final Logger LOGGER = LoggerFactory.getLogger(HttpApisClient.class);
    
    /** default params */
    public static final int DEFAULT_TIMEOUT_REQUEST   = 20;
    public static final int DEFAULT_TIMEOUT_CONNECT   = 20;
    
    /** Singleton pattern. */
    private static HttpApisClient _instance = null;
    
    /** This the endPoint to invoke to work with different API(s). */
    private int connectionRequestTimeout = DEFAULT_TIMEOUT_REQUEST;
    
    /** This the endPoint to invoke to work with different API(s). */
    private int connectionTimeout = DEFAULT_TIMEOUT_CONNECT;
    
    /** default request config. */
    protected RequestConfig requestConfig = null;
    
    /** HhtpComponent5. */
    protected CloseableHttpClient httpClient = null;
    
    /** Working with the token. */
    private ApiTokenProvider tokenProvider;
    
    /** If enabled more logs. */
    private boolean verbose = false;
     
    /**
     * Hide default constructor
     */
    private HttpApisClient() {}
    
    /**
     * Singleton Pattern.
     */
    public static synchronized HttpApisClient getInstance() {
        if (_instance == null) {
            _instance = new HttpApisClient();
            _instance.requestConfig = RequestConfig.custom()
                    .setCookieSpec(StandardCookieSpec.STRICT)
                    .setExpectContinueEnabled(true)
                    .setConnectionRequestTimeout(Timeout.ofSeconds(_instance.connectionRequestTimeout))
                    .setConnectTimeout(Timeout.ofSeconds(_instance.connectionTimeout))
                    .setTargetPreferredAuthSchemes(Arrays.asList(StandardAuthScheme.NTLM, StandardAuthScheme.DIGEST))
                    .build();
            final PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager();
            connManager.setValidateAfterInactivity(TimeValue.ofSeconds(10));
            connManager.setMaxTotal(100);
            connManager.setDefaultMaxPerRoute(10);
            _instance.httpClient = HttpClients.custom().setConnectionManager(connManager).build();
            LOGGER.info("+ HttpClient Initialized");
        }
        return _instance;
    }
    
    /**
     * Debugging HTTP CALLS.
     *
     * @param bool
     *      response
     */
    public void setVerbose(boolean bool) {
        this.verbose = true;
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
        httpGet.addHeader(HEADER_CONTENT_TYPE, CONTENT_TYPE_JSON);
        httpGet.addHeader(HEADER_ACCEPT, CONTENT_TYPE_JSON);
        httpGet.addHeader(HEADER_USER_AGENT, REQUEST_WITH);
        httpGet.addHeader(HEADER_REQUESTED_WITH, REQUEST_WITH);
        httpGet.addHeader(HEADER_CASSANDRA, getInstance().tokenProvider.getToken());
        httpGet.addHeader(HEADER_AUTHORIZATION, "Bearer " + getInstance().tokenProvider.getToken());
        return executeHttp(httpGet, false);
    }
    
    public ApiResponseHttp POST(String url) {
        HttpPost httpPost = new HttpPost(url);
        httpPost.addHeader(HEADER_CONTENT_TYPE, CONTENT_TYPE_JSON);
        httpPost.addHeader(HEADER_ACCEPT, CONTENT_TYPE_JSON);
        httpPost.addHeader(HEADER_USER_AGENT, REQUEST_WITH);
        httpPost.addHeader(HEADER_REQUESTED_WITH, REQUEST_WITH);
        httpPost.addHeader(HEADER_CASSANDRA, getInstance().tokenProvider.getToken());
        httpPost.addHeader(HEADER_AUTHORIZATION, "Bearer " + getInstance().tokenProvider.getToken());
        return executeHttp(httpPost, true);
    }
    
    public ApiResponseHttp POST(String url, String body) {
        HttpPost httpPost = new HttpPost(url);
        httpPost.addHeader(HEADER_CONTENT_TYPE, CONTENT_TYPE_JSON);
        httpPost.addHeader(HEADER_ACCEPT, CONTENT_TYPE_JSON);
        httpPost.addHeader(HEADER_USER_AGENT, REQUEST_WITH);
        httpPost.addHeader(HEADER_REQUESTED_WITH, REQUEST_WITH);
        httpPost.addHeader(HEADER_CASSANDRA, getInstance().tokenProvider.getToken());
        httpPost.addHeader(HEADER_AUTHORIZATION, "Bearer " + getInstance().tokenProvider.getToken());
        httpPost.setEntity(new StringEntity(body));
        return executeHttp(httpPost, true);
    }
    
    public ApiResponseHttp DELETE(String url) {
        HttpDelete httpDelete = new HttpDelete(url);
        httpDelete.addHeader(HEADER_CONTENT_TYPE, CONTENT_TYPE_JSON);
        httpDelete.addHeader(HEADER_ACCEPT, CONTENT_TYPE_JSON);
        httpDelete.addHeader(HEADER_USER_AGENT, REQUEST_WITH);
        httpDelete.addHeader(HEADER_REQUESTED_WITH, REQUEST_WITH);
        httpDelete.addHeader(HEADER_CASSANDRA, getInstance().tokenProvider.getToken());
        httpDelete.addHeader(HEADER_AUTHORIZATION, "Bearer " + getInstance().tokenProvider.getToken());
        return executeHttp(httpDelete, true);
    }
    
    public ApiResponseHttp PUT(String url,  String body) {
        HttpPut httpPut = new HttpPut(url);
        httpPut.addHeader(HEADER_CONTENT_TYPE, CONTENT_TYPE_JSON);
        httpPut.addHeader(HEADER_USER_AGENT, REQUEST_WITH);
        httpPut.addHeader(HEADER_ACCEPT, CONTENT_TYPE_JSON);
        httpPut.addHeader(HEADER_REQUESTED_WITH, REQUEST_WITH);
        httpPut.addHeader(HEADER_CASSANDRA, getInstance().tokenProvider.getToken());
        httpPut.addHeader(HEADER_AUTHORIZATION, "Bearer " + getInstance().tokenProvider.getToken());
        httpPut.setEntity(new StringEntity(body));
        return executeHttp(httpPut, false);
    }
    
    public ApiResponseHttp PATCH(String url,  String body) {
        HttpPatch httpPatch = new HttpPatch(url);
        httpPatch.addHeader(HEADER_CONTENT_TYPE, CONTENT_TYPE_JSON);
        httpPatch.addHeader(HEADER_ACCEPT, CONTENT_TYPE_JSON);
        httpPatch.addHeader(HEADER_USER_AGENT, REQUEST_WITH);
        httpPatch.addHeader(HEADER_REQUESTED_WITH, REQUEST_WITH);
        httpPatch.addHeader(HEADER_CASSANDRA, getInstance().tokenProvider.getToken());
        httpPatch.addHeader(HEADER_AUTHORIZATION, "Bearer " + getInstance().tokenProvider.getToken());
        httpPatch.setEntity(new StringEntity(body));
        return executeHttp(httpPatch, true);
    }
    
    /**
     * Main Method executting HTTP Request.
     *
     * @param req
     *      http request
     * @return
     */
    private ApiResponseHttp executeHttp(ClassicHttpRequest req, boolean mandatory) {
        if (verbose) {
            LOGGER.info("Executing {} on {}", req.getMethod(),req.getPath());
            Arrays.asList(req.getHeaders()).stream().map(Header::toString).forEach(LOGGER::info);
        }
        ApiResponseHttp res = null;
        try (CloseableHttpResponse response = getHttpClient().execute(req)) {
            
            Map<String, String > headers = new HashMap<>();
            Arrays.asList(response.getHeaders())
                  .stream()
                  .forEach(h -> headers.put(h.getName(), h.getValue()));
            
            // Parse body if present
            String body = null;
            if (null != response.getEntity()) {
                body = EntityUtils.toString(response.getEntity());
                if (verbose) {
                    LOGGER.info("Response {} on {}", response.getCode(),body);
                } 
                EntityUtils.consume(response.getEntity());
            }
            
            if (verbose) {
                LOGGER.error("+ request_method={}", req.getMethod());
                LOGGER.error("+ request_url={}", req.getUri().toString());
                LOGGER.error("+ response_code={}", response.getCode());
                LOGGER.error("+ response_body={}", body);
            }
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
            e.printStackTrace();
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalArgumentException("Error in HTTP Request", e);
        }
    }
    
    public CloseableHttpClient getHttpClient() {
        return httpClient;
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

}
