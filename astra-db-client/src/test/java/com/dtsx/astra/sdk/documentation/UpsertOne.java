package com.dtsx.astra.sdk.documentation;

import com.dtsx.astra.sdk.AstraDB;
import com.dtsx.astra.sdk.AstraDBCollection;
import io.stargate.sdk.data.domain.DocumentMutationStatus;
import io.stargate.sdk.data.domain.JsonDocument;
import io.stargate.sdk.data.domain.JsonDocumentMutationResult;
import org.junit.jupiter.api.Assertions;

import java.util.Map;

public class UpsertOne {
  public static void main(String[] args) {
    AstraDB db = new AstraDB("TOKEN", "API_ENDPOINT");

    // Assumes a collection with a vector field of dimension 14
    AstraDBCollection collection = db.collection("collection_vector1");

    // You must delete any existing rows with the same IDs as the
    // rows you want to insert
    collection.deleteAll();

    // Insert rows defined by key/value
    JsonDocument doc1 = new JsonDocument()
            .id("doc1") // uuid is generated if not explicitely set
            .vector(new float[]{1f, 0f, 1f, 1f, 1f, 1f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f})
            .put("product_name", "HealthyFresh - Beef raw dog food")
            .put("product_price", 12.99);

    // Create the document
    JsonDocumentMutationResult res1 = collection.upsertOne(doc1);
    Assertions.assertEquals(DocumentMutationStatus.CREATED, res1.getStatus());

    // No error
    JsonDocumentMutationResult res2 = collection.upsertOne(doc1);
    Assertions.assertEquals(DocumentMutationStatus.UNCHANGED, res1.getStatus());

    // Update document
    doc1.put("new_property", "value");
    collection.upsertOneASync(doc1)
              .thenAccept(res ->
      Assertions.assertEquals(DocumentMutationStatus.UPDATED, res.getStatus()));
    }
}
