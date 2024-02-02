package com.dtsx.astra.sdk.documentation;

import com.dtsx.astra.sdk.AstraDB;
import com.dtsx.astra.sdk.AstraDBCollection;

public class ClearCollection {
  public static void main(String[] args) {
    AstraDB db = new AstraDB("TOKEN", "API_ENDPOINT");
    AstraDBCollection collection = db.createCollection("collection_vector1", 14);

    // Delete all rows from an existing collection
    collection.deleteAll();
  }
}
