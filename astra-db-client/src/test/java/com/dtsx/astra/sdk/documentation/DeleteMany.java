package com.dtsx.astra.sdk.documentation;

import com.dtsx.astra.sdk.AstraDB;
import com.dtsx.astra.sdk.AstraDBCollection;
import io.stargate.sdk.data.domain.query.DeleteQuery;
import io.stargate.sdk.data.domain.query.DeleteResult;
import io.stargate.sdk.data.domain.query.Filter;
import io.stargate.sdk.http.domain.FilterOperator;

import static io.stargate.sdk.http.domain.FilterOperator.EQUALS_TO;

public class DeleteMany {
  public static void main(String[] args) {
    AstraDB db = new AstraDB("TOKEN", "API_ENDPOINT");
    AstraDBCollection collection = db.createCollection("collection_vector1", 14);

    // Build our query
    DeleteQuery deleteQuery = DeleteQuery.builder()
      .where("product_price", EQUALS_TO, 9.99)
      .build();

    // Deleting only up to 20 record
    DeleteResult page = collection
            .deleteManyPaged(deleteQuery);

    // Deleting all documents matching query
    DeleteResult allDeleted = collection
            .deleteMany(deleteQuery);

    // Deleting all documents matching query in distributed way
    DeleteResult result = collection
            .deleteManyChunked(deleteQuery, 5);
  }
}
