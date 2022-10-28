package com.datastax.astra.sdk.devops;

import com.dtsx.astra.sdk.utils.AstraRc;
import com.dtsx.astra.sdk.utils.Utils;

/**
 * Superclass for test.
 */
public abstract class AbstractDevopsApiTest {

    /**
     * Read Token for tests.
     * @return
     *      token for test or error
     */
    protected String getToken() {
        String token = null;
        if (AstraRc.isDefaultConfigFileExists()) {
            token = new AstraRc()
                    .getSectionKey(AstraRc.ASTRARC_DEFAULT, AstraRc.ASTRA_DB_APPLICATION_TOKEN)
                    .orElse(null);
        }
        return Utils.readEnvVariable(AstraRc.ASTRA_DB_APPLICATION_TOKEN).orElse(token);
    }
}
