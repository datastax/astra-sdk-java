package com.datastax.astra.sdk.devops;

import com.datastax.astra.sdk.AstraClient;
import com.dtsx.astra.sdk.org.domain.User;

public class AstraDevops {

    // Authentication
    static String ASTRA_DB_TOKEN = "<change_me>";

    public static void main(String[] args) {
        try (AstraClient astraClient = AstraClient
                .builder()
                //.withToken(ASTRA_DB_TOKEN)
                .build()) {
            astraClient.apiDevops()
                    .users()
                    .findAll()
                    .map(User::getEmail)
                    .forEach(System.out::println);
        }
    }
}
