package com.datastax.astra.integration;

import com.datastax.astra.devops.utils.AstraEnvironment;
import io.stargate.sdk.data.client.DataApiNamespace;
import io.stargate.sdk.data.test.integration.AbstractNamespaceITTest;

import static com.datastax.astra.AstraDBTestSupport.createDatabase;

/**
 * Run the namespace test Suite against Astra.
 */
public class AstraNamespaceITTest extends AbstractNamespaceITTest {

    @Override
    public DataApiNamespace initNamespace() {
        return createDatabase(AstraEnvironment.PROD);
    }

}
