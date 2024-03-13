package com.datastax.astra.documentation;

import com.datastax.astra.db.AstraDBCollection;
import com.datastax.astra.db.AstraDBDatabase;
import io.stargate.sdk.data.client.model.Document;
import io.stargate.sdk.data.client.model.Filter;
import io.stargate.sdk.data.client.model.FindIterable;
import io.stargate.sdk.data.client.model.SimilarityMetric;
import io.stargate.sdk.data.client.model.collections.CreateCollectionOptions;

import static io.stargate.sdk.data.client.model.Filters.eq;

public class QuickStart {
  public static void main(String[] args) {

    // Connect to db
    AstraDBDatabase myDb = new AstraDBDatabase("API_ENDPOINT", "TOKEN");

    // Create collection with vector search
    AstraDBCollection<Document> demoCollection = myDb.createCollection("demo", CreateCollectionOptions
            .builder()
            .withVectorDimension(14)
            .withVectorSimilarityMetric(SimilarityMetric.cosine)
            .build());

   // Insert vectors
   demoCollection.insertOne(new Document()
           .id("doc1") // generated if not set
           .vector(new float[]{1f, 0f, 1f, 1f, 1f, 1f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f})
           .append("product_name", "HealthyFresh - Beef raw dog food")
           .append("product_price", 12.99));
    demoCollection.insertOne(new Document()
           .id("doc2")
           .vector(new float[]{1f, 1f, 1f, 1f, 1f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f})
           .append("product_name", "HealthyFresh - Chicken raw dog food")
           .append("product_price", 9.99));
    demoCollection.insertOne(new Document()
           .id("doc3")
           .vector(new float[]{1f, 1f, 1f, 1f, 1f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f}));
    demoCollection.insertOne(new Document()
           .id("doc4")
           .vector(new float[]{1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f})
           .append("product_name", "HealthyFresh - Chicken raw dog food")
           .append("product_price", 9.99));

    // Perform a similarity search
    float[] embeddings = new float[] {1f, 1f, 1f, 1f, 1f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f};
    Filter metadataFilter = new Filter().where("product_price").isEqualsTo(9.99);
    int maxRecord = 10;
    long top = System.currentTimeMillis();
    FindIterable<Document> docs = demoCollection
            .find(eq("product_price", 9.99));
    //TODO FindOptions
    System.out.println(System.currentTimeMillis() - top);

  }
}
