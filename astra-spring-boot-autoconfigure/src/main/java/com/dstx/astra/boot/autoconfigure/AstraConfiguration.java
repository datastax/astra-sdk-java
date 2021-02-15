package com.dstx.astra.boot.autoconfigure;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.dstx.astra.sdk.AstraClient;

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
public class AstraConfiguration {

    @Autowired
    private AstraClientProperties astraClientProperties;
    
    @Bean
    @ConditionalOnMissingBean
    public AstraClient astraClient() {
        
        AstraClientProperties p;
        return AstraClient.builder().build();
    }
    
   
    

}
