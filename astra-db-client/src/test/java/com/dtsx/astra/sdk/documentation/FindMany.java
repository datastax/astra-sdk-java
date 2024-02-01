package com.dtsx.astra.sdk.documentation;

import com.dtsx.astra.sdk.AstraDB;
import com.dtsx.astra.sdk.AstraDBCollection;

public class FindMany {
    public static void main(String[] args) {
        AstraDB db = new AstraDB("<token>", "<api_endpoint>");
        AstraDBCollection collection = db.createCollection("collection_vector1", 14);



    }
}
