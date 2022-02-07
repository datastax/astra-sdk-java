package com.datastax.stargate.sdk.cql;

import java.util.function.Supplier;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

/**
 * Configuration Supplier for the SDK.
 *
 * @author Cedrick LUNVEN (@clunven)
 */
public class DefaultSdkConfigSupplier implements Supplier<Config> {

    /** {@inheritDoc} */
    @Override
    public Config get() {
        ConfigFactory.invalidateCaches();
        return ConfigFactory.defaultOverrides()
                .withFallback(ConfigFactory.parseResources("application.conf"))
                .withFallback(ConfigFactory.parseResources("application.json"))
                .withFallback(ConfigFactory.defaultReference())
                .resolve();
    }
    

}
