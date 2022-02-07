package com.datastax.astra.boot.utils;

import java.util.Map;
import java.util.function.Supplier;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

/**
 * Introducing application.yaml in the configuration.
 *
 * @author Cedrick LUNVEN (@clunven)
 */
public class SdkConfigSupplierSpring  implements Supplier<Config> {

    /** Reference to Spring keys. */
    private Map<String, String> sprinKeys;
    
    /**
     * Constructor.
     *
     * @param springkeys
     *      hold reference to keys;
     */
    public SdkConfigSupplierSpring(Map<String, String> springkeys) {
        this.sprinKeys = springkeys;
    }
    
    /** {@inheritDoc} */
    @Override
    public Config get() {
        ConfigFactory.invalidateCaches();
        return ConfigFactory.defaultOverrides()
                .withFallback(ConfigFactory.parseMap(sprinKeys, "Spring properties"))
                .withFallback(ConfigFactory.parseResources("application.conf"))
                .withFallback(ConfigFactory.parseResources("application.json"))
                .withFallback(ConfigFactory.defaultReference())
                .resolve();
    }
    

}
