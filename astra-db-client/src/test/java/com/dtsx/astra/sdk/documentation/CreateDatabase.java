package com.dtsx.astra.sdk.documentation;

import com.dtsx.astra.sdk.AstraDBAdmin;
import com.dtsx.astra.sdk.db.domain.CloudProviderType;
import java.util.UUID;

public class CreateDatabase {
  public static void main(String[] args) {
    AstraDBAdmin client = new AstraDBAdmin("<token>");

    // Choose a cloud provider (GCP, AZURE, AWS) and a region
    CloudProviderType cloudProvider = CloudProviderType.GCP;
    String cloudRegion = "us-east1";

    // Create a database
    UUID newDbId = client.createDatabase("<database_name>", cloudProvider, cloudRegion);
  }
}
