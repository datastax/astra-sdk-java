package com.datastax.astra;

import com.datastax.astra.db.AstraDBClient;
import com.datastax.astra.db.AstraDBDatabase;
import com.datastax.astra.devops.db.domain.CloudProviderType;
import com.datastax.astra.devops.utils.ApiLocator;
import com.datastax.astra.devops.utils.AstraEnvironment;
import io.stargate.sdk.utils.Utils;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

/**
 * This class will help us generate database or select the environment
 * we are targeting.
 */
@Slf4j
public class AstraDBTestSupport {

    /**
     * Test Constants
     */
    public static final String DATABASE_NAME = "astra_db_client";

    public static AstraDBClient getAstraDBClient(AstraEnvironment env) {
        switch (env) {
            case DEV:
                return new AstraDBClient(Utils.readEnvVariable("ASTRA_DB_APPLICATION_TOKEN_DEV")
                        .orElseThrow(() -> new IllegalStateException("Please define env variable 'ASTRA_DB_APPLICATION_TOKEN_DEV'")));
            case PROD:
                return new AstraDBClient(Utils.readEnvVariable("ASTRA_DB_APPLICATION_TOKEN")
                        .orElseThrow(() -> new IllegalStateException("Please define env variable 'ASTRA_DB_APPLICATION_TOKEN'")));
            case TEST:
                return new AstraDBClient(Utils.readEnvVariable("ASTRA_DB_APPLICATION_TOKEN_TEST")
                        .orElseThrow(() -> new IllegalStateException("Please define env variable 'ASTRA_DB_APPLICATION_TOKEN_TEST'")));
            default:
                throw new IllegalArgumentException("Invalid Environment");
         }
    }

    public static AstraDBDatabase createDatabase(AstraEnvironment env) {
        CloudProviderType cloud;
        String region;
        switch (env) {
            case DEV:
            case TEST:
                cloud = CloudProviderType.GCP;
                region = "europe-west4";
                break;
            case PROD:
                cloud = CloudProviderType.GCP;
                region = "us-east1";
                break;
            default:
                throw new IllegalArgumentException("Invalid Environment");
        }
        return createDatabase(env, cloud, region);
    }

    public static AstraDBDatabase createDatabase(AstraEnvironment env, CloudProviderType cloud, String region) {
        log.info("Working in environment '{}'", env.name());
        AstraDBClient client = getAstraDBClient(env);
        UUID databaseId =  client.createDatabase(DATABASE_NAME, cloud, region);
        log.info("Working with api Endpoint '{}'", ApiLocator.getApiJsonEndpoint(env, databaseId.toString(), region));
        return client.getDatabase(databaseId);
    }


}
