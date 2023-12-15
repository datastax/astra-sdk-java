package com.dtsx.astra.sdk.documentation;

import com.dtsx.astra.sdk.AstraDB;
import io.stargate.sdk.json.domain.CollectionDefinition;
import java.util.Optional;

public class FindCollection {
  public static void main(String[] args) {
    AstraDB db = new AstraDB("<token>", "<api_endpoint>");

    // Find a collection
    Optional<CollectionDefinition> collection = db.findCollection("collection_vector1");

    // Verify if a collection exists
    boolean collectionExists = db.isCollectionExists("collection_vector2");
  }
}
