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

import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.datastax.astra.sdk.AstraClient;
import com.datastax.astra.sdk.config.AstraClientConfig;
import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.config.TypedDriverOption;
import com.datastax.oss.driver.api.core.metrics.DefaultNodeMetric;
import com.datastax.oss.driver.api.core.metrics.DefaultSessionMetric;

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
public class AstraClientConfiguration {
    
    /** Logger for our Client. */
    private static final Logger LOGGER = LoggerFactory.getLogger(AstraClient.class);

    /**
     * reference to properties
     */
    @Autowired
    private AstraClientProperties astraClientProperties;
    
    @Autowired(required = false)
    private MeterRegistry microMeterMetricsRegistry;
    
    /**
     * Acessing astra client.
     *
     * @return
     *      astra client
     */
    @Bean
    @ConditionalOnMissingBean
    public AstraClient astraClient() {
        /* 
         * Load properties and initialize the client
         */
        AstraClientConfig builder = AstraClient.builder();
        
        if (null != astraClientProperties.getDatabaseId() &&
                !"".equals(astraClientProperties.getDatabaseId())) {
            builder = builder.withDatabaseId(astraClientProperties.getDatabaseId());
        }
        
        if (null != astraClientProperties.getDatabaseRegion() &&
                !"".equals(astraClientProperties.getDatabaseRegion())) {
            builder = builder.withDatabaseRegion(astraClientProperties.getDatabaseRegion());  
        }
        
        if (null != astraClientProperties.getApplicationToken() &&
                !"".equals(astraClientProperties.getApplicationToken())) {
            builder = builder.withToken(astraClientProperties.getApplicationToken());  
        }
        
        if (null != astraClientProperties.getClientId() &&
                !"".equals(astraClientProperties.getClientId())) {
            builder = builder.withClientId(astraClientProperties.getClientId());  
        }
        
        if (null != astraClientProperties.getClientSecret() &&
                !"".equals(astraClientProperties.getClientSecret())) {
            builder = builder.withClientSecret(astraClientProperties.getClientSecret());  
        }
        
        if (null != astraClientProperties.getSecureConnectBundlePath() &&
            !"".equals(astraClientProperties.getSecureConnectBundlePath())) {
            builder = builder.withSecureConnectBundleFolder(astraClientProperties.getSecureConnectBundlePath());
        }
        
        if (null != astraClientProperties.getKeyspace() &&
                !"".equals(astraClientProperties.getKeyspace())) {
            builder = builder.withCqlKeyspace(astraClientProperties.getKeyspace());  
        }
        
        if (null != microMeterMetricsRegistry) {
            LOGGER.info("+ Micrometer detected");
            if (null != astraClientProperties.getMetrics() && 
                 astraClientProperties.getMetrics().isEnabled()) {
            LOGGER.info("+ Enabling CQL Metrics through Actuator");
            builder.withCqlDriverOption(TypedDriverOption.METRICS_FACTORY_CLASS, "MicrometerMetricsFactory")
                   .withCqlDriverOption(TypedDriverOption.METRICS_SESSION_ENABLED, Stream
                           .of(DefaultSessionMetric.values())
                           .map(DefaultSessionMetric::getPath)
                           .collect(Collectors.toList()))
                   .withCqlDriverOption(TypedDriverOption.METRICS_NODE_ENABLED, Stream
                           .of(DefaultNodeMetric.values())
                           .map(DefaultNodeMetric::getPath)
                           .collect(Collectors.toList()));
            
                builder.withCqlMetricsRegistry(microMeterMetricsRegistry);
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
    public CqlSession cqlSession(AstraClient astraClient) {
        return astraClient.cqlSession();
    }
    
    //@Bean
    //public StargateClient stargateClient(AstraClient astraClient) {
    //    return astraClient.getStargateClient();
    // }

}
