package com.dtsx.astra.sdk;

import com.dtsx.astra.sdk.db.domain.CloudProviderType;
import com.dtsx.astra.sdk.utils.AstraEnvironment;
import io.stargate.sdk.data.domain.odm.Document;
import io.stargate.sdk.utils.Utils;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class BreakAstraTest {

    public static final String TEST_DBNAME = "astra_db_client";

    static CloudProviderType targetCloud = AstraDBAdmin.FREE_TIER_CLOUD;
    static String targetRegion = AstraDBAdmin.FREE_TIER_CLOUD_REGION;
    static String astraToken = Utils.readEnvVariable("ASTRA_DB_APPLICATION_TOKEN").get();

    static AstraDBAdmin astraDbAdmin;
    static AstraDB astraDb;
    static UUID databaseId;
    static AstraDBCollection collectionSimple;

    @Test
    public void testQueryBust() {
        astraDbAdmin = new AstraDBAdmin(astraToken, AstraEnvironment.PROD);
        databaseId       = astraDbAdmin.createDatabase(TEST_DBNAME, targetCloud, targetRegion);
        astraDb = astraDbAdmin.getDatabase(databaseId);
        collectionSimple = astraDb.createCollection("collection_vector", 1536);
        collectionSimple.deleteAll();
        List<Document<AstraDBTestSuiteIT.Product>> documents = new ArrayList<>();

        // Create an instance of Random
        Random random = new Random();
        int size = 1536;
        float[] fakeEmbeddings = new float[size];
        for(int i = 0; i < size; i++) {
            fakeEmbeddings[i] = random.nextFloat();
        }

        long start = System.currentTimeMillis();
        int nbDocs = 200000;
        for (int i = 0; i < nbDocs; i++) {
            documents.add(new Document<AstraDBTestSuiteIT.Product>()
                    .id(String.valueOf(i))
                    .vector(fakeEmbeddings)
                    .data(new AstraDBTestSuiteIT.Product("Desc " + i, i * 1.0d)));
        }
        collectionSimple.insertManyChunked(documents, 20, 200);
    }
}
