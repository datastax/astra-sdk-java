package com.datastax.stargate.sdk.core;

import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;

import com.datastax.stargate.sdk.loadbalancer.Loadbalancer;
import com.datastax.stargate.sdk.utils.Assert;
import com.datastax.stargate.sdk.utils.HttpApisClient;
import com.datastax.stargate.sdk.utils.JsonUtils;

/**
 * Using the authentication endpoint you shoud be able tp...
 *
 * @author Cedrick LUNVEN (@clunven)
 *
 */
public class TokenProviderDefault implements ApiTokenProvider, ApiConstants {
    
    /** Default values for authentication. */
    public static final String DEFAULT_USERNAME      = "cassandra";
    public static final String DEFAULT_PASSWORD      = "cassandra";
    public static final String DEFAULT_AUTH_URL      = "http://localhost:8081";
    public static final int    DEFAULT_TIMEOUT_TOKEN = 300;
    
    /** Username - required all the time */
    private final String username;

    /** Password - required all the time */
    private final String password;
    
    /** Authentication token, time to live. */
    private final Duration tokenttl;
    
    /** Mark the token update. */
    private long tokenCreatedtime = 0;
    
    /** Storing an authentication token to speed up queries. */
    private String token;
    
    /** Load balancer. */
    private Loadbalancer<String> endPointAuthenticationLB;
    
    /**
     * Using defautls
     */
    public TokenProviderDefault() {
        this(DEFAULT_USERNAME, DEFAULT_PASSWORD, DEFAULT_AUTH_URL, DEFAULT_TIMEOUT_TOKEN);
    }
    
    /**
     * Overrriding credentials.
     *
     * @param username
     *      username
     * @param password
     *      password
     */
    public TokenProviderDefault(String username, String password) {
        this(username, password, DEFAULT_AUTH_URL, DEFAULT_TIMEOUT_TOKEN);
    }
    
    /**
     * Credentials and auth url customize.
     * 
     * @param username
     *      username
     * @param password
     *      password
     * @param url
     *      endpoint to authenticate.
     */
    public TokenProviderDefault(String username, String password, String... url) {
        this(username, password, Arrays.asList(url), DEFAULT_TIMEOUT_TOKEN);
    }
    
    /**
     * Credentials and auth url customize.
     * 
     * @param username
     *      username
     * @param password
     *      password
     * @param url
     *      endpoint to authenticate.
     */
    public TokenProviderDefault(String username, String password, List<String> url) {
        this(username, password, url, DEFAULT_TIMEOUT_TOKEN);
    }
    
    /**
     * Full fledge constructor.
     *
     * @param username
     *      username
     * @param password
     *      password
     * @param url
     *      endpoint to authenticate.
     * @param ttlSecs
     *      token time to live
     */
    public TokenProviderDefault(String username, String password, String url, int ttlSecs) {
        this(username, password, Collections.singletonList(url), ttlSecs);
    }
    
    /**
     * Full fledge constructor.
     *
     * @param username
     *      username
     * @param password
     *      password
     * @param url
     *      endpoint to authenticate.
     * @param ttlSecs
     *      token time to live
     */
    public TokenProviderDefault(String username, String password, List<String> url, int ttlSecs) {
        Assert.hasLength(username, "username");
        Assert.hasLength(password, "password");
        Assert.isTrue(ttlSecs>0, "time to live");
        Assert.notNull(url, "Url list shoudl not be null");
        Assert.isTrue(url.size()>0, "Url list should not be empty");
        this.username                 = username;
        this.password                 = password;
        this.tokenttl                 = Duration.ofSeconds(ttlSecs);
        this.endPointAuthenticationLB = new Loadbalancer<String>(url);
    }
    
    /**
     * Generate or renew authentication token.
     * 
     * @return String
     */
    @Override
    public String getToken() {
        if ((System.currentTimeMillis() - tokenCreatedtime) > 1000 * tokenttl.getSeconds()) {
            token = renewToken();
            tokenCreatedtime = System.currentTimeMillis();
        }
        return token;
    }

    /**
     * If token is null or too old (X seconds) renew the token.
     * 
     * @return
     */
    private String renewToken() {
        try {
            HttpPost httpPost = new HttpPost(endPointAuthenticationLB.get() + "/v1/auth");
            httpPost.addHeader(HEADER_CONTENT_TYPE, CONTENT_TYPE_JSON);
            httpPost.setEntity(new StringEntity(new StringBuilder("{")
                    .append("\"username\":").append(JsonUtils.valueAsJson(username))
                    .append(", \"password\":").append(JsonUtils.valueAsJson(password))
                    .append("}").toString()
            )); 
            try (CloseableHttpResponse response = HttpApisClient.getInstance().getHttpClient().execute(httpPost)) {
                String body = EntityUtils.toString(response.getEntity());
                EntityUtils.consume(response.getEntity());
                if (201 == response.getCode() || 200 == response.getCode()) {
                    return (String) JsonUtils.unmarshallBean(body, Map.class).get("authToken");
                } else {
                    EntityUtils.consume(response.getEntity());
                    throw new IllegalStateException("Cannot generate authentication token " + body);
                }
            }
            
        } catch(Exception e)  {
            throw new IllegalArgumentException("Cannot generate authentication token", e);
        }
    }

}
