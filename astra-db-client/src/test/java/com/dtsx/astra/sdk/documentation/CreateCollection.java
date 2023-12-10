package com.dtsx.astra.sdk.documentation;
import com.dtsx.astra.sdk.AstraDB;
import com.dtsx.astra.sdk.AstraDBCollection;
import io.stargate.sdk.json.domain.CollectionDefinition;
import io.stargate.sdk.json.domain.SimilarityMetric;
import io.stargate.sdk.json.exception.ApiException;
import io.stargate.sdk.json.exception.InvalidJsonApiArgumentException;

public class CreateCollection {
  public static void main(String[] args) {
    // Given an active db
    AstraDB db = new AstraDB("<token>", "<api_endpoint>");

    /*
     * Create collection with no vector.
     */
    AstraDBCollection collection1 = db.createCollection("collection_simple");

    // Create collection with vector
    AstraDBCollection collection2 = db.createCollection(
            "collection_vector1",
            14,
            SimilarityMetric.cosine);

    // Create collection with vector (builder)
    AstraDBCollection collection3 = db.createCollection(CollectionDefinition
            .builder()
            .name("collection_vector2")
            .vector(1536, SimilarityMetric.euclidean)
            .build());

    /*
     * Collection name should follow [a-zA-Z][a-zA-Z0-9_]* pattern (snake case)
     */
    try {
      db.createCollection("invalid.name");
    } catch(ApiException e) {
      // will fail
    }
  }
}
