package com.dtsx.astra.sdk.documentation;

import com.dtsx.astra.sdk.AstraDB;
import com.dtsx.astra.sdk.AstraDBCollection;
import io.stargate.sdk.json.domain.Filter;
import io.stargate.sdk.json.domain.JsonDocument;
import io.stargate.sdk.json.domain.JsonResult;

import java.util.Map;
import java.util.stream.Stream;

public class QuickStart {
    public static void main(String[] args) {
        // Initialization
        AstraDB myDb = new AstraDB("<token>", "<api_endpoint>");

        // Create a collection (if needed)
        AstraDBCollection demoCollection = myDb.createCollection("demo",14);

        // Insert vectors
        demoCollection.insertOne(new JsonDocument()
                .id("doc1") // generated if not set
                .vector(new float[]{1f, 0f, 1f, 1f, 1f, 1f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f})
                .put("product_name", "HealthyFresh - Beef raw dog food")
                .put("product_price", 12.99));
        demoCollection.insertOne(new JsonDocument()
                .id("doc2")
                .vector(new float[]{1f, 1f, 1f, 1f, 1f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f})
                .data("{"
                        + "   \"product_name\": \"HealthyFresh - Chicken raw dog food\", "
                        + "  \"product_price\": 9.99"
                        + "}"));
        demoCollection.insertOne(new JsonDocument()
                .id("doc3")
                .vector(new float[]{1f, 1f, 1f, 1f, 1f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f})
                .data(Map.of("product_name", "HealthyFresh - Chicken raw dog food")));
        demoCollection.insertOne(new JsonDocument()
                .id("doc4")
                .vector(new float[]{1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f})
                .put("product_name", "HealthyFresh - Chicken raw dog food")
                .put("product_price", 9.99));

        // Semantic/Vector Search
        float[] embeddings     = new float[] {1f, 1f, 1f, 1f, 1f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f};
        Filter metadataFilter = new Filter().where("product_price").isEqualsTo(9.99);
        int maxRecord = 10;
        Stream<JsonResult> resultsSet = demoCollection.findVector(embeddings, metadataFilter, maxRecord);
    }
}
