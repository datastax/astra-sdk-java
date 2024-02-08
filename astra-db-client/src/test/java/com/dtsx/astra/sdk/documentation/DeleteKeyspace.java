package com.dtsx.astra.sdk.documentation;

import com.dtsx.astra.sdk.AstraDBAdmin;

public class DeleteKeyspace {

    public static void main(String[] args) {
        AstraDBAdmin client = new AstraDBAdmin("TOKEN");

        // Create a Keyspace
        client.deleteKeyspace("<db_name>", "<keyspace_name>");
    }
}
