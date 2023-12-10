package com.dtsx.astra.sdk.documentation;
import com.dtsx.astra.sdk.AstraDB;
import com.dtsx.astra.sdk.AstraDBCollection;
import io.stargate.sdk.json.domain.CollectionDefinition;
import io.stargate.sdk.json.domain.SimilarityMetric;

public class CreateCollection {
  public static void main(String[] args) {
    // Given an active db
    AstraDB db = new AstraDB("<token>", "<api_endpoint>");

    // Create collection with no vector
    AstraDBCollection collection1 = db.createCollection("collection_simple");

    // Create collection with vector
    AstraDBCollection collection2 = db.createCollection(
            "collection_vector1",
            1536,
            SimilarityMetric.cosine);

    // Create collection with vector (builder)
    AstraDBCollection collection3 = db.createCollection(CollectionDefinition
            .builder()
            .name("collection_vector2")
            .vector(1536, SimilarityMetric.euclidean)
            .build());
  }
}
