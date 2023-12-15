package com.dtsx.astra.sdk.documentation;

import com.dtsx.astra.sdk.AstraDB;
import com.dtsx.astra.sdk.AstraDBCollection;

public class ClearCollection {
    public static void main(String[] args) {

// Accessing existing DB
AstraDB db = new AstraDB("<token>", "<api_endpoint>");

// Access existing collection
AstraDBCollection collection = db.createCollection("collection_vector1", 14);

// Clear collection
collection.deleteAll();

    }
}
