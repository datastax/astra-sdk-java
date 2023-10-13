package com.dtsx.astra.sdk.vector.demo;

import com.dtsx.astra.sdk.vector.AstraVectorClient;
import io.stargate.sdk.core.domain.Page;
import io.stargate.sdk.json.JsonCollectionClient;
import io.stargate.sdk.json.JsonNamespaceClient;
import io.stargate.sdk.json.domain.JsonDocument;
import io.stargate.sdk.json.domain.JsonResult;
import io.stargate.sdk.json.domain.SelectQuery;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class DemoVectorAsAFeature {

    @Test
    public void shouldDoCollection() {

        // Token retrieved from CLI config or env var
        AstraVectorClient vectorClient = new AstraVectorClient();

        // Create Database
        UUID uuid = vectorClient.createDatabase("vector_client_test");

        // Accessing the JSON CLIENT
        JsonNamespaceClient db = vectorClient
                .database("vector_client_test")
                .getJsonApiClient().namespace("default_keyspace");

        // Create a collection
        db.deleteCollection("collection_test");
        db.createCollection("collection_test", 5);

        // User the collection
        JsonCollectionClient collection = db.collection("collection_test");

        // # Insert into vector collection
        collection.insertOne("{" +
                "  \"_id\": \"4\",\n" +
                "  \"name\": \"Coded Cleats Copy\",\n" +
                "  \"description\": \"ChatGPT integrated sneakers that talk to you\",\n" +
                "  \"$vector\": [0.25, 0.25, 0.25, 0.25, 0.25]" +
                "}");

        // # Insert non-vector document
        collection.insertOne("{ " +
                "  \"_id\": \"Cliff1\",\n" +
                "  \"first_name\": \"Cliff\",\n" +
                "  \"last_name\": \"Wicklow\"}");

        //  Insert Many document
        List<String> docs = collection.insertMany(List.of(
                new JsonDocument("1", new float[]{0.1f, 0.15f, 0.3f, 0.12f, 0.05f})
                        .put("name", "Coded Cleats")
                        .put("description", "ChatGPT integrated sneakers that talk to you"),
                new JsonDocument("2", new float[]{0.45f, 0.09f, 0.01f, 0.2f, 0.11f})
                        .put("name", "Logic Layers")
                        .put("description", "An AI quilt to help you sleep forever"),
                new JsonDocument("3", new float[]{0.1f, 0.05f, 0.08f, 0.3f, 0.6f})
                        .put("name", "Vision Vector Frame")
                        .put("description", "Vision Vector Frame - A deep learning display that controls your mood")
        ));


        // Find a document
        Optional<JsonResult> doc1 = collection.findOne(SelectQuery.builder()
                        .where("_id")
                        .isEqualsTo("4").build());
        Optional<JsonResult> doc2 = collection.findById("4");
        System.out.println(doc2.get().getData().get("name"));

        // Find documents using vector search
        Page<JsonResult> results = collection.queryForPage(
                SelectQuery.builder()
                .where("$vector")
                .isEqualsTo(new float[]{0.15f, 0.1f, 0.1f, 0.35f, 0.55f})
                .limit(2)
                .build());
        System.out.println(results.getResults().get(0).getData().get("name"));
        System.out.println(results.getResults().size());

        // Find documents using vector search and projection




    }
}
