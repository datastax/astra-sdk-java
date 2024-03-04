package com.datastax.astradb.client.v2;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.stream.Collectors;

public class AstraDataApiV2Test {

    @Test
    public void testData() throws IOException {
        try(AstraDB db = new AstraDB(
                "https://4391daae-016c-49e3-8d0a-b4633a86082c-us-east1.apps.astra.datastax.com",
                "AstraCS:iLPiNPxSSIdefoRdkTWCfWXt:2b360d096e0e6cb732371925ffcc6485541ff78067759a2a1130390e231c2c7a") {

        }
        System.out.println(db.listNamespaceNames().collect(Collectors.toList()));
    }
}
