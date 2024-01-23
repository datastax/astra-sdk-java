package com.dtsx.astra.sdk.vector;

import com.dtsx.astra.sdk.AstraDBAdmin;
import com.dtsx.astra.sdk.AstraDBTestSuiteIT;
import io.stargate.sdk.core.domain.Page;
import io.stargate.sdk.data.CollectionClient;
import io.stargate.sdk.data.domain.JsonDocumentMutationResult;
import io.stargate.sdk.data.NamespaceClient;
import io.stargate.sdk.data.domain.JsonDocument;
import io.stargate.sdk.data.domain.JsonDocumentResult;
import io.stargate.sdk.data.domain.query.SelectQuery;
import io.stargate.sdk.data.domain.query.UpdateQuery;
import io.stargate.sdk.data.domain.query.UpdateQueryBuilder;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Demo using Json client
 */
public class AstraDBDemoTest {

    @Test
    public void shouldDoCollection() {

        // Token retrieved from CLI config or env var
        AstraDBAdmin astraDBAdmin = new AstraDBAdmin();

        // Create Database
        UUID uuid = astraDBAdmin.createDatabase(AstraDBTestSuiteIT.TEST_DBNAME);

        // Select Database / Namespace
        NamespaceClient db = astraDBAdmin
                .getDataApiClient(AstraDBTestSuiteIT.TEST_DBNAME)
                .namespace("default_keyspace");

        // Create a collection
        db.deleteCollection("collection_test");
        db.createCollection("collection_test", 5);

        // User the collection
        CollectionClient collection = db.collection("collection_test");

        // # Insert into vector collection
        collection.insertOne(new JsonDocument().id("4")
                .put("name", "Coded Cleats Copy")
                .put("description", "ChatGPT integrated sneakers that talk to you")
                .vector(new float[]{0.25f, 0.25f, 0.25f, 0.25f, 0.25f}));
        // # Insert non-vector document
        collection.insertOne(new JsonDocument().id("Cliff1")
                .put("first_name", "Cliff")
                .put("last_name", "Wicklow"));

        //  Insert Many document
        List<JsonDocumentMutationResult> docs = collection.insertManyJsonDocuments(List.of(
                new JsonDocument().id("1")
                        .vector(new float[]{0.1f, 0.15f, 0.3f, 0.12f, 0.05f})
                        .put("name", "Coded Cleats")
                        .put("description", "ChatGPT integrated sneakers that talk to you"),
                new JsonDocument().id("2")
                        .vector(new float[]{0.45f, 0.09f, 0.01f, 0.2f, 0.11f})
                        .put("name", "Logic Layers")
                        .put("description", "An AI quilt to help you sleep forever"),
                new JsonDocument().id("3")
                        .vector(new float[]{0.1f, 0.05f, 0.08f, 0.3f, 0.6f})
                        .put("name", "Vision Vector Frame")
                        .put("description", "Vision Vector Frame - A deep learning display that controls your mood")
        ));

        // Find a document
        Optional<JsonDocumentResult> doc1 = collection.findOne(SelectQuery.builder()
                        .where("_id")
                        .isEqualsTo("4").build());
        Optional<JsonDocumentResult> doc2 = collection.findById("4");
        doc2.ifPresent(this::showResult);

        // Find documents using vector search
        Page<JsonDocumentResult> results = collection.findPage(
                SelectQuery.builder()
                .where("$vector").isEqualsTo(new float[]{0.15f, 0.1f, 0.1f, 0.35f, 0.55f})
                // best way to do it below
                //.orderByAnn(new float[]{0.15f, 0.1f, 0.1f, 0.35f, 0.55f})
                .withLimit(2)
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
        Page<JsonDocumentResult> results2 = collection.findPage(
                SelectQuery.builder()
                        .orderByAnn(new float[]{0.15f, 0.1f, 0.1f, 0.35f, 0.55f})
                        .withLimit(2)
                        .includeSimilarity()
                        .build());
        showPage(results2);

        /**
         * Find one document using vector search and projection
         *
         * sort = {"$vector": [0.15, 0.1, 0.1, 0.35, 0.55]}
         * projection = {"$vector": 1}
         */
        Optional<JsonDocumentResult> doc3 = collection.findOne(SelectQuery.builder()
                .orderByAnn(new float[]{0.15f, 0.1f, 0.1f, 0.35f, 0.55f})
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
        Optional<JsonDocumentResult> result = collection.findOne(SelectQuery.builder()
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
                .replaceBy(new JsonDocument().id("3")
                        .vector(new float[]{0.1f, 0.05f, 0.08f, 0.3f, 0.6f})
                        .put("name", "Vision Vector Frame")
                        .put("description", "Vision Vector Frame - A deep learning display that controls your mood")
                        .put("status", "inactive"))
                .orderByAnn(new float[]{0.15f, 0.1f, 0.1f, 0.35f, 0.55f})
                .withReturnDocument(UpdateQueryBuilder.ReturnDocument.after)
                .build());
        Optional<JsonDocumentResult> result2 = collection.findOne(SelectQuery.builder()
                .where("name")
                .isEqualsTo("Vision Vector Frame")
                .build());
        result2.ifPresent(this::showResult);
    }

    private void showPage(Page<JsonDocumentResult> page) {
        if (page != null) {
            System.out.println("Page size: " + page.getPageSize());
            System.out.println("Page state: " + page.getPageState());
            System.out.println("Page results: " + page.getResults().size());
            page.getResults().forEach(this::showResult);
        }
    }

    private void showResult(JsonDocumentResult r) {
        String row = r.getId() + " - ";
        if (r.getSimilarity() != null) {
            row += r.getSimilarity() + " - ";
        }
        System.out.println(row + r.getData() + " " + Arrays.toString(r.getVector()));
    }
}
