package com.dtsx.astra.sdk.documentation;
import com.dtsx.astra.sdk.AstraDB;
import io.stargate.sdk.json.domain.CollectionDefinition;

import java.util.Optional;

public class DeleteCollection {
  public static void main(String[] args) {
    // Given an active db
    //AstraDB db = new AstraDB("<token>", "<api_endpoint>");
    AstraDB db = new AstraDB(
            "AstraCS:iLPiNPxSSIdefoRdkTWCfWXt:2b360d096e0e6cb732371925ffcc6485541ff78067759a2a1130390e231c2c7a",
            "https://1537fca7-e315-4a63-8773-846bde477518-us-east1.apps.astra.datastax.com");
    // Find a collection
    db.deleteCollection("tmp_collection");
  }
}
