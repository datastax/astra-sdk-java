package com.datastax.tutorial;

import com.datastax.astra.sdk.AstraClient;
import com.datastax.stargate.sdk.StargateClient;
import com.datastax.stargate.sdk.config.StargateNodeConfig;

public class App {
    
    public static void main(String[] args) {
        try (AstraClient astraClient = configureAtraClient()) {
          // work with Stargate
        }
      }
      public static AstraClient configureAtraClient() {
        return AstraClient.builder()
                          .withDatabaseId(null)
                          .withDatabaseRegion(null)
                          .withToken(null)
                          .build();
      }
    
}
