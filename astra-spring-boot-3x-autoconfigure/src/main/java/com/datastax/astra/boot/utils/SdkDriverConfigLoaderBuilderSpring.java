package com.datastax.astra.boot.utils;

import java.util.Map;

import com.datastax.oss.driver.internal.core.config.typesafe.DefaultDriverConfigLoader;
import com.datastax.oss.driver.internal.core.config.typesafe.DefaultProgrammaticDriverConfigLoaderBuilder;

/**
 * Default configuration builder.
 */
public class SdkDriverConfigLoaderBuilderSpring extends DefaultProgrammaticDriverConfigLoaderBuilder {

    /**
     * Load configuration as map of keys
     *
     * @param keys
     *      spring keys.
     */
    public SdkDriverConfigLoaderBuilderSpring(Map<String, String> keys) {
        super(new SdkConfigSupplierSpring(keys), DefaultDriverConfigLoader.DEFAULT_ROOT_PATH);
    }

}
