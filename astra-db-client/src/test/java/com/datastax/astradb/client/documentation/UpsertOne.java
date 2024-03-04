package com.datastax.astradb.client.documentation;

import com.datastax.astradb.client.AstraCollection;
import com.datastax.astradb.client.AstraDB;
import io.stargate.sdk.data.domain.JsonDocument;
import io.stargate.sdk.data.domain.JsonDocumentMutationResult;
import org.junit.jupiter.api.Assertions;

import static io.stargate.sdk.data.domain.DocumentMutationStatus.CREATED;
import static io.stargate.sdk.data.domain.DocumentMutationStatus.UNCHANGED;
import static io.stargate.sdk.data.domain.DocumentMutationStatus.UPDATED;

public class UpsertOne {
  public static void main(String[] args) {
    AstraDB db = new AstraDB("TOKEN", "API_ENDPOINT");

    // Assumes a collection with a vector field of dimension 14
    AstraCollection collection = db.getCollection("collection_vector1");

    // Insert rows defined by key/value
    JsonDocument doc1 = new JsonDocument()
            .id("doc1") // uuid is generated if not explicitely set
            .put("product_name", "HealthyFresh - Beef raw dog food")
            .put("product_price", 12.99);

    // Create the document
    JsonDocumentMutationResult res1 = collection.upsertOne(doc1);
    Assertions.assertEquals(CREATED, res1.getStatus());

    // Nothing happened
    JsonDocumentMutationResult res2 = collection.upsertOne(doc1);
    Assertions.assertEquals(UNCHANGED, res1.getStatus());

    // Document is updated (async)
    doc1.put("new_property", "value");
    collection.upsertOneASync(doc1).thenAccept(res ->
      Assertions.assertEquals(UPDATED, res.getStatus()));
    }
}
