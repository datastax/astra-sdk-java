package com.dtsx.astra.sdk.vector;

import com.dtsx.astra.sdk.db.domain.CloudProviderType;
import io.stargate.sdk.core.domain.ObjectMap;
import io.stargate.sdk.json.vector.VectorStore;

import java.util.UUID;

public class AstraVectorQuickStart {

    public void quickStartTest() {
        String databaseName = "vector_client_test";
        String astraToken = System.getenv("ASTRA_DB_APPLICATION_TOKEN");

        UUID dbId = new AstraVectorClient(astraToken)
                .createDatabase(databaseName, CloudProviderType.GCP, "us-east1");

        AstraVectorDatabaseClient vectorDb = new AstraVectorClient(astraToken)
                .database(databaseName);

       DefaultVectorStore vectorStore = vectorDb.createVectorStore("demo_product", 14);


    }
}
