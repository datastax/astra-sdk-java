package com.dtsx.astra.sdk.documentation;

import com.dtsx.astra.sdk.AstraDB;
import com.dtsx.astra.sdk.AstraDBAdmin;
import com.dtsx.astra.sdk.AstraDBCollection;
import io.stargate.sdk.json.domain.DeleteQuery;

public class DeleteMany {
public static void main(String[] args) {

// Given an active db and a collection with a vector field (see CreateCollection.java)
AstraDB db = new AstraDB("<token>", "<api_endpoint>");

// Create collection if not exists
AstraDBCollection collection = db
  .createCollection("collection_vector1",14);

// Delete item based on a query
int deletedCount = collection
 .deleteMany(DeleteQuery.builder()
 .where("product_price").isEqualsTo(9.99)
 .build());
}
}
