package com.dtsx.astra.sdk.cassio;

import com.datastax.oss.driver.api.core.CqlSession;
import com.dtsx.astra.sdk.db.AstraDBOpsClient;
import com.dtsx.astra.sdk.utils.AstraEnvironment;
import io.stargate.sdk.utils.Utils;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.nio.file.Paths;
import java.util.UUID;

import static com.dtsx.astra.sdk.AstraDBAdmin.DEFAULT_KEYSPACE;

/**
 * Utility to work with CassIO and Astra
 */
@Slf4j
public class CassIO {

    private static CqlSession cqlSession;

    /**
     * Default constructor.
     */
    public CassIO() {
        Runtime.getRuntime().addShutdownHook(shutdownHook);
    }

    /**
     * Shutdown hook to close Session.
     */
    private final Thread shutdownHook = new Thread() {
        public void run() {
            if (cqlSession != null) {
                cqlSession.close();
            }
        }
    };

    /**
     * Accessing the session.
     *
     * @return
     *    the cassandra session
     */
    public static CqlSession getCqlSession() {
        if (cqlSession == null) {
            throw new IllegalStateException("CqlSession not initialized, please use init() method");
        }
        return cqlSession;
    }

    /**
     * Initialization from db is and region.
     *
     * @param cqlSession
     *      cassandra connection
     * @return
     *    the cassandra session initialized
     */
    public static synchronized CqlSession init(CqlSession cqlSession) {
        if (cqlSession == null) {
            throw new IllegalStateException("CqlSession not initialized, please use init() method");
        }
        CassIO.cqlSession = cqlSession;
        return cqlSession;
    }

    /**
     * Initialization from db is and region.
     *
     * @param dbId
     *      database identifier.
     * @param dbRegion
     *      database region,
     * @param token
     *      astra token
     * @return
     *    the cassandra session initialized
     */
    public static CqlSession init(String token, UUID dbId, String dbRegion) {
       return init(token, dbId, dbRegion, DEFAULT_KEYSPACE, AstraEnvironment.PROD);
    }

    /**
     * Initialization from db is and region.
     *
     * @param dbId
     *      database identifier.
     * @param dbRegion
     *      database region,
     * @param token
     *      astra token
     * @param keyspace
     *      destination keyspace
     * @return
     *    the cassandra session initialized
     */
    public static CqlSession init(String token, UUID dbId, String dbRegion, String keyspace) {
        return init(token, dbId, dbRegion, keyspace, AstraEnvironment.PROD);
    }

    /**
     * Initialization from db is and region.
     *
     * @param dbId
     *      database identifier.
     * @param dbRegion
     *      database region,
     * @param token
     *      astra token
     * @param keyspace
     *      destination keyspace
     * @param env
     *      destination environment
     * @return
     *    the cassandra session initialized
     */
    public static synchronized CqlSession init(String token, UUID dbId, String dbRegion, String keyspace, AstraEnvironment env) {
        String secureConnectBundleFolder = Utils
                .readEnvVariable("ASTRA_DB_SCB_FOLDER")
                .orElse(System.getProperty("user.home") + File.separator + ".astra" + File.separator + "scb");
        if (!new File(secureConnectBundleFolder).exists()) {
            if (new File(secureConnectBundleFolder).mkdirs()) {
                log.info("+ Folder Created to hold SCB {}", secureConnectBundleFolder);
            }
        }

        // Download SCB with Devops API
        AstraDBOpsClient devopsApiClient = new AstraDBOpsClient(token, env);
        devopsApiClient.database(dbId.toString()).downloadAllSecureConnectBundles(secureConnectBundleFolder);
        String scb = secureConnectBundleFolder + File.separator + "scb_" + dbId + "_" + dbRegion + ".zip";
        // Create Session
        cqlSession = CqlSession.builder()
                .withAuthCredentials("token", token)
                .withCloudSecureConnectBundle(Paths.get(scb))
                .withKeyspace(keyspace)
                .build();
        return cqlSession;
    }

    /**
     * Create a new table to store vectors.
     *
     * @param tableName
     *      table name
     * @param vectorDimension
     *      vector dimension
     * @return
     *      table to store vector
     */
    public static MetadataVectorTable metadataVectorTable(String tableName, int vectorDimension) {
        if (tableName == null || tableName.isEmpty()) throw new IllegalArgumentException("Table name must be provided");
        if (vectorDimension < 1) throw new IllegalArgumentException("Vector dimension must be greater than 0");
        return new MetadataVectorTable(
                getCqlSession(),
                cqlSession.getKeyspace().orElseThrow(() ->
                        new IllegalArgumentException("CqlSession does not select any keyspace")).asInternal(),
                tableName, vectorDimension);
    }

    public static ClusteredMetadataVectorTable clusteredMetadataVectorTable(String tableName, int vectorDimension) {
        if (tableName == null || tableName.isEmpty()) throw new IllegalArgumentException("Table name must be provided");
        if (vectorDimension < 1) throw new IllegalArgumentException("Vector dimension must be greater than 0");
        return new ClusteredMetadataVectorTable(
                getCqlSession(),
                cqlSession.getKeyspace().orElseThrow(() ->
                        new IllegalArgumentException("CqlSession does not select any keyspace")).asInternal(),
                tableName, vectorDimension);
    }
}
