package com.dtsx.astra.sdk;

import org.junit.jupiter.api.Test;

public class AstraDBReworkTest {

    @Test
    public void testReword() {
        AstraDBAdmin client = AstraDBClients.create("token");
        AstraDB db = client.getDatabase("sample_database");

        AstraDB db1 = new AstraDB("apiEnpoint", "token");
        AstraDBCollection collection = db.getCollection("sample_collection");


        //collection.find().all().forEach(System.out::println);
    }
}
