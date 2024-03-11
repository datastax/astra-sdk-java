package com.datastax.astra.documentation;

import com.datastax.astra.db.AstraDBDatabase;
import io.stargate.sdk.data.client.DataApiCollection;
import io.stargate.sdk.data.client.model.CreateCollectionOptions;
import io.stargate.sdk.data.client.model.Document;

public class FindCollection {
  public static void main(String[] args) {
    AstraDBDatabase db = new AstraDBDatabase("TOKEN", "API_ENDPOINT");

    // Find a collection
    DataApiCollection<Document> collection = db.getCollection("collection_vector1");

    // Gather collection information
    CreateCollectionOptions options = collection.getOptions();

    // Check if a collection exists
    boolean collectionExists = db.isCollectionExists("collection_vector2");
  }
}
