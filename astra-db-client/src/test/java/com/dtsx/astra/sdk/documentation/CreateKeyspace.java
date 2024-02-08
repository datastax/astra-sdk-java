package com.dtsx.astra.sdk.documentation;

import com.dtsx.astra.sdk.AstraDBAdmin;
import com.dtsx.astra.sdk.db.domain.CloudProviderType;

import java.util.UUID;

public class CreateKeyspace {

    public static void main(String[] args) {
        AstraDBAdmin client = new AstraDBAdmin("TOKEN");

        // Create a Keyspace
        client.createKeyspace("<db_name>", "<keyspace_name>");
    }
}
