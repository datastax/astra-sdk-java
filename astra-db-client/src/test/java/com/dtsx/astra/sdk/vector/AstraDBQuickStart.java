package com.dtsx.astra.sdk.vector;

import com.dtsx.astra.sdk.AstraDB;
import com.dtsx.astra.sdk.AstraDBCollection;
import io.stargate.sdk.json.CollectionClient;
import io.stargate.sdk.json.domain.JsonDocument;
import io.stargate.sdk.json.domain.JsonResult;
import io.stargate.sdk.json.domain.SelectQuery;

import java.util.List;
import java.util.stream.Stream;

public class AstraDBQuickStart {

    public static void main(String[] args) {
        // Loading Arguments
        String astraToken         = System.getenv("ASTRA_DB_APPLICATION_TOKEN");
        String astraApiEndpoint   = "https://5f4b7a96-afcf-48d4-848b-f37f4bcfdc71-us-east1.apps.astra.datastax.com";
        String testCollectionName = "vector_test";

        // Initialization
        AstraDB db = new AstraDB(astraToken, astraApiEndpoint);
        db.createCollection(testCollectionName, 5);
        AstraDBCollection testCollection = db.collection(testCollectionName);

        /* Insert
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
        ));*/
        SelectQuery query1 = SelectQuery.builder()
                //.selectSimilarity()
                .includeSimilarity()
                .orderByAnn(new float[]{0.15f, 0.1f, 0.1f, 0.35f, 0.55f})
                .withLimit(1)
                .build();
        SelectQuery query2 = SelectQuery.builder()
                .select("name")
                .orderByAnn(new float[]{0.15f, 0.1f, 0.1f, 0.35f, 0.55f})
                .withLimit(1)
                .build();
        // Search
        Stream<JsonResult> resultsSet = testCollection.findVector(query1);
        resultsSet.forEach(System.out::println);

    }

}
