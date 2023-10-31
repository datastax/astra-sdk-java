package com.dtsx.astra.sdk.vector;

import com.dtsx.astra.sdk.AstraDB;
import io.stargate.sdk.json.CollectionClient;
import io.stargate.sdk.json.domain.JsonDocument;
import io.stargate.sdk.json.domain.JsonResult;

import java.util.List;

public class AstraDBQuickStart {

    public static void main(String[] args) {
        // Loading Arguments
        String astraToken         = System.getenv("ASTRA_DB_APPLICATION_TOKEN");
        String astraApiEndpoint   = System.getenv("ASTRA_DB_API_ENDPOINT");
        String testCollectionName = "vector_test";

        // Initialization
        AstraDB db = new AstraDB(astraToken, astraApiEndpoint);
        db.createCollection(testCollectionName, 5);
        CollectionClient testCollection = db.collection(testCollectionName);

        // Insert
        testCollection.insertMany(List.of(
                new JsonDocument()
                        .id("1")
                        .put("name", "Coded Cleats")
                        .put("description", "ChatGPT integrated sneakers that talk to you")
                        .vector(new float[]{0.1f, 0.15f, 0.3f, 0.12f, 0.05f}),
                new JsonDocument()
                        .id("2")
                        .put("name", "Logic Layers")
                        .put("description", "An AI quilt to help you sleep forever")
                        .vector(new float[]{0.45f, 0.09f, 0.01f, 0.2f, 0.11f}),
                new JsonDocument()
                        .id("3")
                        .put("name", "Vision Vector Frame")
                        .put("description", "Vision Vector Frame - A deep learning display that controls your mood")
                        .vector(new float[]{0.1f, 0.05f, 0.08f, 0.3f, 0.6f})
        ));

        // Search
        List<JsonResult> resultsSet =
                testCollection.similaritySearch(new float[]{0.15f, 0.1f, 0.1f, 0.35f, 0.55f},10);
        resultsSet.stream().forEach(System.out::println);

    }


}
