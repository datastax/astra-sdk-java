package com.datastax.astradb.client.documentation;

import com.datastax.astradb.client.AstraDB;

public class FindKeyspace {

    public static void main(String[] args) {
        AstraDB db = new AstraDB("TOKEN", "API_ENDPOINT");

        // List all keyspaces in the db
        db.findAllKeyspaceNames().forEach(System.out::println);

        // validate is a keyspace exists
        boolean ks = db.isKeyspaceExists("keyspace_name");

        // Show Current keyspace name
        String currentKs = db.getCurrentKeyspace();

        // Switch keyspace if needed
        db.changeKeyspace("keyspace_name");
    }
}

