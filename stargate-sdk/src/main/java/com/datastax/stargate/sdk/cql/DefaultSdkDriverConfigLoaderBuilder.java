package com.datastax.stargate.sdk.cql;

import com.datastax.oss.driver.internal.core.config.typesafe.DefaultDriverConfigLoader;
import com.datastax.oss.driver.internal.core.config.typesafe.DefaultProgrammaticDriverConfigLoaderBuilder;

/**
 * Default configuration duilder.
 *
 * @author Cedrick LUNVEN (@clunven)
 */
public class DefaultSdkDriverConfigLoaderBuilder extends DefaultProgrammaticDriverConfigLoaderBuilder {
    
    /**
     * Constructor.
     */
    public DefaultSdkDriverConfigLoaderBuilder() {
        super(new DefaultSdkConfigSupplier(), DefaultDriverConfigLoader.DEFAULT_ROOT_PATH);
    }

}
