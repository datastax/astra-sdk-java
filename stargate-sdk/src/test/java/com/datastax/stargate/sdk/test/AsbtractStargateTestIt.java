package com.datastax.stargate.sdk.test;

import static com.datastax.stargate.sdk.StargateClient.STARGATE_ENDPOINT_AUTH;
import static com.datastax.stargate.sdk.StargateClient.STARGATE_ENDPOINT_CQL;
import static com.datastax.stargate.sdk.StargateClient.STARGATE_ENDPOINT_GRAPHQL;
import static com.datastax.stargate.sdk.StargateClient.STARGATE_ENDPOINT_REST;
import static com.datastax.stargate.sdk.StargateClient.STARGATE_LOCAL_DC;
import static com.datastax.stargate.sdk.StargateClient.STARGATE_PASSWORD;
import static com.datastax.stargate.sdk.StargateClient.STARGATE_USERNAME;

import com.datastax.stargate.sdk.StargateClient;

public class AsbtractStargateTestIt {
    
    public static final String ANSI_RESET           = "\u001B[0m";
    public static final String ANSI_GREEN           = "\u001B[32m";
    public static final String ANSI_YELLOW          = "\u001B[33m";
    
    public static String username    = "cassandra";
    public static String password    = "cassandra";
    public static String authUrl     = "http://127.0.0.2:8081";
    public static String grapqhQlUrl = "http://127.0.0.2:8080";
    public static String restUrl     = "http://127.0.0.2:8082";
    public static String localDc     = "datacenter1";
    public static String cqlIP       = "127.0.0.2";
    
    public static StargateClient client;
    
    public static void init() {
        if (null != System.getenv(STARGATE_USERNAME) 
                && !"".equals(System.getenv(STARGATE_USERNAME))) {
            username = System.getenv(STARGATE_USERNAME);
        }
        if (null != System.getProperty(STARGATE_USERNAME) 
                && !"".equals(System.getProperty(STARGATE_USERNAME))) {
            username = System.getProperty(STARGATE_USERNAME);
        } 
        if (null != System.getenv(STARGATE_PASSWORD) 
                && !"".equals(System.getenv(STARGATE_PASSWORD))) {
            password = System.getenv(STARGATE_PASSWORD);
        }
        if (null != System.getProperty(STARGATE_PASSWORD) 
                && !"".equals(System.getProperty(STARGATE_PASSWORD))) {
            password = System.getProperty(STARGATE_PASSWORD);
        } 
        if (null != System.getenv(STARGATE_ENDPOINT_AUTH) 
                && !"".equals(System.getenv(STARGATE_ENDPOINT_AUTH))) {
            authUrl = System.getenv(STARGATE_ENDPOINT_AUTH);
        }
        if (null != System.getProperty(STARGATE_ENDPOINT_AUTH) 
                && !"".equals(System.getProperty(STARGATE_ENDPOINT_AUTH))) {
            authUrl = System.getProperty(STARGATE_ENDPOINT_AUTH);
        }
        if (null != System.getenv(STARGATE_ENDPOINT_GRAPHQL) 
                && !"".equals(System.getenv(STARGATE_ENDPOINT_GRAPHQL))) {
            grapqhQlUrl = System.getenv(STARGATE_ENDPOINT_GRAPHQL);
        }
        if (null != System.getProperty(STARGATE_ENDPOINT_GRAPHQL) 
                && !"".equals(System.getProperty(STARGATE_ENDPOINT_GRAPHQL))) {
            grapqhQlUrl = System.getProperty(STARGATE_ENDPOINT_GRAPHQL);
        }
        if (null != System.getenv(STARGATE_ENDPOINT_REST) 
                && !"".equals(System.getenv(STARGATE_ENDPOINT_REST))) {
            restUrl = System.getenv(STARGATE_ENDPOINT_REST);
        }
        if (null != System.getProperty(STARGATE_ENDPOINT_REST) 
                && !"".equals(System.getProperty(STARGATE_ENDPOINT_REST))) {
            restUrl = System.getProperty(STARGATE_ENDPOINT_REST);
        }
        if (null != System.getenv(STARGATE_LOCAL_DC) 
                && !"".equals(System.getenv(STARGATE_LOCAL_DC))) {
            localDc = System.getenv(STARGATE_LOCAL_DC);
        }
        if (null != System.getProperty(STARGATE_LOCAL_DC) 
                && !"".equals(System.getProperty(STARGATE_LOCAL_DC))) {
            localDc = System.getProperty(STARGATE_LOCAL_DC);
        }
        if (null != System.getenv(STARGATE_ENDPOINT_CQL) 
                && !"".equals(System.getenv(STARGATE_ENDPOINT_CQL))) {
            cqlIP = System.getenv(STARGATE_ENDPOINT_CQL);
        }
        if (null != System.getProperty(STARGATE_ENDPOINT_CQL) 
                && !"".equals(System.getProperty(STARGATE_ENDPOINT_CQL))) {
            cqlIP = System.getProperty(STARGATE_ENDPOINT_CQL);
        }
        
        System.out.println(ANSI_YELLOW + "-----------------------------------------" + ANSI_RESET);
        System.out.println(ANSI_YELLOW + " Stargate Document Api Integration Test" + ANSI_RESET);
        System.out.println(ANSI_YELLOW + "-----------------------------------------" + ANSI_RESET);
        
        System.out.println(ANSI_YELLOW + "\n#01 Initialization" + ANSI_RESET);
        client = StargateClient.builder()
                .username(username)
                .password(password)
                .endPointAuth(authUrl)
                .endPointGraphQL(grapqhQlUrl)
                .endPointGraphQL(restUrl)
                .cqlContactPoint(cqlIP, 9042).localDc(localDc)
                .build();
        System.out.println(ANSI_GREEN + "[OK]" + ANSI_RESET + " - Stargate Client is up.");
    }

}
