package com.dtsx.astra.sdk;

import io.stargate.sdk.json.domain.CollectionDefinition;

public class Documentation {
    public static void listCollections() {
        // Given an active db
        AstraDB db = new AstraDB("<token>", "<api_endpoint>");

        db.findAllCollections().forEach(col -> {
            System.out.println("name=" + col.getName());
            if (col.getOptions() != null && col.getOptions().getVector() != null) {
                CollectionDefinition.Options.Vector vector = col.getOptions().getVector();
                System.out.println("dim=" + vector.getDimension());
                System.out.println("metric=" + vector.getMetric());
            }
        });
    }
}


// [...]


