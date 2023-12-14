package com.dtsx.astra.sdk.documentation;
import com.dtsx.astra.sdk.AstraDB;
import io.stargate.sdk.json.domain.CollectionDefinition;

import java.util.Optional;

public class DeleteCollection {
  public static void main(String[] args) {

    // Given an active db
    AstraDB db = new AstraDB("<token>", "<api_endpoint>");

    // Find a collection
    db.deleteCollection("collection_vector2");
  }
}
