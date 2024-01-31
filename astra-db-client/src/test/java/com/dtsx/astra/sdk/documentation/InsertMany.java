package com.dtsx.astra.sdk.documentation;

import com.dtsx.astra.sdk.AstraDB;
import com.dtsx.astra.sdk.AstraDBCollection;
import io.stargate.sdk.data.domain.JsonDocumentMutationResult;
import io.stargate.sdk.data.domain.JsonDocument;
import java.util.List;

public class InsertMany {
  public static void main(String[] args) {
    AstraDB db = new AstraDB("TOKEN", "API_ENDPOINT");
    AstraDBCollection collection = db.createCollection("collection_vector1",14);

    // Insert documents into the collection (IDs are generated automatically)
    List<JsonDocumentMutationResult> identifiers = collection.insertManyJsonDocuments(List.of(
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
