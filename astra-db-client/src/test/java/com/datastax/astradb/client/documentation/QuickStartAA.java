package com.datastax.astradb.client.documentation;

import com.datastax.astradb.client.AstraCollection;
import com.datastax.astradb.client.AstraDBAdmin;
import com.datastax.astradb.client.AstraDB;
import com.dtsx.astra.sdk.db.domain.CloudProviderType;
import io.stargate.sdk.data.domain.JsonDocument;
import io.stargate.sdk.data.domain.JsonDocumentResult;
import io.stargate.sdk.data.domain.query.Filter;

import java.util.List;
import java.util.stream.Stream;

public class QuickStartAA {

 public static void main(String[] args) {

   // Organization level token as describe in pre-requisites
   AstraDBAdmin astraDBAdmin = new AstraDBAdmin("<token>");

   // Create a Database if needed
   astraDBAdmin.createDatabase("quickstart", CloudProviderType.GCP, "us-east-1");

   // Accessing the database
   AstraDB myDb = astraDBAdmin.getDatabase("quickstart");

   // Create a collection
   AstraCollection demoCollection = myDb.createCollection("demo",14);

    // Insertions
    demoCollection.insertManyJsonDocuments(List.of(
      new JsonDocument()
        .id("doc1") // generated if not set
        .vector(new float[]{1f, 0f, 1f, 1f, 1f, 1f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f})
        .put("product_name", "HealthyFresh - Beef raw dog food")
        .put("product_price", 12.99),
      new JsonDocument()
        .id("doc2")
        .vector(new float[]{1f, 1f, 1f, 1f, 1f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f})
        .put("product_name", "HealthyFresh - Chicken raw dog food")
        .put("product_price", 9.99))
    );

    // Search
    float[] embeddings = new float[] {1f, 1f, 1f, 1f, 1f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f};
    Filter metadataFilter = new Filter()
            .where("product_price").isEqualsTo(9.99);
    int maxRecord = 10;
    Stream<JsonDocumentResult> resultsSet = demoCollection.
            findVector(embeddings, metadataFilter, maxRecord);
 }
}
