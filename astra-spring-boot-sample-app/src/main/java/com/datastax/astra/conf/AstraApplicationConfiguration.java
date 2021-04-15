package com.datastax.astra.conf;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Repository;

import com.dstx.astra.sdk.AstraClient;

@Repository
public class AstraApplicationConfiguration implements InitializingBean {
    
    /** Logger for our Client. */
    private static final Logger LOGGER = LoggerFactory.getLogger(AstraApplicationConfiguration.class);
    
    /** hold ref to the client. */
    private AstraClient astraClient;
    
    public AstraApplicationConfiguration(AstraClient astraClient) {
        this.astraClient = astraClient;   
    }
    

    /** {@inheritDoc} */
    @Override
    public void afterPropertiesSet() throws Exception {
        LOGGER.info("Released version from Cassandra {}", astraClient.cqlSession()
                .execute("SELECT release_version FROM system.local")
                .one()  
                .getString("release_version"));
    }

}
