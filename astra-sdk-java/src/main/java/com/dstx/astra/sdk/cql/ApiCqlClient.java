package com.dstx.astra.sdk.cql;

import java.io.File;
import java.net.InetSocketAddress;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.CqlSessionBuilder;
import com.datastax.oss.driver.api.core.config.DriverConfigLoader;
import com.dstx.astra.sdk.devops.ApiDevopsClient;

/**
 * Wrapper for Java driver using CqlSession.
 *
 * @author Cedrick LUNVEN (@clunven)
 *
 */
public class ApiCqlClient {

    /** Logger for our Client. */
    private static final Logger LOGGER = LoggerFactory.getLogger(ApiCqlClient.class);
    
    private static final String SANITY_QUERY = "SELECT data_center from system.local";
    
    /** Username - required all the time */
    private final String username;
    
    /** Password - required all the time */
    private final String password;
    
    /**
     * Use can provide external fully fledge application.conf 
     */
    private String applicationConf;
    
    /**
     * If provided not try to download the secure bundle when using CQL.
     */
    private  String cloudSecureBundle;
    
    /**
     * If provided use this to create some CqlSession.
     */
    private  String[] contactPoints;
    
    /** Keep instance reference. */
    private static CqlSession cqlSession;
    
    /**
     * Optional devops client. Could be relevant to download a secure connect bundle
     */
    private ApiDevopsClient devops;
    
    /**
     * Optional devops client. Could be relevant to download a secure connect bundle
     */
    private String dbId;
    
    /**
     * Using Astra
     */
    public ApiCqlClient(String username, String pwd, String cloudSecureBundle) {
        this(null,username, pwd, cloudSecureBundle, null, null, null);
    }
    
    /**
     * Using Stargate CQL, no need for secure bundle
     */
    public ApiCqlClient(String username, String pwd, List<String> contactPoint) {
        this(null,username, pwd, null, contactPoint, null, null);
    }
    
    /**
     * Full Constructor, contactPoints first then ecure bundle
     *
     * applicationConfigFile overrride all
     * then username + password + secureConnectBundlePath
     * then username + password + contactPoints
     * devops client can be useful to download secure cloud bundle
     */
    public ApiCqlClient(String applicationConf, String username, String pwd, String cloudSecureBundle, List<String> contactPoints, ApiDevopsClient devopsClient, String astraDbId) {
        this.applicationConf    = applicationConf; 
        this.username           = username;
        this.password           = pwd;
        this.cloudSecureBundle  = cloudSecureBundle;
        this.devops             = devopsClient;
        this.dbId               = astraDbId;
        
        // Check settings
        initCqlSession();
    }
    
    private void initCqlSession() {
        if (null != applicationConf && !"".equals(applicationConf)) {
            LOGGER.info("A configuration file has been provided using only this");
            DriverConfigLoader loader = DriverConfigLoader.fromFile(new File(applicationConf));
            cqlSession = CqlSession.builder().withConfigLoader(loader).build();
        } else {
            // Start with auth
            CqlSessionBuilder builder = CqlSession.builder()
                    .withAuthCredentials(username, password);
            // CloudBundle has priority
            if (null != cloudSecureBundle && new File(cloudSecureBundle).canRead()) {
                builder.withCloudSecureConnectBundle(Paths.get(cloudSecureBundle));
            // Then contact points
            } else if (null != contactPoints && contactPoints.length >0) {
                Arrays.stream(contactPoints)
                      .map(this::mapContactPoint)
                      .forEach(builder::addContactPoint);
            } else if (null != devops && null!=dbId) {
                // No contact points, no cloudbundle 
                // but with a devops API we can download the secure bundle
                // FIXME download securebundle
            } else {
                throw new IllegalStateException("you must provide either CloudSecureBundle "
                        + "or Contact points to initiate a cqlSession");
            }
            cqlSession = builder.build();
        }
    }
    
    private InetSocketAddress mapContactPoint(String contactPoint) {
        String[] chunks = contactPoint.split(":");
        if (chunks.length != 2) {
            throw new IllegalArgumentException(contactPoint 
                    + " is not a valid contactPoint expression: invalid format,expecting ip:port");
        }
        int port = 0;
        try {
            port = Integer.parseInt(chunks[1]);
            if (port <0 || port > 65536) {
                throw new IllegalArgumentException(contactPoint 
                        + " is not a valid contactPoint expression: port should be in [0,65536] range");
            }
        } catch (Exception e) {
            throw new IllegalArgumentException(contactPoint 
                    + " is not a valid contactPoint expression: port invalid expecting ip:port");
        }
        return new InetSocketAddress(chunks[0], port);
    }
    
    /**
     * Can we perform query with CQL
     */
    public boolean testConnection() {
        return (null != cqlSession().execute(SANITY_QUERY).one());
    }

    /**
     * Select a keyspace
     */
    public void useKeyspace(String keyspace) {
        cqlSession().execute("USE " + keyspace);
    }
    
    /**
     * Accessing the CQL Sesssion
     */
    public CqlSession cqlSession() {
        if (cqlSession == null) {
            initCqlSession();
        }
        return cqlSession;
    }
    
    
}
