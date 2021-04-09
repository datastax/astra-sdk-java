package com.datastax.astra.conf;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Repository;

import com.dstx.astra.sdk.AstraClient;

import io.stargate.sdk.rest.DataCenter;

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
       
        //if (!astraClient.apiRest().keyspace("sample").exist()) {
        //    LOGGER.info("Creating keyspace'sample' as it does not exists");
        //    astraClient.apiRest().keyspace("sample").create(new DataCenter(astraClient.get));
        //}
        
        //SchemaBuilder.createTable(USER_TABLENAME)
        //.ifNotExists()
        //.withPartitionKey(USER_EMAIL, DataTypes.TEXT)
        //.withColumn(USER_FIRSTNAME, DataTypes.TEXT)
        //.withColumn(USER_LASTNAME, DataTypes.TEXT)
        //.build()
        /*
        
        CREATE TABLE IF NOT EXISTS starter_orders (
                order_id uuid,
                product_id uuid,
                product_quantity int,
                product_name text,
                product_price decimal,
                added_to_order_at timestamp,
                PRIMARY KEY ((order_id), product_id)
               ) WITH CLUSTERING ORDER BY (product_id DESC);
               
        */
        
    }
    
   

}
