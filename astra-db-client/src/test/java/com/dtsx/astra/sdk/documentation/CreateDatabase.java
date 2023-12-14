package com.dtsx.astra.sdk.documentation;

import com.dtsx.astra.sdk.AstraDBAdmin;
import com.dtsx.astra.sdk.db.domain.CloudProviderType;

import java.util.UUID;

public class CreateDatabase {
    public static void main(String[] args) {
        AstraDBAdmin client = new AstraDBAdmin("<replace_with_token>");

        String databaseName = "<replace_with_db_name>";
        // GCP, AZURE or AWS
        CloudProviderType cloudProvider = CloudProviderType.GCP;
        // To get the list of available regions see below
        String cloudRegion = "us-east1";
        UUID newDbId = client.createDatabase(databaseName, cloudProvider, cloudRegion);
    }
}
