package com.datastax.astra.boot.utils;

import java.util.Map;
import java.util.function.Supplier;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

/**
 * Introducing application.yaml in the configuration.
 */
public class SdkConfigSupplierSpring  implements Supplier<Config> {

    /** Reference to Spring keys. */
    private final Map<String, String> springKeys;
    
    /**
     * Constructor.
     *
     * @param springKeys
     *      hold reference for keys;
     */
    public SdkConfigSupplierSpring(Map<String, String> springKeys) {
        this.springKeys = springKeys;
    }
    
    /** {@inheritDoc} */
    @Override
    public Config get() {
        ConfigFactory.invalidateCaches();
        return ConfigFactory.defaultOverrides()
                .withFallback(ConfigFactory.parseMap(springKeys, "Spring properties"))
                .withFallback(ConfigFactory.parseResources("application.conf"))
                .withFallback(ConfigFactory.parseResources("application.json"))
                .withFallback(ConfigFactory.defaultReference())
                .resolve();
    }
    

}
