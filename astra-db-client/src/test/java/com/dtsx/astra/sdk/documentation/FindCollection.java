package com.dtsx.astra.sdk.documentation;
import com.dtsx.astra.sdk.AstraDB;
import io.stargate.sdk.json.domain.CollectionDefinition;

import java.util.Optional;

public class FindCollection {
  public static void main(String[] args) {
    // Given an active db
    AstraDB db = new AstraDB("<token>", "<api_endpoint>");

    // Find a collection
    Optional<CollectionDefinition> collection = db.findCollection("collection1");

    // Another test with a collection that does not exist
    boolean collectionExists = db.isCollectionExists("collection1");
  }
}
