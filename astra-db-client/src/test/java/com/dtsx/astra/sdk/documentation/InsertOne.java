package com.dtsx.astra.sdk.documentation;

import com.dtsx.astra.sdk.AstraDB;
import com.dtsx.astra.sdk.AstraDBCollection;
import io.stargate.sdk.data.domain.JsonDocumentMutationResult;
import io.stargate.sdk.data.domain.JsonDocument;
import java.util.Map;

public class InsertOne {
  public static void main(String[] args) {
    AstraDB db = new AstraDB("TOKEN", "API_ENDPOINT");

    // Assumes a collection with a vector field of dimension 14
    AstraDBCollection collection = db.collection("collection_vector1");

    // You must delete any existing rows with the same IDs as the
    // rows you want to insert
    collection.deleteAll();

    // Insert rows defined by key/value
    collection.insertOne(
        new JsonDocument()
            .id("doc1") // uuid is generated if not explicitely set
            .vector(new float[]{1f, 0f, 1f, 1f, 1f, 1f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f})
            .put("product_name", "HealthyFresh - Beef raw dog food")
            .put("product_price", 12.99));
      
    // Insert rows defined as a JSON String
    collection.insertOne(
        new JsonDocument()
            .data(
                "{" +
                "\"_id\": \"doc2\", " +
                "\"$vector\": [1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0], " +
                "\"product_name\": \"HealthyFresh - Chicken raw dog food\", " +
                "\"product_price\": 9.99" +
                "}"));

    // Insert rows defined as a Map
    collection.insertOne(
        new JsonDocument()
            .id("doc3")
            .vector(new float[]{1f, 1f, 1f, 1f, 1f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f})
            .data(Map.of("product_name", "HealthyFresh - Chicken raw dog food")));

    // Insert rows defined as a combination of key/value, JSON, and Map
    collection.insertOne(
        new JsonDocument()
            .id("doc4")
            .vector(new float[]{1f, 1f, 1f, 1f, 1f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f})
            .data("{" +
                  "\"product_name\": \"HealthyFresh - Chicken raw dog food\", " +
                  "\"product_price\": 9.99" +
                  "}"));

    // If you do not provide an ID, they are generated automatically
   JsonDocumentMutationResult result = collection.insertOne(
        new JsonDocument().put("demo", 1));
   String generatedId = result.getDocument().getId();
  }
}
