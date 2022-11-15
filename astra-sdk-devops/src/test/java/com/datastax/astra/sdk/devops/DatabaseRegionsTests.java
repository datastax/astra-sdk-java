package com.datastax.astra.sdk.devops;

import com.dtsx.astra.sdk.db.DatabaseClient;
import com.dtsx.astra.sdk.db.DatabasesClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * SDK Tests against Astra.
 */
public class DatabaseRegionsTests extends AbstractDevopsApiTest {

    private String dbId = "e9ff9623-b679-406f-9916-e92b261e52a2";

    public static DatabaseClient dbClient;

    @BeforeEach
    public void initDbClient() {
        dbClient = new DatabasesClient(getToken()).database(dbId);
    }

    @Test
    public void testListRegions() {
        dbClient.regions().forEach(dc -> {
            System.out.println(dc.getRegion() + ":" + dc.getStatus());
        });
    }

    @Test
    public void testDeleteRegion() {
       dbClient.deleteRegion("centralindia");
    }

    @Test
    public void testAddRegion() {
        //dbClient.addRegion();
    }
}
