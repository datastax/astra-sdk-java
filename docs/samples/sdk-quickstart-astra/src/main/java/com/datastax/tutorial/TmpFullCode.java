package com.datastax.tutorial;

import java.time.Duration;
import java.util.Arrays;

import org.apache.hc.client5.http.auth.StandardAuthScheme;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.cookie.StandardCookieSpec;
import org.apache.hc.core5.util.Timeout;

import com.datastax.astra.sdk.AstraClient;
import com.datastax.oss.driver.api.core.CqlSessionBuilder;
import com.datastax.oss.driver.api.core.config.TypedDriverOption;
import com.datastax.stargate.sdk.audit.AnsiLoggerObserver;
import com.datastax.stargate.sdk.audit.AnsiLoggerObserverLight;
import com.datastax.stargate.sdk.config.CqlSessionBuilderCustomizer;
import com.evanlennick.retry4j.config.RetryConfigBuilder;

public class TmpFullCode {
    
    public AstraClient setupAstra() {
        return AstraClient.builder()
                // Astra Credentials Settings
                .withClientId("my_client_id")
                .withClientSecret("my_client_secret")
                .withToken("my_token")
                
                // Astra DB instance Settings
                .withDatabaseId("my_db_id")
                .withDatabaseRegion("my_db_region")
                
                // Cql Settings
                .withApplicationName("SampleAPP")
                .withCqlDriverOption(TypedDriverOption.CONNECTION_CONNECT_TIMEOUT, Duration.ofSeconds(10))
                .withCqlDriverOption(TypedDriverOption.CONNECTION_INIT_QUERY_TIMEOUT, Duration.ofSeconds(10))
                .withCqlDriverOption(TypedDriverOption.CONNECTION_SET_KEYSPACE_TIMEOUT, Duration.ofSeconds(10))
                .withCqlDriverOption(TypedDriverOption.CONTROL_CONNECTION_TIMEOUT, Duration.ofSeconds(10))
                .withCqlKeyspace("quickstart")
                .withCqlSessionBuilderCustomizer(new CqlSessionBuilderCustomizer() {
                    /** {@inheritDoc} */
                    @Override
                    public void customize(CqlSessionBuilder cqlSessionBuilder) {
                        cqlSessionBuilder.withClassLoader(null);
                    }
                })
                //.withCqlMetricsRegistry(getClass())
                //.withCqlRequestTracker(null)
                //.withoutCqlSession()
                .withSecureConnectBundleFolder("~/.astra")
                
                // Http Client Settings
                .withHttpRequestConfig(RequestConfig.custom()
                        .setCookieSpec(StandardCookieSpec.STRICT)
                        .setExpectContinueEnabled(true)
                        .setConnectionRequestTimeout(Timeout.ofSeconds(5))
                        .setConnectTimeout(Timeout.ofSeconds(5))
                        .setTargetPreferredAuthSchemes(Arrays.asList(StandardAuthScheme.NTLM, StandardAuthScheme.DIGEST))
                        .build())
                .withHttpRetryConfig(new RetryConfigBuilder()
                        .retryOnAnyException()
                        .withDelayBetweenTries( Duration.ofMillis(100))
                        .withExponentialBackoff()
                        .withMaxNumberOfTries(10)
                        .build())
                .addHttpObserver("logger_light", new AnsiLoggerObserver())
                .addHttpObserver("logger_full", new AnsiLoggerObserverLight())
                
                .build();
    }

}
