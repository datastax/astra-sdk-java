package com.datastax.astra.db;

import io.stargate.sdk.http.HttpClientOptions;
import lombok.Data;

/**
 * Astra DB Connections.
 */
@Data
public class AstraDBOptions {

    /**
     * Http Connections.
     */
    HttpClientOptions httpClientOptions = HttpClientOptions.builder()
            .userAgentCallerName("astra-db-java")
            .userAgentCallerVersion(
                    AstraDBOptions.class.getPackage().getImplementationVersion() != null ?
                    AstraDBOptions.class.getPackage().getImplementationVersion() : "dev")
            .build();

}
