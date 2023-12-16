package com.dtsx.astra.sdk.documentation;

import com.dtsx.astra.sdk.AstraDB;

public class DeleteCollection {
  public static void main(String[] args) {
    AstraDB db = new AstraDB("<token>", "<api_endpoint>");

    // Delete an existing collection
    db.deleteCollection("collection_vector2");
  }
}
