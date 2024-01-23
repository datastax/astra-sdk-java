package com.dtsx.astra.sdk.vector;

import com.dtsx.astra.sdk.AstraDB;
import io.stargate.sdk.data.domain.CollectionDefinition;

public class AstraUIQuickStart {
    public static void main(String[] args) {
        AstraDB db = new AstraDB("<token>", "<api_endpoint>");
        System.out.println("Connected to AstraDB");
        db.findAllCollections()
          .map(CollectionDefinition::getName)
          .forEach(col -> System.out.println("Collection:" + col));
    }
}
