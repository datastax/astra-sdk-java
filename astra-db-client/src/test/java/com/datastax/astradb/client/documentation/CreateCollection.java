package com.datastax.astradb.client.documentation;

import com.datastax.astradb.client.AstraCollection;
import com.datastax.astradb.client.AstraDB;
import io.stargate.sdk.data.domain.CollectionDefinition;
import io.stargate.sdk.data.domain.SimilarityMetric;
import io.stargate.sdk.data.exception.DataApiException;

public class CreateCollection {
  public static void main(String[] args) {
    AstraDB db = new AstraDB("TOKEN", "API_ENDPOINT");

    // Create a non-vector collection
    AstraCollection collection1 = db.createCollection("collection_simple");

    // Create a vector collection
    AstraCollection collection2 = db.createCollection(
        "collection_vector1",
        14,
        SimilarityMetric.cosine);

    // Create a vector collection with a builder
    AstraCollection collection3 = db.createCollection(CollectionDefinition
        .builder()
        .name("collection_vector2")
        .vector(1536, SimilarityMetric.euclidean)
        .build());

    // Collection names should use snake case ([a-zA-Z][a-zA-Z0-9_]*)
    try {
      db.createCollection("invalid.name");
    } catch(DataApiException e) {
      // invalid.name is not valid
    }
  }
}
