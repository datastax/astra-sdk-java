package com.datastax.astradb.client;

import com.datastax.astradb.client.v2.AstraDBClients;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public class AstraDatabaseReworkTest {

    @Test
    public void testRework() {
        try(AstraDBAdmin astraDBAdmin = AstraDBClients.create("token");) {
            AstraDB database = astraDBAdmin.getDatabase("sample_mflix");
            AstraCollection collection = database.getCollection("movies");

            //AstraDatabase db1 = new AstraDatabase("apiEnpoint", "token");
            //AstraDBCollection collection = db.getCollection("sample_collection");
            //collection.insertOne(Document.builder().put("title", "Star Wars").build());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        //collection.find().all().forEach(System.out::println);
    }
}
