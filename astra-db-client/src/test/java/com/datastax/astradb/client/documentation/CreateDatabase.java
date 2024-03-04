package com.datastax.astradb.client.documentation;

import com.datastax.astradb.client.AstraDBAdmin;
import com.dtsx.astra.sdk.db.domain.CloudProviderType;
import java.util.UUID;

public class CreateDatabase {
  public static void main(String[] args) {
    AstraDBAdmin client = new AstraDBAdmin("TOKEN");

    // Choose a cloud provider (GCP, AZURE, AWS) and a region
    CloudProviderType cloudProvider = CloudProviderType.GCP;
    String cloudRegion = "us-east1";

    // Create a database
    UUID newDbId = client.createDatabase("<database_name>", cloudProvider, cloudRegion);
  }
}
