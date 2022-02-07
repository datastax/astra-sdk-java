package com.datastax.astra.boot.utils;

import java.util.Map;

import com.datastax.oss.driver.internal.core.config.typesafe.DefaultDriverConfigLoader;
import com.datastax.oss.driver.internal.core.config.typesafe.DefaultProgrammaticDriverConfigLoaderBuilder;

/**
 * Default configuration duilder.
 *
 * @author Cedrick LUNVEN (@clunven)
 */
public class SdkDriverConfigLoaderBuilderSpring extends DefaultProgrammaticDriverConfigLoaderBuilder {
    
    
    /**
     * Load configuration.
     *
     * @param keys
     *      spring keys.
     */
    public SdkDriverConfigLoaderBuilderSpring(Map<String, String> keys) {
        super(new SdkConfigSupplierSpring(keys), DefaultDriverConfigLoader.DEFAULT_ROOT_PATH);
    }

}
