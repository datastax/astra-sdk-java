package com.dtsx.astra.sdk.documentation;

import com.dtsx.astra.sdk.AstraDB;
import com.dtsx.astra.sdk.AstraDBCollection;
import io.stargate.sdk.json.domain.CollectionDefinition;
import io.stargate.sdk.json.domain.SimilarityMetric;
import io.stargate.sdk.json.exception.JsonApiException;

public class CreateCollection {
  public static void main(String[] args) {
    // Given an active db
    AstraDB db = new AstraDB("<token>", "<api_endpoint>");

    // Create a non-vector collection
    AstraDBCollection collection1 = db.createCollection("collection_simple");

    // Create a vector collection
    AstraDBCollection collection2 = db.createCollection(
        "collection_vector1",
        14,
        SimilarityMetric.cosine);

    // Create a vector collection with a builder
    AstraDBCollection collection3 = db.createCollection(CollectionDefinition
        .builder()
        .name("collection_vector2")
        .vector(1536, SimilarityMetric.euclidean)
        .build());

    // Collection names should use snake case ([a-zA-Z][a-zA-Z0-9_]*)
    try {
      db.createCollection("invalid.name");
    } catch(JsonApiException e) {
      // invalid.name is not valid
    }
  }
}
