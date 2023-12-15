package com.dtsx.astra.sdk.documentation;

import com.dtsx.astra.sdk.AstraDB;
import com.dtsx.astra.sdk.AstraDBCollection;
import io.stargate.sdk.json.domain.JsonDocument;

import java.util.List;

public class InsertMany {
    public static void main(String[] args) {

// Given an active db and a collection with a vector field (see CreateCollection.java)
AstraDB db = new AstraDB("<token>", "<api_endpoint>");

// Create collection if not exists
AstraDBCollection collection = db
        .createCollection("collection_vector1",14);

// Insert documents, ids are generated here
List<String> identifiers = collection.insertMany(List.of(
  new JsonDocument()
    .vector(new float[]{1f, 0f, 1f, 1f, .5f, 1f, 0f, 0.3f, 0f, 0f, 0f, 0f, 0f, 0f})
    .put("product_name", "Yet another product")
    .put("product_price", 99.99),
  new JsonDocument()
    .vector(new float[]{1f, 0f, 1f, 1f, .5f, 1f, 0f, 0.3f, 0f, 0f, 0f, 0f, 0f, 0f})
    .put("product_name", "product3")
    .put("product_price", 99.99)));

}
}
