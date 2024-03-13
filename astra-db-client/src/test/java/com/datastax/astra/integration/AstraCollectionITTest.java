package com.datastax.astra.integration;

import com.datastax.astra.devops.utils.AstraEnvironment;
import io.stargate.sdk.data.client.DataApiNamespace;
import io.stargate.sdk.data.test.integration.AbstractCollectionITTest;

import static com.datastax.astra.AstraDBTestSupport.createDatabase;

public class AstraCollectionITTest  extends AbstractCollectionITTest {

    @Override
    public DataApiNamespace initNamespace() {
        return createDatabase(AstraEnvironment.PROD);
    }
}
