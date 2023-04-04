package com.datastax.astra.sdk.devops;

import com.datastax.astra.sdk.AstraClient;
import com.dtsx.astra.sdk.db.domain.Database;
import com.dtsx.astra.sdk.db.domain.DatabaseInfo;

/**
 * Work with Astra DB
 */
public class AstraDevopsDb {

    // Authentication
    static String ASTRA_DB_TOKEN = "<change_me>";

    public static void main(String[] args) {
        try (AstraClient astraClient = AstraClient
                .builder()
                //.withToken(ASTRA_DB_TOKEN)
                .build()) {
            astraClient.apiDevopsDatabases()
                    .findAll()
                    .map(Database::getInfo)
                    .map(DatabaseInfo::getName)
                    .forEach(System.out::println);
        }
    }
}
