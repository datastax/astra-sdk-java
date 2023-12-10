package com.dtsx.astra.sdk.documentation;

import com.dtsx.astra.sdk.AstraDB;
import com.dtsx.astra.sdk.AstraDBCollection;
import io.stargate.sdk.json.domain.JsonDocument;
import io.stargate.sdk.json.exception.ApiException;

import java.util.Map;

public class InsertOne {
    public static void main(String[] args) {

// Given an active db
AstraDB db = new AstraDB("<token>", "<api_endpoint>");

/*
 * Given a collection with vector (dimension 14)
 * Can be created with:
 * AstraDBCollection collection = db.createCollection("collection_vector1", 14);
 */
AstraDBCollection collection = db.collection("collection_vector1");

//  (1) You can insert records with key/value.
collection.insertOne(new JsonDocument()
 .id("doc1") // uuid is generated if not explicitely set
 .vector(new float[]{1f, 0f, 1f, 1f, 1f, 1f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f})
 .put("product_name", "HealthyFresh - Beef raw dog food")
 .put("product_price", 12.99));

// (2) You can insert records payload as a Json String
collection.insertOne(new JsonDocument()
 .data("{"
  +"   \"_id\": \"doc2\", "
  +"   \"$vector\": [1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0], "
  +"   \"product_name\": \"HealthyFresh - Chicken raw dog food\", "
  + "  \"product_price\": 9.99"
  + "}")
);

// (3) You can also insert records payload as a Map
collection.insertOne(new JsonDocument()
 .id("doc3")
 .vector(new float[]{1f, 1f, 1f, 1f, 1f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f})
 .data(Map.of("product_name", "HealthyFresh - Chicken raw dog food"))
);

// (4) All hybrid combination are possible (key/value, json, map)
collection.insertOne(new JsonDocument()
 .id("doc4")
 .vector(new float[]{1f, 1f, 1f, 1f, 1f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f})
 .data("{"
  +"   \"product_name\": \"HealthyFresh - Chicken raw dog food\", "
  + "  \"product_price\": 9.99"
  + "}")
);

// (5) You cannot insert a document with an existing id
try {
    collection.insertOne(new JsonDocument("doc4"));
} catch(ApiException e) {
    System.out.println("Expected ERROR: " + e.getMessage());
}

/*
* (5) Insertion rules
*
* - all attributes are nullable
* - id is generated if needed, the id is returned by insertOne()
* - Add any property you want, but needs to use [A-Za-z_-.] and not starting by '$'
* - It is schema less, values can be any simple type
*/
String generatedId = collection.insertOne(
        new JsonDocument().put("demo", 1));

// (6) can the payload be a bean/pojo ?
// Yes! check Object Mapping section

    }
}
