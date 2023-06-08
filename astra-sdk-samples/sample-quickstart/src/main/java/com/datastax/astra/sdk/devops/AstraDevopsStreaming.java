package com.datastax.astra.sdk.devops;

import com.datastax.astra.sdk.AstraClient;

import java.util.List;
import java.util.Map;

/**
 * Getting Started with Astra Devops Streaming
 */
public class AstraDevopsStreaming {

    // Authentication
    static String ASTRA_DB_TOKEN = "<change_me>";

    public static void main(String[] args) {
        try (AstraClient astraClient = AstraClient
                .builder()
                //.withToken(ASTRA_DB_TOKEN)
                .build()) {
            Map<String, List<String>> clouds = astraClient.apiDevopsStreaming().providers().findAll();
            System.out.println("+ Available Clouds to create tenants=" + clouds);
        }
    }
}
