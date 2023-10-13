package com.dtsx.astra.sdk.vector.demo;

import com.dtsx.astra.sdk.vector.AstraVectorClient;
import io.stargate.sdk.core.domain.Page;
import io.stargate.sdk.json.JsonCollectionClient;
import io.stargate.sdk.json.JsonNamespaceClient;
import io.stargate.sdk.json.domain.JsonDocument;
import io.stargate.sdk.json.domain.JsonResult;
import io.stargate.sdk.json.domain.SelectQuery;
import io.stargate.sdk.json.domain.UpdateQuery;
import io.stargate.sdk.json.domain.UpdateQueryBuilder;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Demo using Json client
 */
public class VectorAsAFeatureTest {

    @Test
    public void shouldDoCollection() {

        // Token retrieved from CLI config or env var
        AstraVectorClient vectorClient = new AstraVectorClient();

        // Create Database
        UUID uuid = vectorClient.createDatabase("vector_client_test");

        // Select Database / Namespace
        JsonNamespaceClient db = vectorClient
                .database("vector_client_test")
                .namespace("default_keyspace");

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
                new JsonDocument("1")
                        .vector(new float[]{0.1f, 0.15f, 0.3f, 0.12f, 0.05f})
                        .put("name", "Coded Cleats")
                        .put("description", "ChatGPT integrated sneakers that talk to you"),
                new JsonDocument("2")
                        .vector(new float[]{0.45f, 0.09f, 0.01f, 0.2f, 0.11f})
                        .put("name", "Logic Layers")
                        .put("description", "An AI quilt to help you sleep forever"),
                new JsonDocument("3")
                        .vector(new float[]{0.1f, 0.05f, 0.08f, 0.3f, 0.6f})
                        .put("name", "Vision Vector Frame")
                        .put("description", "Vision Vector Frame - A deep learning display that controls your mood")
        ));

        // Find a document
        Optional<JsonResult> doc1 = collection.findOne(SelectQuery.builder()
                        .where("_id")
                        .isEqualsTo("4").build());
        Optional<JsonResult> doc2 = collection.findById("4");
        doc2.ifPresent(this::showResult);

        // Find documents using vector search
        Page<JsonResult> results = collection.queryForPage(
                SelectQuery.builder()
                .where("$vector").isEqualsTo(new float[]{0.15f, 0.1f, 0.1f, 0.35f, 0.55f})
                // best way to do it below
                //.orderByAnn(new float[]{0.15f, 0.1f, 0.1f, 0.35f, 0.55f})
                .limit(2)
                .build());
        System.out.println(results.getResults().get(0).getData().get("name"));
        System.out.println(results.getResults().size());
        showPage(results);

        /**
         * Find documents using vector search and projection.
         *
         * sort = {"$vector": [0.15, 0.1, 0.1, 0.35, 0.55]}
         * options = {"limit": 100}
         * projection = {"$vector": 1, "$similarity": 1}
          */
        Page<JsonResult> results2 = collection.queryForPage(
                SelectQuery.builder()
                        .orderByAnn(new float[]{0.15f, 0.1f, 0.1f, 0.35f, 0.55f})
                        .limit(2)
                        .selectVector().selectSimilarity()
                        .build());
        showPage(results2);

        /**
         * Find one document using vector search and projection
         *
         * sort = {"$vector": [0.15, 0.1, 0.1, 0.35, 0.55]}
         * projection = {"$vector": 1}
         */
        Optional<JsonResult> doc3 = collection.findOne(SelectQuery.builder()
                .orderByAnn(new float[]{0.15f, 0.1f, 0.1f, 0.35f, 0.55f})
                .selectVector()
                .build());
        doc3.ifPresent(this::showResult);

        /**
         * Find one and update with vector search
         *
         * sort = {"$vector": [0.15, 0.1, 0.1, 0.35, 0.55]}
         * update = {"$set": {"status": "active"}}
         * options = {"returnDocument": "after"}
         */
        collection.findOneAndUpdate(UpdateQuery.builder()
                .updateSet("status", "active")
                .orderByAnn(new float[]{0.15f, 0.1f, 0.1f, 0.35f, 0.55f})
                .withReturnDocument(UpdateQueryBuilder.ReturnDocument.after)
                .build());
        Optional<JsonResult> result = collection.findOne(SelectQuery.builder()
                .where("status")
                .isEqualsTo("active")
                .build());
        result.ifPresent(this::showResult);

        /**
         * Find one and replace with vector search
         *
         * sort = ({"$vector": [0.15, 0.1, 0.1, 0.35, 0.55]},)
         * replacement = {
         *         "_id": "3",
         *         "name": "Vision Vector Frame",
         *         "description": "Vision Vector Frame - A deep learning display that controls your mood",
         *         "$vector": [0.1, 0.05, 0.08, 0.3, 0.6],
         *         "status": "inactive",
         * }
         * options = {"returnDocument": "after"}
         */
        collection.findOneAndReplace(UpdateQuery.builder()
                .replaceBy(new JsonDocument("3")
                        .vector(new float[]{0.1f, 0.05f, 0.08f, 0.3f, 0.6f})
                        .put("name", "Vision Vector Frame")
                        .put("description", "Vision Vector Frame - A deep learning display that controls your mood")
                        .put("status", "inactive"))
                .orderByAnn(new float[]{0.15f, 0.1f, 0.1f, 0.35f, 0.55f})
                .withReturnDocument(UpdateQueryBuilder.ReturnDocument.after)
                .build());
        Optional<JsonResult> result2 = collection.findOne(SelectQuery.builder()
                .where("name")
                .isEqualsTo("Vision Vector Frame")
                .build());
        result2.ifPresent(this::showResult);
    }

    private void showPage(Page<JsonResult> page) {
        if (page != null) {
            System.out.println("Page size: " + page.getPageSize());
            System.out.println("Page state: " + page.getPageState());
            System.out.println("Page results: " + page.getResults().size());
            page.getResults().forEach(this::showResult);
        }
    }

    private void showResult(JsonResult r) {
        String row = r.getId() + " - ";
        if (r.getSimilarity() != null) {
            row += r.getSimilarity() + " - ";
        }
        System.out.println(row + r.getData() + " " + Arrays.toString(r.getVector()));
    }
}
