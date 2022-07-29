/*
 * Copyright DataStax, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.datastax.astra.boot.autoconfigure;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;

import com.datastax.astra.boot.autoconfigure.AstraClientProperties.Api;
import com.datastax.astra.boot.autoconfigure.AstraClientProperties.Cql;
import com.datastax.astra.boot.autoconfigure.AstraClientProperties.DownloadSecureBundle;
import com.datastax.astra.boot.autoconfigure.AstraClientProperties.Grpc;
import com.datastax.astra.boot.autoconfigure.AstraClientProperties.Metrics;
import com.datastax.astra.boot.utils.DataStaxDriverSpringConfig;
import com.datastax.astra.boot.utils.SdkDriverConfigLoaderBuilderSpring;
import com.datastax.astra.sdk.AstraClient;
import com.datastax.astra.sdk.config.AstraClientConfig;
import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.config.DefaultDriverOption;
import com.datastax.oss.driver.api.core.metrics.DefaultSessionMetric;
import com.datastax.stargate.sdk.utils.Utils;

import io.micrometer.core.instrument.MeterRegistry;

/**
 * Initializing AstraClient (if class present in classpath)
 * - #1 Configuration with application.properties
 * - #2 Configuration with environment variables
 * - #3 Configuration with AstraRC on file system in user.home
 * 
 * You can also define your {@link AstraClient} explicitely.
 * 
 * @author Cedrick LUNVEN (@clunven)
 */
@Configuration
@ConditionalOnClass(AstraClient.class)
@EnableConfigurationProperties(AstraClientProperties.class)
public class AstraClientAutoConfiguration {
    
    /** Logger for our Client. */
    private static final Logger LOGGER = LoggerFactory.getLogger(AstraClient.class);

    /** Reference Properties. */
    @Autowired
    private AstraClientProperties astraClientProperties;
    
    @Autowired(required = false)
    private MeterRegistry microMeterMetricsRegistry;
    
    /**
     * Spring Configuration
     */
    @Autowired 
    private ConfigurableEnvironment env;
    
    /**
     * Acessing astra client.
     *
     * @return
     *      astra client
     */
    @Bean
    @ConditionalOnMissingBean
    public AstraClient astraClient() {
        LOGGER.info("Setup of AstraClient from application.yaml");
        /* 
         * Load properties and initialize the client
         */
        AstraClientConfig builder = AstraClient.builder();
        
        // API
        if (astraClientProperties.getApi()!= null) {
            LOGGER.debug("+ Api detected");
            Api api = astraClientProperties.getApi();
            if (Utils.hasLength(api.getApplicationToken())) {
                builder.withToken(api.getApplicationToken());
                LOGGER.debug("+ Api /token detected {}...", api.getApplicationToken().substring(0, 10));
            }
            if (Utils.hasLength(api.getDatabaseId())) {
                builder.withDatabaseId(api.getDatabaseId());
                LOGGER.debug("+ Api /dbId detected {}", api.getDatabaseId());
            }
            if (Utils.hasLength(api.getDatabaseRegion())) {
                builder.withDatabaseRegion(api.getDatabaseRegion());
                LOGGER.debug("+ Api /dbRegion detected {}", api.getDatabaseRegion());
            }
            if (api.getGrpc()!= null) {
                Grpc grpc = api.getGrpc();
                if (grpc.isEnabled()) {
                    LOGGER.debug("+ Grpc is enabled");
                    builder.enableGrpc();
                }
            }
        }

        // CQL
        if (astraClientProperties.getCql()!= null) {
            LOGGER.debug("+ Cql detected");
            Cql cql = astraClientProperties.getCql();

            if (cql.isEnabled()) {
                LOGGER.debug("+ Cql is enabled");
                builder.enableCql();
                // Load all properties in "astra.cql.driver-config" change keys to "datastax-java-driver" 
                Map<String, String> properties = DataStaxDriverSpringConfig.driverConfigFromSpring(env);
                LOGGER.debug("+ Keys loaded {}" , properties);
                SdkDriverConfigLoaderBuilderSpring driverConfig = new SdkDriverConfigLoaderBuilderSpring(properties);
                
                // Secure Bundle
                DownloadSecureBundle dscb = cql.getDownloadScb();
                
                if (dscb !=null && dscb.isEnabled()) {
                    LOGGER.debug("+ Secure connect bundle will be downloaded into {}", dscb.getPath());
                    builder.enableDownloadSecureConnectBundle()
                           .withCqlSecureConnectBundleFolder(dscb.getPath());
                } else {
                    LOGGER.info("+ Load Secure connect bundle locally from {}", dscb.getPath());
                    builder.disableDownloadSecureConnectBundle();
                    builder.secureConnectBundleFolder(dscb.getPath());
                }
                
                // Metrics
                Metrics metrics = cql.getMetrics();
                if (metrics !=null && metrics.isEnabled() && null != microMeterMetricsRegistry) {
                    LOGGER.debug("+ Enabling CQL Metrics through Actuator");
                    driverConfig.withString(DefaultDriverOption.METRICS_FACTORY_CLASS, "MicrometerMetricsFactory");
                    driverConfig.withStringList(DefaultDriverOption.METRICS_SESSION_ENABLED, Stream
                            .of(DefaultSessionMetric.values())
                            .map(DefaultSessionMetric::getPath)
                            .collect(Collectors.toList()));
                    driverConfig.withStringList(DefaultDriverOption.METRICS_NODE_ENABLED, Stream
                            .of(DefaultSessionMetric.values())
                            .map(DefaultSessionMetric::getPath)
                            .collect(Collectors.toList()));
                }
                
                builder.withCqlDriverConfig(driverConfig);
            }   
        }
        return builder.build();
    }
    
    /**
     * We want the CqlSession generated by {@link AstraClient}.
     * 
     * @param astraClient
     *      astraClient
     * @return
     *      the ccassandra session
     */
    @Bean
    @ConditionalOnProperty(prefix = "astra.cql", name = "enabled", havingValue = "true")
    public CqlSession cqlSession(AstraClient astraClient) {
        return astraClient.cqlSession();
    }

}
