package com.datastax.astradb.client.documentation;

import com.datastax.astradb.client.AstraDB;
import com.datastax.astradb.client.AstraCollection;

public class FindMany {
    public static void main(String[] args) {
        AstraDB db = new AstraDB("<token>", "<api_endpoint>");
        AstraCollection collection = db.createCollection("collection_vector1", 14);



    }
}
