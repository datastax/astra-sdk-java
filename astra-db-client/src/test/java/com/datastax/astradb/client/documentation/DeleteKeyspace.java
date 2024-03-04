package com.datastax.astradb.client.documentation;

import com.datastax.astradb.client.AstraDBAdmin;

public class DeleteKeyspace {

    public static void main(String[] args) {
        AstraDBAdmin client = new AstraDBAdmin("TOKEN");

        // Create a Keyspace
        client.deleteKeyspace("<db_name>", "<keyspace_name>");
    }
}
