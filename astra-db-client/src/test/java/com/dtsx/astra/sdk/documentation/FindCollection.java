package com.dtsx.astra.sdk.documentation;

import com.dtsx.astra.sdk.AstraDB;
import io.stargate.sdk.data.domain.CollectionDefinition;
import java.util.Optional;

public class FindCollection {
  public static void main(String[] args) {
    AstraDB db = new AstraDB("TOKEN", "API_ENDPOINT");

    // Find a collection
    Optional<CollectionDefinition> collection = db.findCollectionByName("collection_vector1");

    // Check if a collection exists
    boolean collectionExists = db.isCollectionExists("collection_vector2");
  }
}
