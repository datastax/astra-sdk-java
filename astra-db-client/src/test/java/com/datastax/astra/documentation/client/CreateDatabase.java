package com.datastax.astra.documentation.client;

import com.datastax.astra.db.AstraDBClient;
import com.datastax.astra.devops.db.domain.CloudProviderType;

import java.util.UUID;

public class CreateDatabase {
  public static void main(String[] args) {
    AstraDBClient client = new AstraDBClient("TOKEN");

    // Choose a cloud provider (GCP, AZURE, AWS) and a region
    CloudProviderType cloudProvider = CloudProviderType.GCP;
    String cloudRegion = "us-east1";

    // Create a database
    UUID newDbId = client.createDatabase("DATABASE_NAME", cloudProvider, cloudRegion);
  }
}
