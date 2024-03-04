package com.datastax.astradb.client.documentation;

import com.datastax.astradb.client.AstraDBAdmin;

public class CreateKeyspace {

    public static void main(String[] args) {
        AstraDBAdmin client = new AstraDBAdmin("TOKEN");

        // Create a Keyspace
        client.createKeyspace("<db_name>", "<keyspace_name>");
    }
}
