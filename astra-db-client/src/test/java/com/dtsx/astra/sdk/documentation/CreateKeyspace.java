package com.dtsx.astra.sdk.documentation;

import com.dtsx.astra.sdk.AstraDBAdmin;

public class CreateKeyspace {

    public static void main(String[] args) {
        AstraDBAdmin client = new AstraDBAdmin("TOKEN");

        // Create a Keyspace
        client.createKeyspace("<db_name>", "<keyspace_name>");
    }
}
